package club.wontfix.gravity.listeners;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.commands.Command;
import club.wontfix.gravity.events.impl.console.ConsoleInputEvent;
import com.google.common.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.util.Map;

public class ConsoleInputListener {

    @Subscribe
    public void onConsoleInput(ConsoleInputEvent event) {
        String label = event.getCommand().contains(" ") ? event.getCommand().split(" ")[0] : event.getCommand();

        if (!label.equalsIgnoreCase("help")) {
            for (Map.Entry<Command, Method> entry : Gravity.getInstance().getCommandManager().getCommands().entrySet()) {
                for (String cmd : entry.getKey().getCommands()) {
                    if (cmd.equalsIgnoreCase(label)) {
                        String[] args = {};
                        if (event.getCommand().contains(" ")) {
                            args = new String[event.getCommand().split(" ").length - 1];
                            int index = 0;
                            for (String s : event.getCommand().split(" ")) {
                                if (!s.equalsIgnoreCase(label)) {
                                    args[index] = s;
                                    index++;
                                }
                            }
                        }

                        boolean success = Gravity.getInstance().getCommandManager().call(label, args);
                        if (!success) {
                            event.addResponse("Usage: " + entry.getKey().getUsage().replace("<label>", label));
                        }
                    }
                }
            }

            Gravity.getInstance().getLogger().warn("Unknown command. Type \"help\" for a list of commands.");
        } else {
            Gravity.getInstance().getLogger().info("Command(s) - Description - Usage");

            for (Map.Entry<Command, Method> entry : Gravity.getInstance().getCommandManager().getCommands().entrySet()) {
                Command command = entry.getKey();

                int index = 0;
                StringBuilder builder = new StringBuilder();
                for (String cmd : command.getCommands()) {
                    index++;
                    builder.append(cmd).append(index != command.getCommands().length ? ", " : "");
                }

                Gravity.getInstance().getLogger().info(
                        "{} - {} - Usage: {}", builder.toString(), command.getDescription(), command.getUsage()
                );
            }
        }
    }

}
