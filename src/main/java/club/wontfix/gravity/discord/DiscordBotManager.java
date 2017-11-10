package club.wontfix.gravity.discord;

import club.wontfix.gravity.Gravity;
import club.wontfix.gravity.easy.User;
import club.wontfix.gravity.events.impl.actions.FreshVerifyIDEvent;
import club.wontfix.gravity.events.impl.actions.InvalidateVerifyIDEvent;
import club.wontfix.gravity.events.impl.actions.KillSwitchEvent;
import club.wontfix.gravity.events.impl.verify.FailedAuthEvent;
import club.wontfix.gravity.events.impl.verify.SuccessAuthEvent;
import com.darichey.discord.Command;
import com.darichey.discord.CommandContext;
import com.darichey.discord.CommandListener;
import com.darichey.discord.CommandRegistry;
import com.darichey.discord.limiter.Limiter;
import com.darichey.discord.limiter.UserLimiter;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class DiscordBotManager {

    @Getter
    private Map<String, Long> devsMap = new HashMap<>();

    @Getter
    private Map<String, Long> usersMap = new HashMap<>();

    @Getter
    @Setter
    private IDiscordClient discordClient;

    @Getter
    @Setter
    private CommandRegistry registry;

    @Getter
    @Setter
    private ScheduledFuture statusFuture;

    @Getter
    @Setter
    private int currentTick = 0;

    @Getter
    private CommandListener cmdListener;

    @Getter
    private LocalDateTime startTime;

    @Getter
    private ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);

    public void initialize() {
        registry = new CommandRegistry("~");

        Iterator<String> guilds = Gravity.getInstance().getConfig().getKeys("discord.permitted.guilds");
        List<Long> permittedGuilds = new ArrayList<>();
        while (guilds.hasNext()) {
            permittedGuilds.add(Gravity.getInstance().getConfig().getLong(guilds.next()));
        }

        Iterator<String> channels = Gravity.getInstance().getConfig().getKeys("discord.permitted.channels");
        List<Long> permittedChannels = new ArrayList<>();
        while (channels.hasNext()) {
            permittedChannels.add(Gravity.getInstance().getConfig().getLong(channels.next()));
        }

        Iterator<String> roles = Gravity.getInstance().getConfig().getKeys("discord.permitted.roles");
        List<Long> permittedRoles = new ArrayList<>();
        while (roles.hasNext()) {
            permittedRoles.add(Gravity.getInstance().getConfig().getLong(roles.next()));
        }

        Iterator<String> sudoers = Gravity.getInstance().getConfig().getKeys("discord.assign.sudoers");
        List<Long> permittedSudoers = new ArrayList<>();
        while (sudoers.hasNext()) {
            permittedSudoers.add(Gravity.getInstance().getConfig().getLong(sudoers.next()));
        }

        Reflections reflections = new Reflections("club.wontfix.gravity.discord.commands");
        Set<Class<?>> commands = reflections.getTypesAnnotatedWith(GravityCommand.class);
        Gravity.getInstance().getLogger().info("Size: " + commands.size());
        for (Class<?> cmd : commands) {
            Object obj;
            try {
                obj = cmd.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Gravity.getInstance().getLogger().error("Failed to initialize Class.", ex);
                continue;
            }

            Method method;
            try {
                method = cmd.getMethod("accept", CommandContext.class);
            } catch (NoSuchMethodException ex) {
                Gravity.getInstance().getLogger().warn("A non-command class has been found in the commands package.", ex);
                continue;
            }

            Set<Limiter> limiters = new HashSet<>();

            GravityCommand gravityCmd = cmd.getAnnotation(GravityCommand.class);
            if (gravityCmd != null) {
                if (!gravityCmd.allowPublic()) {
                    if (permittedGuilds.size() > 0) {
                        //limiters.add(new GuildLimiter(
                        //        ArrayUtils.toPrimitive(permittedGuilds.toArray(new Long[permittedGuilds.size()]))
                        //));
                    }

                    if (permittedRoles.size() > 0) {
                        //limiters.add(new RoleLimiter(
                        //        ArrayUtils.toPrimitive(permittedSudoers.toArray(new Long[permittedSudoers.size()]))
                        //));
                    }

                    if (permittedChannels.size() > 0) {
                        //limiters.add(new ChannelLimiter(
                        //        ArrayUtils.toPrimitive(permittedChannels.toArray(new Long[permittedChannels.size()]))
                        //));
                    }

                    if (permittedSudoers.size() > 0) {
                        limiters.add(new UserLimiter(
                                ArrayUtils.toPrimitive(permittedSudoers.toArray(new Long[permittedSudoers.size()]))
                        ));
                    }
                }

                String main = gravityCmd.value()[0];
                String[] aliases = new String[gravityCmd.value().length - 1];
                if (gravityCmd.value().length > 1) {
                    System.arraycopy(gravityCmd.value(), 1, aliases, 0, gravityCmd.value().length - 1);
                }

                registry.register(Command.builder()
                        .onCalled(commandContext -> {
                            try {
                                if (service.getActiveCount() <= 50) {
                                    service.submit(
                                            new CommandExecutor(method, obj, commandContext)
                                    ).get(10, TimeUnit.SECONDS);
                                } else {
                                    RequestBuffer.request(() -> commandContext.getChannel().sendMessage(new EmbedBuilder()
                                            .withTitle("Gravity is right now exhausted")
                                            .withAuthorName("Gravity")
                                            .withAuthorIcon(Gravity.getInstance().getConfig().getString("discord.avatar"))
                                            .withDescription("I'm very sorry but at the moment Gravity is serving over ")
                                            .appendDescription("**50** commands from **" + discordClient.getUsers().size() + "** ")
                                            .appendDescription("users across **" + discordClient.getGuilds().size() + "** guilds ")
                                            .appendDescription("using **" + discordClient.getShardCount() + "** shards.\n\n")
                                            .appendDescription("Please try again in a few seconds.")
                                            .appendField("Status", "Check the [status]() of Gravity", true)
                                            .appendField("Active Threads", "\u2265" +
                                                    ManagementFactory.getThreadMXBean().getThreadCount(), true)
                                            .appendField("Max. Threads", "50", true)
                                            .withColor(new Color(255, 0, 0))
                                            .build()));
                                }
                            } catch (Exception ex) {
                                if (ex instanceof TimeoutException) {
                                    RequestBuffer.request(() -> new MessageBuilder(discordClient)
                                            .withChannel(commandContext.getChannel())
                                            .withEmbed(new EmbedBuilder()
                                                    .withTitle("The command timed out.")
                                                    .withDescription("Command took over `10` seconds to complete. Aborted.")
                                                    .withColor(new Color(255, 0, 0))
                                                    .build())
                                            .build());
                                } else {
                                    StringBuilder builder = new StringBuilder();
                                    for (StackTraceElement e : ex.getStackTrace()) {
                                        builder.append("at").append(" ").append(e.toString()).append("\n");
                                    }

                                    RequestBuffer.request(() -> new MessageBuilder(discordClient)
                                            .withChannel(commandContext.getChannel())
                                            .withEmbed(new EmbedBuilder()
                                                    .withTitle(ex.getClass().getName() + ": " + ex.getMessage())
                                                    .withDescription(builder.toString())
                                                    .withColor(new Color(255, 0, 0))
                                                    .build())
                                            .build());

                                    Gravity.getInstance().getLogger().error("Error while executing command.", ex);
                                }
                            }
                        })
                        .limiters(limiters)
                        .build(), main, aliases);

                Gravity.getInstance().getLogger().info("Registered new command: " +
                        "Command: " + main + " - Aliases: " + Arrays.toString(aliases) + " - Method: " + method.toGenericString() +
                        "Public: " + gravityCmd.allowPublic() + " (" + limiters.size() + " limiters)");
            }
        }

        discordClient.getDispatcher().registerListener(this);
        discordClient.getDispatcher().registerListener(cmdListener = new CommandListener(registry));

        // Developers
        Iterator<String> devs = Gravity.getInstance().getConfig().getKeys("discord.assign.sudoers");

        while (devs.hasNext()) {
            String key = devs.next();
            devsMap.put(key.split(Pattern.quote("."))[key.split(Pattern.quote(".")).length - 1],
                    Gravity.getInstance().getConfig().getLong(key));
        }

        // Users
        Iterator<String> users = Gravity.getInstance().getConfig().getKeys("discord.assign.normies");

        while (users.hasNext()) {
            String key = users.next();
            usersMap.put(key.split(Pattern.quote("."))[key.split(Pattern.quote(".")).length - 1],
                    Gravity.getInstance().getConfig().getLong(key));
        }

        startTime = LocalDateTime.now();
    }

    public void destroy() {
        discordClient.getDispatcher().unregisterListener(cmdListener);
        discordClient.getDispatcher().unregisterListener(this);

        usersMap.clear();
        devsMap.clear();

        discordClient.logout();

        cmdListener = null;
        usersMap = null;
        devsMap = null;
        registry = null;
        discordClient = null;

        System.gc(); // Collect the garbage we set to null above
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        setStatusFuture(Gravity.getInstance().getScheduler().scheduleAtFixedRate(() -> {
            setCurrentTick(((getCurrentTick() + 1) > 3) ? 0 : getCurrentTick() + 1);
            final String streamUrl = Gravity.getInstance().getConfig().getString("discord.stream");

            switch (getCurrentTick()) {
                case 0: {
                    String playing = "for " + discordClient.getUsers().size() + " users";
                    RequestBuffer.request(() -> discordClient.streaming(playing, streamUrl));
                    break;
                }
                case 1: {
                    String playing = "in " + discordClient.getGuilds().size() + " guilds";
                    RequestBuffer.request(() -> discordClient.streaming(playing, streamUrl));
                    break;
                }
                case 3: {
                    String playing = "using " + discordClient.getShardCount() + " shard" +
                            (discordClient.getShardCount() != 1 ? "s" : "");
                    RequestBuffer.request(() -> discordClient.streaming(playing, streamUrl));
                    break;
                }
            }
        }, 0, 10, TimeUnit.SECONDS));
    }

    @EventSubscriber
    public void onMessage(MessageEvent event) {
        if (event.getMessage() != null) {
            String content = event.getMessage().getFormattedContent().toLowerCase().replaceAll("\\s+", "");

            // Bad Bot!
            if (content.contains("badbot")) {
                RequestBuffer.request(() -> {
                    MessageHistory history = event.getMessage().getChannel().getMessageHistory(2);
                    IMessage latestMessage = history.getEarliestMessage();

                    if (latestMessage.getAuthor().getLongID() == discordClient.getOurUser().getLongID()) {
                        new MessageBuilder(discordClient)
                                .withChannel(event.getChannel())
                                .withContent("(\u0E07 \u2022\u0300_\u2022\u0301)\u0E07 Fight me")
                                .build();
                    }
                });
            }

            // Good Bot!
            if (content.contains("goodbot")) {
                RequestBuffer.request(() -> {
                    MessageHistory history = event.getMessage().getChannel().getMessageHistory(2);
                    IMessage latestMessage = history.getEarliestMessage();

                    if (latestMessage.getAuthor().getLongID() == discordClient.getOurUser().getLongID()) {
                        new MessageBuilder(discordClient)
                                .withChannel(event.getChannel())
                                .withContent("(\uFF89\u25D5\u30EE\u25D5)\uFF89*:\uFF65\uFF9F\u2727 Thank you")
                                .build();
                    }
                });
            }

            if ((content.contains("titan") || ((content.contains("report") || content.contains("commend")) && content.contains("bot")))
                    && event.getMessage().getGuild().getLongID() != 350677899126243328L) {
                if (event.getAuthor().getLongID() != discordClient.getOurUser().getLongID()) {
                    new MessageBuilder(discordClient)
                            .withChannel(event.getChannel())
                            .withContent("The Titan bot is a advanced report & commend bot built " +
                                    "with performance and ease-of-use in mind. I highly suggest checking it out " +
                                    "and using it! https://github.com/Marc3842h/Titan")
                            .build();
                }
            }
        }
    }

    // v  BUILT-IN EVENTS  v

    @Subscribe
    public void onFreshVerifyID(FreshVerifyIDEvent event) {
        long snowflakeID = -1;
        if (usersMap.containsKey(event.getName())) {
            snowflakeID = usersMap.get(event.getName());
        }
        if (devsMap.containsKey(event.getName())) {
            snowflakeID = devsMap.get(event.getName());
        }

        IUser user = snowflakeID != -1 ? discordClient.fetchUser(snowflakeID) : null;

        new MessageBuilder(discordClient)
                .withChannel(Gravity.getInstance().getConfig().getLong("discord.channel"))
                .withEmbed(new EmbedBuilder()
                        .withTitle("Verify ID added")
                        .withDescription(
                                "**Name**: " + event.getName() + "\n" +
                                        "**Verify ID**: " + event.getVerifyID() + "\n" +
                                        "**Developer**: " + (event.isDev() ? "Yes" : "No")
                        )
                        .withColor(new Color(0, 171, 178))
                        .withAuthorName(event.getName())
                        .withAuthorIcon(user != null ? user.getAvatarURL() :
                                Gravity.getInstance().getConfig().getString("discord.avatar"))
                        .withTimestamp(LocalDateTime.now())
                        .build())
                .build();
    }

    @Subscribe
    public void onInvalidateVerifyID(InvalidateVerifyIDEvent event) {
        long snowflakeID = -1;
        if (usersMap.containsKey(event.getName())) {
            snowflakeID = usersMap.get(event.getName());
        }
        if (devsMap.containsKey(event.getName())) {
            snowflakeID = devsMap.get(event.getName());
        }

        IUser user = snowflakeID != -1 ? discordClient.fetchUser(snowflakeID) : null;

        new MessageBuilder(discordClient)
                .withChannel(Gravity.getInstance().getConfig().getLong("discord.channel"))
                .withEmbed(new EmbedBuilder()
                        .withTitle("Verify ID removed")
                        .withDescription(
                                (!event.getName().equals("__PENDING__") ? "**Name**: " + event.getName() + "\n" : "") +
                                        "**Verify ID**: " + event.getVerifyID()
                        )
                        .withColor(new Color(178, 90, 0))
                        .withAuthorName(event.getName().equals("__PENDING__") ? "Gravity" : event.getName())
                        .withAuthorIcon(user != null ? user.getAvatarURL() :
                                Gravity.getInstance().getConfig().getString("discord.avatar"))
                        .withTimestamp(LocalDateTime.now())
                        .build())
                .build();
    }

    @Subscribe
    public void onKillSwitch(KillSwitchEvent event) {
        User user = Gravity.getInstance().getEasyDatabase().getUserFromVerifyID(event.getVerifyID());
        long snowflakeID = -1;
        if (usersMap.containsKey(user.getName())) {
            snowflakeID = usersMap.get(user.getName());
        }
        if (devsMap.containsKey(user.getName())) {
            snowflakeID = devsMap.get(user.getName());
        }

        IUser discordUser = snowflakeID != -1 ? discordClient.fetchUser(snowflakeID) : null;

        new MessageBuilder(discordClient)
                .withChannel(Gravity.getInstance().getConfig().getLong("discord.channel"))
                .withEmbed(new EmbedBuilder()
                        .withTitle("Kill Switch activated")
                        .withDescription(
                                "**Verify ID**: " + event.getVerifyID()
                        )
                        .withColor(new Color(178, 61, 5))
                        .withAuthorName(user != User.NULL ? user.getName() : "Gravity")
                        .withAuthorIcon(discordUser != null ? discordUser.getAvatarURL() :
                                Gravity.getInstance().getConfig().getString("discord.avatar"))
                        .withTimestamp(LocalDateTime.now())
                        .build())
                .build();
    }

    @Subscribe
    public void onSuccessAuth(SuccessAuthEvent event) {
        long snowflakeID = -1;
        if (usersMap.containsKey(event.getUser().getName())) {
            snowflakeID = usersMap.get(event.getUser().getName());
        }
        if (devsMap.containsKey(event.getUser().getName())) {
            snowflakeID = devsMap.get(event.getUser().getName());
        }

        IUser user = snowflakeID != -1 ? discordClient.fetchUser(snowflakeID) : null;

        LocalDateTime time;
        try {
            time = LocalDateTime.parse(event.getRequest().getTimestamp());
        } catch (DateTimeException ex) {
            event.setCancelled(true);
            Gravity.getInstance().getLogger().warn("A invalid formatted timestamp ({}) has been sent.",
                    event.getRequest().getTimestamp());
            return;
        }

        new MessageBuilder(discordClient)
                .withChannel(Gravity.getInstance().getConfig().getLong("discord.channel"))
                .withEmbed(new EmbedBuilder()
                        .withTitle("User authenticated")
                        .withDescription(
                                "**Unique ID**: " + event.getUser().getUniqueID() + "\n" +
                                        "**Verify ID**: " + event.getUser().getVerifyID() + "\n" +
                                        "**IP Address**: " + event.getRequest().getTrace().getIpAddress() + " " +
                                        "(" + event.getContext().getRequest().getClientIp() + ") \n" +
                                        "**Developer**: " + event.getUser().isDev()
                        )
                        .withColor(new Color(43, 115, 178))
                        .withAuthorName(event.getUser().getName())
                        .withAuthorIcon(user != null ? user.getAvatarURL() :
                                Gravity.getInstance().getConfig().getString("discord.avatar"))
                        .withTimestamp(time)
                        .build())
                .build();
    }

    @Subscribe
    public void onFailedAuth(FailedAuthEvent event) {
        new MessageBuilder(discordClient)
                .withChannel(Gravity.getInstance().getConfig().getLong("discord.channel"))
                .withEmbed(new EmbedBuilder()
                        .withTitle("Failed Authentication request")
                        .withDescription(
                                "**Type**: " + event.getResponse().getMode() + " \n" +
                                        "**Message**: " + event.getResponse().getMessage() + " \n" +
                                        "**IP Address**: " + event.getRequestContext().getRequest().getClientIp() + "\n\n" +
                                        "**Body**: \n" + event.getRequestContext().getRequest().getBody()
                        )
                        .withColor(new Color(255, 0, 0))
                        .withAuthorName("Gravity")
                        .withAuthorIcon(Gravity.getInstance().getConfig().getString("discord.avatar"))
                        .withTimestamp(LocalDateTime.now())
                        .build())
                .build();
    }

}
