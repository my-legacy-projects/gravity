package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@GravityCommand(value = {"invite", "invitelink", "discord"}, description = "Displays oauth link to invite Gravity to your own server")
public class InviteCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {
        RequestBuffer.request(() -> {
            String inviteURL = "https://discordapp.com/oauth2/authorize?client_id=364240975486517250&scope=bot&permissions=8";

            EmbedBuilder builder = new EmbedBuilder();

            builder.withAuthorName("Gravity");
            builder.withAuthorIcon(Gravity.getInstance().getConfig().getString("discord.avatar"));

            builder.appendField("OAuth2",
                    "[Click to authorize](" + inviteURL + ")",
                    true);

            builder.appendField("Support",
                    "no lol",
                    true);

            builder.withFooterText("Requested by " + context.getAuthor().getDisplayName(context.getGuild()));
            builder.withTimestamp(LocalDateTime.now());

            builder.withColor(new Color(43, 115, 178));

            context.getChannel().sendMessage(builder.build());
        });
    }

}
