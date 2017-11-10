package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.function.Consumer;

@GravityCommand(value = {"skidbuster"}, description = "Busts skids (requested by LWSS)")
public class SkidbusterCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {
        RequestBuffer.request(() -> new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                .withChannel(context.getChannel())
                .withEmbed(new EmbedBuilder()
                        .withAuthorName("Discord")
                        .withAuthorIcon("https://i.imgur.com/1SLKLSB.png")
                        .withDescription("The Company is based in the United States. No matter where you are located, you consent to the processing " +
                                "and transferring of your information in and to the U.S. and other countries.\n\n" +
                                "Aggregated Information: In an ongoing effort to better understand and serve the users of the Services, we may " +
                                "conduct research on our customer demographics, interests and behavior based on the information collected. This " +
                                "research may be compiled and analyzed on an aggregate basis, and we may share this aggregate data with our " +
                                "affiliates, agents and business partners. We may also disclose aggregated user statistics in order to describe " +
                                "our services to current and prospective business partners, and to other third parties for other lawful purposes.\n" +
                                "\nLegal Requirements: We may disclose your information if required to do so by law or in the good faith belief " +
                                "that such action is necessary to (i) comply with a legal obligation, (ii) protect and defend the rights or property " +
                                "of the Company or Related Companies, (iii) protect the personal safety of users of the Services or the public, " +
                                "or (iv) protect against legal liability.\n\n")
                        .appendDescription("We may disclose your information if required to do so by law or in the *good faith*  \n" +
                                "Protect the personal safety of users of the Services or the *public*")
                        .withColor(new Color(255, 0, 0))
                        .build())
                .build());
    }

}
