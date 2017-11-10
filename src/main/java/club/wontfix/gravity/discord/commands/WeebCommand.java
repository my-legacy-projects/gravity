package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;
import com.google.common.hash.Hashing;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@GravityCommand(value = "weeb", description = "Is a Weeb?")
public class WeebCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {

        RequestBuffer.request(() -> {
            IMessage msg = new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                    .withChannel(context.getChannel())
                    .withEmbed(new EmbedBuilder()
                            .withDescription("Calculating... \n")
                            .appendDescription("Please wait.")
                            .withColor(new Color(43, 115, 178))
                            .build())
                    .build();

            context.getChannel().setTypingStatus(true);

            IUser user;

            if (context.getGuild().getRolesByName("weeb").size() > 0) {
                user = getRandomUser(context.getGuild().getUsersByRole(context.getGuild().getRolesByName("weeb").get(0)));
            } else {
                if (context.getArgs().size() == 0) {
                    user = getRandomUser(context.getChannel().getUsersHere());
                } else {
                    if (context.getMessage().getMentions().size() == 1) {
                        user = context.getMessage().getMentions().get(0);
                    } else {
                        user = getRandomUser(context.getChannel().getUsersHere());
                    }
                }
            }

            if (user == Gravity.getInstance().getDiscordBotManager().getDiscordClient().getOurUser()) {
                new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                        .withChannel(context.getChannel())
                        .withEmbed(new EmbedBuilder()
                                .withDescription("I'm not a weeb.")
                                .withColor(new Color(43, 115, 178))
                                .build())
                        .build();
                context.getChannel().setTypingStatus(false);
                msg.delete();
                return;
            }

            int percent = user.getLongID() == 132198459615870976L ? Integer.MAX_VALUE :
                    (int) ((Hashing.sha384().hashLong(user.getLongID()).asInt() * 100.0f) /
                            Hashing.murmur3_128().hashLong(user.getLongID()).asInt());

            Gravity.getInstance().getScheduler().schedule(() -> {
                new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                        .withChannel(context.getChannel())
                        .withEmbed(new EmbedBuilder()
                                .withAuthorName(user.getDisplayName(context.getGuild()))
                                .withAuthorIcon(user.getAvatarURL())
                                .withDescription("Gravity has found a WEEB in this channel using advanced artificial " +
                                        "intelligence and professional, top-tier NSA technologies.\n\n" +
                                        "All calculations performed are 100% accurate and were calculated on the NASA " +
                                        "super computer. There's no chance that the calculated percentages are incorrect.")
                                .appendField("Weeb",
                                        user.mention(true),
                                        true)
                                .appendField("Weebcentage",
                                        (percent + "%").replace("-", ""),
                                        true)
                                .withColor(new Color(43, 115, 178))
                                .build())
                        .build();
                context.getChannel().setTypingStatus(false);
                msg.delete();
            }, 5, TimeUnit.SECONDS);
        });
    }

    private IUser getRandomUser(List<IUser> users) {
        int randomNum = ThreadLocalRandom.current().nextInt(0, users.size());

        IUser user = users.get(randomNum);

        if (user.getLongID() == Gravity.getInstance().getDiscordBotManager().getDiscordClient().getOurUser().getLongID()) {
            // Generate a new random user if the random user is the bot itself
            user = users.get(ThreadLocalRandom.current().nextInt(0, users.size()));
        }

        user = checkIfPositive(user, users);

        return user;
    }

    private IUser checkIfPositive(IUser user, List<IUser> users) {
        if (user.isBot() || user.getPresence().getStatus() == StatusType.OFFLINE) {
            user = users.get(ThreadLocalRandom.current().nextInt(0, users.size()));
            return checkIfPositive(user, users);
        }

        return user;
    }

}
