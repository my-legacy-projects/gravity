package club.wontfix.gravity.commands.impl;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.commands.Command;

public class KillSwitchCommand extends Command {

    public KillSwitchCommand() {
        super(new String[]{"killswitch", "kill", "disable", "switch"},
                "Kill Switches a build",
                "kill <verify id>");
    }

    @Override
    public boolean execute(String label, String[] args) {
        if (args.length == 1) {
            String verifyID = args[0];

            if (Gravity.getInstance().getEasyDatabase().isVerifyIDBound(verifyID)) {
                Gravity.getInstance().getEasyDatabase().killSwitch(verifyID);

                Gravity.getInstance().getLogger().info("Successfully kill switched {}.", verifyID);
                return true;
            }
        }

        return false;
    }

}
