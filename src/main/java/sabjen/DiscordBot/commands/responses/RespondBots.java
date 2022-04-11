package sabjen.DiscordBot.commands.responses;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.BotStrings;
import sabjen.DiscordBot.commands.Command;
import sabjen.DiscordBot.utils.MessageUtil;

import java.util.concurrent.TimeUnit;

public class RespondBots extends Command {

    //==================================================================================
    private BotStrings botInsults = new BotStrings(new String[]{
            "Fara alti boti pe server",
    });

    //==================================================================================

    public RespondBots() {
        super();
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        User author = event.getAuthor();
        if(!author.isBot() || Bot.isUserThisBot(author)) return false;

        return event.getMessage().isMentioned(Bot.getUser());
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        if(onCooldown) return;
        putOnCooldown(60, TimeUnit.MINUTES);

        String message = botInsults.getRandomString();

        MessageUtil.typeMessage( event.getMessage().reply(message) );
    }
}
