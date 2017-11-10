package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;
import org.apache.commons.lang3.time.DurationFormatUtils;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@GravityCommand(value = "status", allowPublic = false, description = "Shows the status of Gravity (limited)")
public class StatusCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {
        RequestBuffer.request(() -> {
            IDiscordClient client = Gravity.getInstance().getDiscordBotManager().getDiscordClient();

            Duration duration = Duration.between(Gravity.getInstance().getDiscordBotManager().getStartTime(), LocalDateTime.now());
            String uptime = DurationFormatUtils.formatDuration(duration.toMillis(), "dd\'d\' HH\'h\' mm\'min\' ss \'sec\'");

            EmbedBuilder builder = new EmbedBuilder();

            builder.withAuthorName("Gravity Status");
            builder.withAuthorIcon(Gravity.getInstance().getConfig().getString("discord.avatar"));

            builder.appendField("Server",
                    "Online: " + Gravity.getInstance().isRunning() + "\n" +
                            "Address: " + Gravity.getInstance().getConfig().getString("server.address") + ":" +
                            Gravity.getInstance().getConfig().getInt("server.port") + "\n" +
                            "Mode: " + Gravity.getInstance().getConfig().getString("server.mode").toUpperCase(),
                    true);
            builder.appendField("Database",
                    "Connected: " + (Gravity.getInstance().getDatabase() != null && Gravity.getInstance().getDatabase().isConnected()) + "\n" +
                            "IP Address: " + Gravity.getInstance().getConfig().getString("database.address") + ":" +
                            Gravity.getInstance().getConfig().getInt("database.port") + "\n" +
                            "Database: " + Gravity.getInstance().getConfig().getString("database.database"),
                    true);

            // Database Info
            //builder.appendField("", "", false);
            //builder.appendField("", "", true);

            builder.appendField("Discord Bot",
                    "Streaming **" + client.getOurUser().getPresence().getPlayingText().get() +
                            "** (" + client.getOurUser().getPresence().getStreamingUrl().get() + ") \n" +
                            "Shards: " + client.getShardCount() + "\n" +
                            "Guilds: " + client.getGuilds().size() + "\n" +
                            "Users (in all " + client.getGuilds().size() + " guilds): " + client.getUsers().size() + "\n" +
                            "Uptime: " + uptime,
                    true);
            builder.appendField("Members",
                    "Developers: " + Gravity.getInstance().getDiscordBotManager().getDevsMap().size() + "\n" +
                            "Members: " + Gravity.getInstance().getDiscordBotManager().getUsersMap().size(),
                    true);

            builder.withThumbnail(context.getGuild().getIconURL());

            builder.withFooterText("Requested by " + context.getAuthor().getDisplayName(context.getGuild()));
            builder.withTimestamp(LocalDateTime.now());

            builder.withColor(new Color(43, 115, 178));

            context.getChannel().sendMessage(builder.build());
        });
    }

}
