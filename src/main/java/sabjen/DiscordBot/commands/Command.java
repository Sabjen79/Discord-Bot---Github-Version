package sabjen.DiscordBot.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;

import java.util.concurrent.TimeUnit;

public abstract class Command {
    public boolean stopAfterExecuted;
    public boolean onCooldown;

    public Command() {
        stopAfterExecuted = false;
        onCooldown = false;
    }

    public void putOnCooldown(long delay, TimeUnit t) {
        onCooldown = true;

        Bot.timers.newTimer().schedule(() -> {
            onCooldown = false;
        }, delay, t);
    }

    public abstract boolean check(MessageReceivedEvent event);
    public abstract void execute(MessageReceivedEvent event);

    public boolean checkChannel(MessageReceivedEvent event, TextChannel tx) {
        if(event.getChannel().getIdLong() != tx.getIdLong()) {
            event.getChannel().sendMessage("NU FAC ASTA DECAT PE " + tx.getAsMention()).queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));

            return false;
        }

        return true;
    }
}
