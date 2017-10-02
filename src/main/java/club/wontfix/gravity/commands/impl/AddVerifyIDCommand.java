package club.wontfix.gravity.commands.impl;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.commands.Command;
import club.wontfix.gravity.events.impl.actions.FreshVerifyIDEvent;
import club.wontfix.gravity.util.Util;

public class AddVerifyIDCommand extends Command {

    public AddVerifyIDCommand() {
        super(new String[]{"addverifyid", "addid", "addverify", "adduser", "add"},
                "Adds a pending VerifyID to the database",
                "add <name of user> <true/false if user is dev>");
    }

    @Override
    public boolean execute(String label, String[] args) {
        if (args.length == 2) {
            String name = args[0];
            boolean dev = Boolean.parseBoolean(args[1]);
            String verifyID = Util.generateVerifyID();

            if (Gravity.getInstance().getEasyDatabase().isVerifyIDBound(verifyID) ||
                    Gravity.getInstance().getEasyDatabase().isVerifyIDWaiting(verifyID)) {
                verifyID = Util.generateVerifyID();
                // ^  Probably will need changes as soon as we get to about 1k users  ^
            }

            FreshVerifyIDEvent freshEvent = new FreshVerifyIDEvent(name, verifyID, dev);
            Gravity.getInstance().getEventBus().post(freshEvent);
            if (!freshEvent.isCancelled()) {
                Gravity.getInstance().getEasyDatabase().registerWaitingVerifyID(name, verifyID, dev);

                Gravity.getInstance().getLogger().info("Success! Generated new Verify ID for {}.", name);
                Gravity.getInstance().getLogger().info("  ");
                Gravity.getInstance().getLogger().info("Name: {}", name);
                Gravity.getInstance().getLogger().info("Dev: {}", dev);
                Gravity.getInstance().getLogger().info("Verify ID: {}", verifyID);
                Gravity.getInstance().getLogger().info("  ");
                Gravity.getInstance().getLogger().info("It has been automatically added to the database.");
                Gravity.getInstance().getLogger().info("Please input this Verify ID the next time when logging into Spartan.");
            } else {
                Gravity.getInstance().getLogger().info("The action has been cancelled.");
            }
            return true;
        }

        return false;
    }

}
