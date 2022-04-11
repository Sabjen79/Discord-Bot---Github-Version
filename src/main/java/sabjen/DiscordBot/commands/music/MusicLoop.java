package sabjen.DiscordBot.commands.music;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.Command;

public class MusicLoop extends Command {

    public MusicLoop() {
        super();
        stopAfterExecuted = true;
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().toLowerCase().replaceAll(" ", "");

        return message.equals(Bot.config.prefix + "loop") || message.equals(Bot.config.prefix + "l");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().delete().queue();

        if(!checkChannel(event, Bot.get(event.getGuild()).musicChannel)) return;

        Bot.musicManager.getGuildAudioPlayer(event.getGuild()).scheduler.switchLoop(event.getAuthor());
    }
}
