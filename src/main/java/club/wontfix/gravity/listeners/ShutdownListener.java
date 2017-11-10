package club.wontfix.gravity.listeners;

import club.wontfix.gravity.events.impl.bootstrap.GravityStopEvent;
import com.google.common.eventbus.Subscribe;

public class ShutdownListener {

    @Subscribe
    public void onShutdown(GravityStopEvent event) {
        event.addResponse("Thank you and have a nice day.");
    }

}
