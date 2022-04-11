package sabjen.DiscordBot.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.Command;
import sabjen.DiscordBot.utils.Rand;

import java.util.concurrent.TimeUnit;

public class MusicSkip extends Command {
    private Guild guild;
    //==================================================================================================================

    private String[] skipMessages = {
            "Nu dau skip la asta, imi place",
            "Skip? Imediat, mai asteapta putin"
    };

    private String currentSkipMessage;

    public void newSkipMessage() {
        currentSkipMessage = Rand.randomFrom(skipMessages);
    }

    public void sendSkipMessage() {
        Bot.get(guild).musicChannel.sendMessage(currentSkipMessage).queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
    }

    //==================================================================================================================

    public MusicSkip(Guild g) {
        super();
        stopAfterExecuted = true;

        guild = g;
        newSkipMessage();
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        String text = event.getMessage().getContentRaw().toLowerCase().replaceAll(" ","");

        return text.equals(Bot.config.prefix + "s") || text.equals(Bot.config.prefix + "skip");
    }

    public void execute(MessageReceivedEvent event) {
        event.getMessage().delete().queue();

        if(!checkChannel(event, Bot.get(event.getGuild()).musicChannel)) return;

        if(Bot.musicManager.getGuildAudioPlayer(event.getGuild()).player.getPlayingTrack() != null) {
            Bot.musicManager.skipTrack(event);
        } else {
            event.getChannel().sendMessage("NU AM LA CE SA DAU SKIP").queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
        }
    }
}
