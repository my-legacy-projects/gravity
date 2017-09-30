package club.wontfix.gravity;

import club.wontfix.gravity.bootstrap.StartupOptions;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.*;
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

    public static void main(String[] args) {

        Options options = new StartupOptions().getOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            getInstance().setCmdArgs(parser.parse(options, args));
        } catch (ParseException ex) {
            formatter.printHelp("utility-name", options);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(getInstance().getCmdArgs().getOptionValue("port"));
        } catch (NumberFormatException ex) {
            getInstance().getLogger().error("Could not parse \"{0}\" to a valid port (int).",
                    getInstance().getCmdArgs().getOptionValue("port"));
            getInstance().getLogger().debug("NumberFormatException (Gravity::main)", ex);
            return;
        }

        String mode = getInstance().getCmdArgs().getOptionValue("mode");
        if(mode != null) {
            switch (mode.toLowerCase()) {
                case "dev":
                case "prod":
                case "test":
                    break;
                default:
                    mode = "prod";
                    break;
            }
        } else {
            mode = "prod";
        }

        System.setProperty("pippo.mode", mode.toUpperCase());
        System.setProperty("pippo.reload.enabled", String.valueOf(mode.equalsIgnoreCase("dev")));

        Pippo pippo = new Pippo(Gravity.getInstance());
        pippo.getServer().setPort(port);
        pippo.addResourceRoute(new PublicResourceHandler());
        pippo.addResourceRoute(new WebjarsResourceHandler());
        pippo.start();

    }

    @Override
    protected void onInit() {
        // Init Web Server
    }

    @Override
    protected void onDestroy() {
        // Destroy Web Server
    }

}
