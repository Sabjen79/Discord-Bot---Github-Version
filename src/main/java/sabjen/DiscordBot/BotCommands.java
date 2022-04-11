package sabjen.DiscordBot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.commands.Command;
import sabjen.DiscordBot.commands.channels.RejectMusic;
import sabjen.DiscordBot.commands.channels.RejectRestricted;
import sabjen.DiscordBot.commands.music.MusicClear;
import sabjen.DiscordBot.commands.music.MusicLoop;
import sabjen.DiscordBot.commands.music.MusicPlay;
import sabjen.DiscordBot.commands.music.MusicSkip;
import sabjen.DiscordBot.commands.other.CleanupCommand;
import sabjen.DiscordBot.commands.other.UnknownCommand;
import sabjen.DiscordBot.commands.responses.*;

public class BotCommands {

    ////////////////////////////////////////////////////////////

    public RejectRestricted rejectRestricted;
    public RejectMusic rejectMusic;

    public MusicPlay musicPlay;
    public MusicSkip musicSkip;
    public MusicClear musicClear;
    public MusicLoop musicLoop;

    public ReactionImage reactionImage;
    public RespondBots respondBots;
    public RespondMention respondMention;

    public CleanupCommand cleanupCommand;
    public UnknownCommand unknownCommand;

    ////////////////////////////////////////////////////////////

    public RespondVoiceJoin respondVoiceJoin;
    public RandomInsult randomInsult;

    ////////////////////////////////////////////////////////////

    private Command[] messageCommands;

    public BotCommands(Guild guild) {
        messageCommands = new Command[]{
                rejectRestricted = new RejectRestricted(),

                musicPlay = new MusicPlay(guild),
                musicSkip = new MusicSkip(guild),
                musicClear = new MusicClear(),
                musicLoop = new MusicLoop(),
                rejectMusic = new RejectMusic(),

                cleanupCommand = new CleanupCommand(guild),

                respondBots = new RespondBots(),
                reactionImage = new ReactionImage(),
                respondMention = new RespondMention(),

                randomInsult = new RandomInsult(guild),
                unknownCommand = new UnknownCommand()
        };

        respondVoiceJoin = new RespondVoiceJoin();
    }

    public void checkAll(MessageReceivedEvent event) {
        if(!event.getMessage().isFromGuild()) return;

        for(Command c : messageCommands) {
            if(c.check(event)) {
                c.execute(event);
                if(c.stopAfterExecuted) return;
            }
        }
    }
}
