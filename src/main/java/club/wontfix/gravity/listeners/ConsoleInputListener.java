package club.wontfix.gravity.listeners;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.events.impl.console.ConsoleInputEvent;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ConsoleInputListener {

    @Getter
    private Map<String[], Method> commands = new HashMap<>();

    public ConsoleInputListener() {
        try {
            commands.put(new String[]{"stop", "shutdown"}, getClass().getMethod("cmdStop", ConsoleInputEvent.class));
        } catch (NoSuchMethodException ex) {
            Gravity.getInstance().getLogger().error("Could not find method!", ex);
        }
    }

    @Subscribe
    public void onConsoleInput(ConsoleInputEvent event) {
        for (Map.Entry<String[], Method> entry : commands.entrySet()) {
            for (String cmd : entry.getKey()) {
                if (event.getCommand().equalsIgnoreCase(cmd)) {
                    Method m = entry.getValue();
                    m.setAccessible(true);

                    Object result = null;
                    try {
                        result = m.invoke(this, event);
                    } catch (Exception ex) {
                        Gravity.getInstance().getLogger().error("Error while executing command!", ex);
                    }

                    if (result != null && (result instanceof String)) {
                        event.addResponse((String) result);
                    }
                }
            }
        }
    }

    public String cmdStop(ConsoleInputEvent event) {
        Gravity.getInstance().getPippo().stop();

        return null;
    }

}
