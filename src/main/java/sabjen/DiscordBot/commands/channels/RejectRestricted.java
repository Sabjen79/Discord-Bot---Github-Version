package sabjen.DiscordBot.commands.channels;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.Command;

import java.util.concurrent.TimeUnit;

public class RejectRestricted extends Command {
    public RejectRestricted() {
        super();
        stopAfterExecuted = true;
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        TextChannel restrictedChannel = Bot.get(event.getGuild()).restrictedChannel;
        if(restrictedChannel == null || event.getTextChannel() == restrictedChannel) return false;

        return event.getMember().getRoles().contains(Bot.get(event.getGuild()).restrictedRole);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        TextChannel restrictedChannel = Bot.get(event.getGuild()).restrictedChannel;

        String messageString = "DACA ESTI RESTRICTIONAT DU-TE PE " + restrictedChannel.getAsMention();

        event.getMessage().delete().queue();
        event.getChannel().sendMessage(messageString).queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
    }
}
