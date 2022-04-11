package sabjen.DiscordBot.commands.channels;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.Command;

import java.util.concurrent.TimeUnit;

public class RejectMusic extends Command {

    public RejectMusic() {
        super();
        stopAfterExecuted = true;
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        return event.getChannel().getIdLong() == Bot.get(event.getGuild()).musicChannel.getIdLong();
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().delete().queue();

        event.getChannel().sendMessage("AICI PUN DOAR MUZICA").queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
    }
}
