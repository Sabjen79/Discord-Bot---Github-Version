package sabjen.DiscordBot.commands.other;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.Command;

import java.util.concurrent.TimeUnit;
public class UnknownCommand extends Command {

    public UnknownCommand() {
        super();
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().replaceAll(" ", "");

        return message.startsWith(Bot.config.prefix);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().delete().queue();
        event.getChannel().sendMessage("???").queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
    }
}
