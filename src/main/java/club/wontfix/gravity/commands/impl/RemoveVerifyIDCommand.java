package club.wontfix.gravity.commands.impl;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.commands.Command;
import club.wontfix.gravity.events.impl.actions.InvalidateVerifyIDEvent;

public class RemoveVerifyIDCommand extends Command {

    public RemoveVerifyIDCommand() {
        super(new String[]{"removeverifyid", "removeid", "removeverify", "removeuser", "remove"},
                "Removes a registered / pending Verify ID from the database",
                "remove <verify id>");
    }

    @Override
    public boolean execute(String label, String[] args) {
        if (args.length == 1) {
            String verifyID = args[0];

            if (Gravity.getInstance().getEasyDatabase().isVerifyIDBound(verifyID)) {
                String name = Gravity.getInstance().getEasyDatabase().getUserFromVerifyID(verifyID).getName();

                InvalidateVerifyIDEvent invalidateEvent = new InvalidateVerifyIDEvent(verifyID, name);
                Gravity.getInstance().getEventBus().post(invalidateEvent);
                if (!invalidateEvent.isCancelled()) {
                    Gravity.getInstance().getEasyDatabase().unregisterUserUsingVerifyID(verifyID);

                    Gravity.getInstance().getLogger().info("Successfully removed {} from database.", verifyID);
                }
            } else if (Gravity.getInstance().getEasyDatabase().isVerifyIDWaiting(verifyID)) {
                InvalidateVerifyIDEvent invalidateEvent = new InvalidateVerifyIDEvent(verifyID, "__PENDING__");
                Gravity.getInstance().getEventBus().post(invalidateEvent);
                if (!invalidateEvent.isCancelled()) {
                    Gravity.getInstance().getEasyDatabase().unregisterWaitingVerifyID(verifyID);

                    Gravity.getInstance().getLogger().info("Successfully removed {} from pending queue.", verifyID);
                }
            } else {
                Gravity.getInstance().getLogger().error("Could not find {} in either database or pending queue.", verifyID);
            }

            return true;
        }

        return false;
    }

}
