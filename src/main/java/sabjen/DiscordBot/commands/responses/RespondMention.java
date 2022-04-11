package sabjen.DiscordBot.commands.responses;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.BotStrings;
import sabjen.DiscordBot.commands.Command;
import sabjen.DiscordBot.utils.MessageUtil;
import sabjen.DiscordBot.utils.Rand;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class RespondMention extends Command {

    //==================================================================================

    private BotStrings responses = new BotStrings(new String[]{
            "Te rog iesi de pe server #mention",
    });

    private BotStrings secondResponses = new BotStrings(new String[]{
            "Ti-am zis sa iesi",
    });

    //==================================================================================

    public RespondMention() {
        super();
    }

    @Override
    public boolean check(MessageReceivedEvent event) {

        for(Member member : event.getMessage().getMentionedMembers()) {
            if(Bot.isUserThisBot(member)) return true;
        }

        return event.getMessage().getContentRaw().toLowerCase().contains("bot");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        if(onCooldown) return;
        putOnCooldown(20, TimeUnit.MINUTES);

        String text = responses.getRandomString();

        MessageAction action = event.getMessage().reply(text);

        if(text.contains("#")) {
            String[] tokens = text.split("#");

            action.addFile(Bot.randomImage(tokens[1]));
            action.content(tokens[0]);
        }



        MessageUtil.typeMessage(action, (messageAction) -> {
            Bot.eventWaiter.waitForEvent(
                    MessageReceivedEvent.class,
                    e -> e.getChannel().getIdLong() == event.getChannel().getIdLong() &&
                         e.getAuthor().getIdLong() == event.getMessage().getAuthor().getIdLong(),

                    e -> MessageUtil.typeMessage(e.getMessage().reply(secondResponses.getRandomString())),

                    15, TimeUnit.SECONDS,
                    () -> {}
            );
        });
    }
}
