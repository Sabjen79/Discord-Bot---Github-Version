package sabjen.DiscordBot.commands.responses;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.Command;
import sabjen.DiscordBot.utils.Rand;

import java.util.concurrent.TimeUnit;

public class ReactionImage extends Command {

    //==================================================================================

    private String[] reactions = {
            "\uD83D\uDE02", //joy
            "\uD83D\uDC4D", //like
            "\uD83D\uDE21", //angry
            "\uD83D\uDE0D", //love
            "\uD83E\uDDD0", //monocle
            "\uD83D\uDE33"  //flushed
    };

    //==================================================================================

    public ReactionImage() {
        super();
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        if(Rand.randomChance(0.4)) {
            return event.getMessage().getContentRaw().startsWith("https://cdn.discordapp.com/attachments/") || event.getMessage().getAttachments().size() > 0;
        }

        return false;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        if(onCooldown) return;
        putOnCooldown(20, TimeUnit.MINUTES);

        event.getMessage().addReaction( Rand.randomFrom(reactions) ).queueAfter(Rand.randomInt(5, 10), TimeUnit.SECONDS);
    }
}
