package club.wontfix.gravity.bootstrap;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class StartupOptions {

    public Options getOptions() {
        Options options = new Options();

        Option config = new Option("c", "config", true,
                "Path to config.json file for Gravity");
        config.setRequired(false);
        options.addOption(config);

        Option address = new Option("a", "address", true,
                "IP Address on which Pippo should be bound to");
        address.setRequired(false);
        options.addOption(address);

        Option port = new Option("p", "port", true,
                "Port that the auth server should listen on");
        port.setRequired(false);
        options.addOption(port);

        Option mode = new Option("m", "mode", true,
                "Mode in which Gravity should run (Dev/Prod/Test)");
        mode.setRequired(false);
        options.addOption(mode);

        return options;
    }

}
