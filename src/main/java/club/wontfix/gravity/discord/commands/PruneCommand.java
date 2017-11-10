package club.wontfix.gravity.discord.commands;

import club.wontfix.gravity.discord.GravityCommand;
import com.darichey.discord.CommandContext;

import java.util.function.Consumer;

@GravityCommand(value = {"prune", "delete"}, description = "Deletes messages")
public class PruneCommand implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {

        context.getChannel().sendMessage("Disabled because of Myrrib! :(");

        return;
        /*
        if (context.getArgs().size() == 1) {
            if (context.getAuthor().getPermissionsForGuild(context.getGuild()).contains(Permissions.MANAGE_MESSAGES)) {
                int amount;
                try {
                    amount = Integer.parseInt(context.getArgs().get(0));
                } catch (NumberFormatException ex) {
                    context.getChannel().sendMessage(
                            new EmbedBuilder()
                                    .withDescription("Please provide a valid number.")
                                    .withColor(new Color(255, 0, 0))
                                    .build());
                    return;
                }

                if (amount <= 0) {
                    context.getChannel().sendMessage(
                            new EmbedBuilder()
                                    .withDescription("Please provide a positive number.")
                                    .withColor(new Color(255, 0, 0))
                                    .build());
                    return;
                }

                final MessageHistory[] history = new MessageHistory[1];

                RequestBuffer.RequestFuture<Void> future = RequestBuffer.request(() ->
                        history[0] = context.getChannel().getMessageHistory(amount + 1)
                );

                while (!future.isDone()) {  Wait for completion of the future task before deleting messages/ }

                for (IMessage msg : history[0].asArray()) {
                    RequestBuffer.request(msg::delete);
                }

                RequestBuffer.request(() -> {
                    IMessage msg = new MessageBuilder(Gravity.getInstance().getDiscordBotManager().getDiscordClient())
                            .withChannel(context.getChannel())
                            .withEmbed(new EmbedBuilder()
                                    .withTitle("Success!")
                                    .withDescription("Gravity has removed `" + amount + "` messages from this channel.")
                                    .withColor(new Color(43, 115, 178))
                                    .build())
                            .build();

                    Gravity.getInstance().getScheduler().schedule(msg::delete, 5, TimeUnit.SECONDS);
                });
            }
        } else {
            //context.getChannel().sendMessage("Please provide a amount of messages to be pruned.");
        }*/
    }

}
