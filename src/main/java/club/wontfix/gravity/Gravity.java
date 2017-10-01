package club.wontfix.gravity;

import club.wontfix.gravity.bootstrap.StartupOptions;
import club.wontfix.gravity.database.Database;
import club.wontfix.gravity.database.impl.MariaDatabase;
import club.wontfix.gravity.events.ConsoleInputEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.virtlink.commons.configuration2.jackson.JsonConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.*;
import org.apache.commons.configuration2.builder.DefaultReloadingDetectorFactory;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.Application;
import ro.pippo.core.Pippo;
import ro.pippo.core.route.CSRFHandler;
import ro.pippo.core.route.PublicResourceHandler;
import ro.pippo.core.route.WebjarsResourceHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

@MetaInfServices
public class Gravity extends Application {

    @Getter(lazy = true)
    private static final Gravity instance = new Gravity();

    @Getter(lazy = true)
    private final Logger logger = LoggerFactory.getLogger(Gravity.class);

    @Getter @Setter
    private CommandLine cmdArgs;

    @Getter @Setter
    private JsonConfiguration config;

    @Getter @Setter
    private Scanner consoleScanner;

    @Getter @Setter
    private Database database;

    @Getter
    private EventBus eventBus = new EventBus();

    @Getter @Setter
    private Pippo pippo;

    public static void main(String[] args) {
        getInstance().getLogger().info("Starting Gravity...");

        Options options = new StartupOptions().getOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            getInstance().setCmdArgs(parser.parse(options, args));
        } catch (ParseException ex) {
            formatter.printHelp("argument", options);
            return;
        }

        String configPath = getInstance().getCmdArgs().getOptionValue("config");
        if(configPath == null) {
            configPath = "config.json";
        }

        File file = new File(Paths.get(".").toAbsolutePath().normalize().toFile(), configPath);
        if(!file.exists()) {
            try {
                Files.copy(Gravity.class.getResourceAsStream("/config.json"), file.toPath());
            } catch (IOException ex) {
                getInstance().getLogger().error("Could not copy config.json into current directory.");
                return;
            }
        }

        try {
            getInstance().setConfig(new FileBasedConfigurationBuilder<>(JsonConfiguration.class)
                    .configure(new Parameters().properties()
                            .setFileName(configPath)
                            .setThrowExceptionOnMissing(true)
                            .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
                            .setReloadingDetectorFactory(new DefaultReloadingDetectorFactory())
                            .setEncoding("UTF-8"))
                    .getConfiguration());
        } catch (ConfigurationException ex) {
            getInstance().getLogger().error("Could not load config file.", ex);
            return;
        }

        String address = getInstance().getCmdArgs().getOptionValue("address");
        if(address == null) {
            try {
                address = getInstance().getConfig().getString("server.address");
            } catch (NoSuchElementException ex) {
                getInstance().getLogger().warn("Could not find a specified Server Address in the config.json file." +
                        "Using default value \"127.0.0.1\". Do not ship like this!", ex);
                address = "127.0.0.1";
            }
        }

        int port = -1;
        try {
            String cmdPort = getInstance().getCmdArgs().getOptionValue("port");
            if(cmdPort != null) {
                port = Integer.parseInt(cmdPort);
            }
        } catch (NumberFormatException ex) {
            getInstance().getLogger().error("Could not parse \"{0}\" to a valid port (int).",
                    getInstance().getCmdArgs().getOptionValue("port"));
            getInstance().getLogger().debug("NumberFormatException (Gravity::main)", ex);
            return;
        }
        if(port == -1) {
            port = getInstance().getConfig().getInt("server.port");
        }

        String mode = getInstance().getCmdArgs().getOptionValue("mode");
        if(mode == null) {
            mode = getInstance().getConfig().getString("server.mode");
        }

        switch (mode.toLowerCase()) {
            case "dev":
            case "prod":
            case "test":
                break;
            default:
                mode = "prod";
                break;
        }

        System.setProperty("pippo.mode", mode.toUpperCase());
        System.setProperty("pippo.reload.enabled", String.valueOf(
                mode.equalsIgnoreCase("dev") || getInstance().getConfig().getString("server.mode").equalsIgnoreCase("dev")
        ));

        String dbAddress = getInstance().getConfig().getString("database.address");
        int dbPort = getInstance().getConfig().getInt("database.port");
        String dbDatabase = getInstance().getConfig().getString("database.database");
        String dbUsername = getInstance().getConfig().getString("database.username");
        char[] dbPassword = getInstance().getConfig().getString("database.password").toCharArray();

        try {
            getInstance().setDatabase(MariaDatabase.create(dbAddress, dbPort, dbDatabase, dbUsername, dbPassword));
            getInstance().getDatabase().connect();
            getInstance().getDatabase().setup();
        } catch (Exception ex) {
            getInstance().getLogger().error("Could not connect to MariaDB database.", ex);
            return;
        }

        // Event Listeners
        getInstance().getEventBus().register(getInstance());

        getInstance().setPippo(new Pippo(getInstance()));
        getInstance().getPippo().getServer().getSettings().host(address);
        getInstance().getPippo().getServer().getSettings().port(port);
        getInstance().getPippo().addResourceRoute(new PublicResourceHandler());
        getInstance().getPippo().addResourceRoute(new WebjarsResourceHandler());
        getInstance().getPippo().start();

        getInstance().getLogger().info("Hello and welcome to Gravity.");
        getInstance().getLogger().info("Type \"help\" to receive a list of commands.");

        getInstance().setConsoleScanner(new Scanner(System.in));
        String input = getInstance().getConsoleScanner().nextLine();

        ConsoleInputEvent event = new ConsoleInputEvent(input);
        getInstance().getEventBus().post(event);
        if(event.getResponse() != null) {
            getInstance().getLogger().info(event.getResponse());
        }
    }

    @Override
    protected void onInit() {
        // Init Web Server
        ANY("/*", new CSRFHandler());
    }

    @Override
    protected void onDestroy() {
        if(getInstance().getDatabase().isConnected()) {
            getInstance().getDatabase().disconnect();
        }
        // Destroy Web Server
    }

    @Subscribe
    public void onConsoleInput(ConsoleInputEvent event) {
        if(event.getCommand().equalsIgnoreCase("stop")) {
            event.setResponse("Thank you and have a nice day.");
            getInstance().getPippo().stop();
        }
    }

}
