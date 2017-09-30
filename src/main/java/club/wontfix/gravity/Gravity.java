package club.wontfix.gravity;

import club.wontfix.gravity.bootstrap.StartupOptions;
import club.wontfix.gravity.database.Database;
import club.wontfix.gravity.database.MariaDatabase;
import com.virtlink.commons.configuration2.jackson.JsonConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
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
import ro.pippo.core.route.PublicResourceHandler;
import ro.pippo.core.route.WebjarsResourceHandler;

@MetaInfServices
public class Gravity extends Application {

    @Getter(lazy = true)
    private static final Gravity instance = new Gravity();

    @Getter
    private Logger logger = LoggerFactory.getLogger(Gravity.class);

    @Getter @Setter
    private CommandLine cmdArgs;

    @Getter @Setter
    private JsonConfiguration config;

    @Getter @Setter
    private Database database;

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

        try {
            getInstance().setConfig(new FileBasedConfigurationBuilder<>(JsonConfiguration.class)
                    .configure(new Parameters().properties()
                            .setFileName(configPath)
                            .setThrowExceptionOnMissing(true)
                            .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
                            .setReloadingDetectorFactory(new DefaultReloadingDetectorFactory())
                            .setIncludesAllowed(false)
                            .setEncoding("UTF-8"))
                    .getConfiguration());
        } catch (ConfigurationException ex) {
            getInstance().getLogger().error("Could not load config file.", ex);
            return;
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
                mode.equalsIgnoreCase("dev") || getInstance().getConfig().getBoolean("server.mode")
        ));

        String dbAddress = getInstance().getConfig().getString("database.address");
        int dbPort = getInstance().getConfig().getInt("database.port");
        String dbDatabase = getInstance().getConfig().getString("database.database");
        String dbUsername = getInstance().getConfig().getString("database.username");
        char[] dbPassword = getInstance().getConfig().getString("database.password").toCharArray();

        try {
            getInstance().setDatabase(MariaDatabase.create(dbAddress, dbPort, dbDatabase, dbUsername, dbPassword));
            getInstance().getDatabase().connect();
        } catch (Exception ex) {
            getInstance().getLogger().error("Could not connect to MariaDB database.", ex);
            return;
        }

        Pippo pippo = new Pippo(Gravity.getInstance());
        pippo.getServer().setPort(port);
        pippo.addResourceRoute(new PublicResourceHandler());
        pippo.addResourceRoute(new WebjarsResourceHandler());
        pippo.start();

        getInstance().getLogger().info("Hello and welcome to Gravity.");
    }

    @Override
    protected void onInit() {
        // Init Web Server
    }

    @Override
    protected void onDestroy() {
        if(getInstance().getDatabase().isConnected()) {
            getInstance().getDatabase().disconnect();
        }
        // Destroy Web Server
    }

}
