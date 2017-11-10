package club.wontfix.gravity.commands.impl;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.commands.Command;
import club.wontfix.gravity.events.impl.actions.KillSwitchEvent;

public class KillSwitchCommand extends Command {

    public KillSwitchCommand() {
        super(new String[]{"killswitch", "kill", "disable", "switch"},
                "Kill Switches a build",
                "kill <verify id>");
    }

    @Override
    public boolean execute(String label, String[] args) {
        if (args.length == 1) {
            if (Gravity.getInstance().getEasyDatabase().isVerifyIDBound(args[0])) {
                KillSwitchEvent killSwitchEvent = new KillSwitchEvent(args[0]);
                Gravity.getInstance().getEventBus().post(killSwitchEvent);

                if (!killSwitchEvent.isCancelled()) {
                    Gravity.getInstance().getEasyDatabase().killSwitch(args[0]);

                    Gravity.getInstance().getLogger().info("Successfully kill switched {}.", args[0]);
                } else {
                    Gravity.getInstance().getLogger().info("The action has been cancelled.");
                }
                return true;
            } else {
                Gravity.getInstance().getLogger().error("Could not find {} in database.", args[0]);
            }
        }

        return false;
    }

}
