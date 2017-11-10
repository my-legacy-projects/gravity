package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.function.Consumer;

@GravityCommand(value = {"help", "?", "ineedhelp"}, description = "Shows help")
public class HelpCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {


        RequestBuffer.request(() -> new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                .withChannel(context.getChannel())
                .withEmbed(new EmbedBuilder()
                        .withAuthorName("Command Help for Gravity")
                        .withAuthorIcon(Gravity.getInstance().getConfig().getString("discord.avatar"))

                        .withDesc("**~help**: Shows this menu \n")
                        .appendDesc("**~invite**: Displays invite link (allows you to get Gravity on your server)\n")
                        .appendDesc("**~status**: Shows information about Gravity \n")
                        .appendDesc("**~stop**: Shuts Gravity down " +
                                "(I\'ll be mad (\u256F\u00B0\u25A1\u00B0\uFF09\u256F\uFE35 \u253B\u2501\u253B) \n")
                        .appendDesc("**~weeb**: Is a weeb? \n")

                        .appendField("Gravity",
                                "Invite Gravity to your own Discord server using [this link](" +
                                        "https://discordapp.com/oauth2/authorize?client_id=364240975486517250&scope=bot&permissions=8).",
                                true)

                        .withFooterText("Help requested by " + context.getAuthor().getDisplayName(context.getGuild()))
                        .withColor(new Color(43, 115, 178))
                        .build())
                .build());
    }

}
