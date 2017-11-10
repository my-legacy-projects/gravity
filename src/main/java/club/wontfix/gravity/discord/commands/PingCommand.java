package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@GravityCommand(value = {"ping", "pong"}, description = "Pong!")
public class PingCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {
        RequestBuffer.request(() -> {
            LocalDateTime timestamp = context.getMessage().getTimestamp();
            Duration duration = Duration.between(LocalDateTime.now(), timestamp);
            String ms = String.valueOf(duration.toMillis()).replace("-", "");

            new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                    .withChannel(context.getChannel())
                    .withEmbed(new EmbedBuilder()
                            .withTitle("Pong!")
                            .withDescription("Response took `" + ms + "ms`")
                            .withColor(new Color(43, 115, 178))
                            .build())
                    .build();
        });
    }

}
