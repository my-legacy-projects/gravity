package club.wontfix.gravity.commands.impl;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.commands.Command;
import club.wontfix.gravity.easy.User;

public class ListCommand extends Command {

    public ListCommand() {
        super(new String[]{"listverifyids", "listids", "listverify", "listusers", "list"},
                "Shows a list of either pending or confirmed Verify ID's.",
                "list [pending | confirmed]");
    }

    @Override
    public boolean execute(String label, String[] args) {
        if (args.length <= 1) {
            int mode = args.length == 1 ? (args[0].equalsIgnoreCase("pending") ? 1 : 0) : 0;
            switch (mode) {
                case 0: // Confirmed
                    Gravity.getInstance().getLogger().info("Name - Unique ID - Verify ID - Machine Name - Dev - Kill Switched");
                    Gravity.getInstance().getLogger().info("--------------------------------------");

                    for (User user : Gravity.getInstance().getEasyDatabase().getUsers()) {
                        Gravity.getInstance().getLogger().info(
                                "{} - {} - {} - {} - {} - {}",
                                user.getName(), user.getUniqueID(), user.getVerifyID(), user.getMachineName(),
                                user.isDev(), user.isKillSwitched()
                        );
                    }
                    break;
                case 1: // Pending
                    Gravity.getInstance().getLogger().info("Name - Verify ID - Dev");
                    Gravity.getInstance().getLogger().info("-----------------");

                    for (User user : Gravity.getInstance().getEasyDatabase().getWaitingVerifyIDs()) {
                        Gravity.getInstance().getLogger().info(
                                "{} - {} - {}", user.getName(), user.getVerifyID(), user.isDev()
                        );
                    }
                    break;
            }

            return true;
        }

        return false;
    }

}
