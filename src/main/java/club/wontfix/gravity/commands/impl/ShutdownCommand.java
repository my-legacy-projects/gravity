package club.wontfix.gravity.commands.impl;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.commands.Command;

public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        super(new String[]{"stop", "exit", "shutdown"},
                "Shuts Gravity down",
                "shutdown");
    }

    @Override
    public boolean execute(String label, String[] args) {
        if (args.length == 0) {
            Gravity.getInstance().getPippo().stop();
            System.exit(0);
            return true;
        }
        return false;
    }

}
