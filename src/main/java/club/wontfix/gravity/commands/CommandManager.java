package club.wontfix.gravity.commands;

import club.wontfix.gravity.Gravity;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    @Getter
    private Map<Command, Method> commands = new HashMap<>();

    public void register(Command command) {
        try {
            commands.put(command, command.getClass().getMethod("execute", String.class, String[].class));
        } catch (NoSuchMethodException ex) {
            Gravity.getInstance().getLogger().error("Failed to register command.", ex);
        }
    }

    public void unregister(Command command) {
        if (commands.containsKey(command)) {
            commands.remove(command);
        }
    }

    public boolean call(String command, String[] args) {
        for (Map.Entry<Command, Method> entry : commands.entrySet()) {
            for (String cmd : entry.getKey().getCommands()) {
                if (cmd.equalsIgnoreCase(command)) {
                    Method m = entry.getValue();
                    m.setAccessible(true);

                    boolean success = false;
                    try {
                        success = (boolean) m.invoke(entry.getKey(), command, args);
                    } catch (Exception ex) {
                        Gravity.getInstance().getLogger().error("Error while executing command {}.", command);
                        Gravity.getInstance().getLogger().error("", ex);
                    }

                    return success;
                }
            }
        }

        return false;
    }

}
