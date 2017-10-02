package club.wontfix.gravity;

import club.wontfix.gravity.bootstrap.StartupOptions;
import club.wontfix.gravity.commands.CommandManager;
import club.wontfix.gravity.commands.impl.*;
import club.wontfix.gravity.database.Database;
import club.wontfix.gravity.easy.EasyDatabase;
import club.wontfix.gravity.events.impl.bootstrap.GravityStartEvent;
import club.wontfix.gravity.events.impl.bootstrap.GravityStopEvent;
import club.wontfix.gravity.events.impl.console.ConsoleInputEvent;
import club.wontfix.gravity.events.impl.error.GeneralExceptionEvent;
import club.wontfix.gravity.events.impl.error.SQLExceptionEvent;
import club.wontfix.gravity.listeners.ConsoleInputListener;
import club.wontfix.gravity.listeners.ShutdownListener;
import club.wontfix.gravity.routes.general.Routes;
import club.wontfix.gravity.routes.verify.VerifyRoutes;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.virtlink.commons.configuration2.jackson.JsonConfiguration;
import lombok.Getter;
import lombok.NonNull;
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
import ro.pippo.freemarker.FreemarkerTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Scanner;

@MetaInfServices
public class Gravity extends Application {

    @Getter(lazy = true)
    private static final Gravity instance = new Gravity();

    @Getter(lazy = true)
    private final Logger logger = LoggerFactory.getLogger(Gravity.class);

    @Getter @Setter(onParam = @__(@NonNull))
    private CommandLine cmdArgs;

    @Getter @Setter(onParam = @__(@NonNull))
    private JsonConfiguration config;

    @Getter @Setter(onParam = @__(@NonNull))
    private Scanner consoleScanner;

    @Getter @Setter(onParam = @__(@NonNull))
    private Database database;

    @Getter(lazy = true)
    private final EasyDatabase easyDatabase = new EasyDatabase();

    @Getter(lazy = true)
    private final CommandManager commandManager = new CommandManager();

    @Getter
    private final EventBus eventBus = new EventBus();

    @Getter @Setter
    private Pippo pippo;

    @Getter(lazy = true)
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    @Setter
    private boolean running = false;

    public static void main(String[] args) {
        getInstance().setRunning(true);
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
        if (configPath == null) {
            configPath = "config.json";
        }

        File file = new File(Paths.get(".").toAbsolutePath().normalize().toFile(), configPath);
        if (!file.exists()) {
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
        if (address == null) {
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
            if (cmdPort != null) {
                port = Integer.parseInt(cmdPort);
            }
        } catch (NumberFormatException ex) {
            getInstance().getLogger().error("Could not parse \"{}\" to a valid port (int).",
                    getInstance().getCmdArgs().getOptionValue("port"));
            getInstance().getLogger().debug("", ex);
            return;
        }
        if (port == -1) {
            port = getInstance().getConfig().getInt("server.port");
        }

        String mode = getInstance().getCmdArgs().getOptionValue("mode");
        if (mode == null) {
            mode = getInstance().getConfig().getString("server.mode");
        }
        mode = mode.toLowerCase();

        switch (mode) {
            case "dev":
            case "prod":
            case "test":
                break;
            default:
                mode = "prod";
                break;
        }

        System.setProperty("pippo.mode", mode);
        System.setProperty("pippo.reload.enabled", String.valueOf(
                mode.equalsIgnoreCase("dev") || getInstance().getConfig().getBoolean("server.reload")
        ));

        String dbAddress;
        int dbPort;
        String dbDatabase;
        String dbUsername;
        char[] dbPassword;

        try {
            dbAddress = getInstance().getConfig().getString("database.address");
            dbPort = getInstance().getConfig().getInt("database.port");
            dbDatabase = getInstance().getConfig().getString("database.database");
            dbUsername = getInstance().getConfig().getString("database.username");
            dbPassword = getInstance().getConfig().getString("database.password").toCharArray();
        } catch (NoSuchElementException ex) {
            getInstance().getLogger().error("Could not find database configuration in config.json.", ex);
            return;
        }

        try {
        /*    getInstance().setDatabase(MariaDatabase.create(dbAddress, dbPort, dbDatabase, dbUsername, dbPassword));
            getInstance().getDatabase().connect();
            getInstance().getDatabase().setup();*/
        } catch (Exception ex) {
            getInstance().getLogger().error("Could not connect to MariaDB database.", ex);
            return;
        }

        // Commands
        getInstance().getCommandManager().register(new AddVerifyIDCommand());
        getInstance().getCommandManager().register(new KillSwitchCommand());
        getInstance().getCommandManager().register(new ListCommand());
        getInstance().getCommandManager().register(new RemoveVerifyIDCommand());
        getInstance().getCommandManager().register(new ShutdownCommand());

        // Event Listeners
        getInstance().getEventBus().register(getInstance());
        getInstance().getEventBus().register(new ConsoleInputListener());
        getInstance().getEventBus().register(new ShutdownListener());

        getInstance().setPippo(new Pippo(getInstance()));
        getInstance().getPippo().getServer().getSettings().host(address);
        getInstance().getPippo().getServer().getSettings().port(port);
        getInstance().getPippo().start();

        GravityStartEvent startEvent = new GravityStartEvent();
        startEvent.addResponse("Hello and welcome to Gravity. Listening on http://" + address + ":" + port + "/");
        startEvent.addResponse("Type \"help\" to receive a list of commands.");
        getInstance().getEventBus().post(startEvent);
        if (startEvent.getResponses().size() > 0) {
            for (int i = 0; i < startEvent.getResponses().size(); i++) {
                getInstance().getLogger().info(startEvent.getResponses().get(i));
            }
        }

        // Continuously wait for input on another thread
        // This doesn't interfere with Pippo as this and Pippo are both running on another thread
        // The ConsoleInputEvent listeners on the other hand are running on the main thread
        Thread inputThread = new Thread(() -> {
            while (getInstance().isRunning()) {
                getInstance().setConsoleScanner(new Scanner(System.in));
                String input = getInstance().getConsoleScanner().nextLine();

                ConsoleInputEvent inputEvent = new ConsoleInputEvent(input);
                getInstance().getEventBus().post(inputEvent);
                if (inputEvent.getResponses().size() > 0) {
                    for (String s : inputEvent.getResponses()) {
                        getInstance().getLogger().info(s);
                    }
                }
            }
        });
        inputThread.setName("Input Thread");
        inputThread.setPriority(Thread.MAX_PRIORITY);
        inputThread.run();
    }

    @Override
    protected void onInit() {
        setTemplateEngine(new FreemarkerTemplateEngine());

        setUploadLocation("uploades");

        // Add routes for static content
        addResourceRoute(new PublicResourceHandler());
        addResourceRoute(new WebjarsResourceHandler());

        getRouter().ignorePaths("/favicon.ico");

        // Handle errors
        getErrorHandler().setExceptionHandler(SQLException.class, (ex, context) -> {
            SQLExceptionEvent exceptionEvent = new SQLExceptionEvent((SQLException) ex);
            getEventBus().post(exceptionEvent);
            if (exceptionEvent.isCancelled()) {
                context.render(exceptionEvent.getTemplateToRender());
            } else {
                getErrorHandler().handle(exceptionEvent.getResponseCode(), context);
            }
        });

        getErrorHandler().setExceptionHandler(Exception.class, (ex, context) -> {
            if (!(ex instanceof SQLException)) {
                GeneralExceptionEvent exceptionEvent = new GeneralExceptionEvent(ex);
                getEventBus().post(exceptionEvent);
                if (exceptionEvent.isCancelled()) {
                    context.render(exceptionEvent.getTemplateToRender());
                } else {
                    getErrorHandler().handle(exceptionEvent.getResponseCode(), context);
                }
            }
        });

        GET("/", context -> getErrorHandler().handle(404, context));

        addBeforeFilters();

        // Routes
        addRouteGroup(new Routes());
        addRouteGroup(new VerifyRoutes());

        addAfterFilters();
    }

    @Override
    protected void onDestroy() {
        GravityStopEvent stopEvent = new GravityStopEvent();
        stopEvent.addResponse("Thank you and have a nice day.");
        getEventBus().post(stopEvent);
        if (stopEvent.getResponses().size() > 0) {
            for (String s : stopEvent.getResponses()) {
                getInstance().getLogger().info(s);
            }
        }

        getInstance().setRunning(false);
    }

    private void addBeforeFilters() {
        ANY("/.*", new CSRFHandler()).named("CSRF security handler");
    }

    private void addAfterFilters() {
        // ANY("/.*", routeContext -> getInstance().getDatabase().disconnect()).runAsFinally();
    }

}
