package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.function.Consumer;

@GravityCommand(value = {"stop", "exit", "shutdown", "bye"}, allowPublic = false, description = "Shuts Gravity down (limited)")
public class StopCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {
        RequestBuffer.request(() -> {
            new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                    .withChannel(context.getChannel())
                    .withEmbed(new EmbedBuilder()
                            .withTitle("Thank you and have a nice day.")
                            .withDescription("Gravity will now shut down. \n" +
                                    "Thanks for your service.")
                            .withColor(new Color(43, 115, 178))
                            .build())
                    .build();

            Gravity.getInstance().getPippo().stop();
            System.exit(0); // Nuke the waiting input thread
        });
    }

}
