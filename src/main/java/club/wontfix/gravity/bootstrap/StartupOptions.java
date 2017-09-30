package club.wontfix.gravity.bootstrap;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class StartupOptions {

    public Options getOptions() {
        Options options = new Options();

        Option ipAddress = new Option("i", "ip", true,
                "IP Address that the auth server should be bound to");
        ipAddress.setRequired(true);
        options.addOption(ipAddress);

        Option port = new Option("p", "port", true,
                "Port that the auth server should listen on");
        port.setRequired(true);
        options.addOption(port);

        Option mode = new Option("m", "mode", true,
                "Mode in which Gravity should run (Dev/Prod/Test)");
        mode.setRequired(false);
        options.addOption(mode);

        return options;
    }

}
