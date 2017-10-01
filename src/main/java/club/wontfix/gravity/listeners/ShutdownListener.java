package club.wontfix.gravity.listeners;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.events.impl.bootstrap.GravityStopEvent;
import com.google.common.eventbus.Subscribe;

public class ShutdownListener {

    @Subscribe
    public void onShutdown(GravityStopEvent event) {
        if (Gravity.getInstance().getDatabase() != null && Gravity.getInstance().getDatabase().isConnected()) {
            Gravity.getInstance().getDatabase().disconnect();
        }
    }

}
