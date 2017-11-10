package club.wontfix.gravity.discord;

import club.wontfix.gravity.Gravity;
import com.darichey.discord.CommandContext;
import lombok.AllArgsConstructor;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class CommandExecutor implements Runnable {

    private Method method;
    private Object reference;
    private CommandContext context;

    @Override
    public void run() {
        try {
            method.invoke(reference, context);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            StringBuilder builder = new StringBuilder();
            for (StackTraceElement e : ex.getStackTrace()) {
                builder.append("at").append(" ").append(e.toString()).append("\n");
            }

            new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                    .withChannel(context.getChannel())
                    .withEmbed(new EmbedBuilder()
                            .withTitle(ex.getClass().getName() + ": " + ex.getMessage())
                            .withDescription(builder.toString())
                            .withColor(new Color(255, 0, 0))
                            .build())
                    .build();

            Gravity.getInstance().getLogger().error("Error while executing command.", ex);
        }
    }

}
