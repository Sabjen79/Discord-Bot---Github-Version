package sabjen.DiscordBot.core;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.music.TrackEvent;
import sabjen.DiscordBot.utils.Rand;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VoiceConnectHandler extends ListenerAdapter {

    private Guild guild;

    public VoiceConnectHandler(Guild g) {
        guild = g;

    }

    public boolean findNewVoiceChannel() {
        Bot.musicManager.getGuildAudioPlayer(guild);
        AudioChannel botVc = getVoiceChannel();
        AudioChannel idealVc = getIdealVoiceChannel();

        if(idealVc == null && botVc == null) return false;

        if(idealVc == null) {
            disconnect();
            return true;
        }

        if(botVc == null || botVc.getIdLong() != idealVc.getIdLong()) {
            connectTo(idealVc);
            return true;
        }

        return false;
    }

    //========================================================================================================================

    public void connectTo(AudioChannel vc) {
        if(vc == null) return;

        guild.getAudioManager().openAudioConnection(vc);
    }

    public void disconnect() {
        guild.getAudioManager().closeAudioConnection();
        Bot.musicManager.getGuildAudioPlayer(guild).scheduler.resetQueue();
    }

    public AudioChannel getVoiceChannel() {
        GuildVoiceState v = Bot.getMember(guild).getVoiceState();

        return (v == null) ? null : v.getChannel();
    }

    //========================================================================================================================

    public void playRandomSound() {
        AudioChannel vChannel = getVoiceChannel();
        TrackEvent playingTrack = Bot.musicManager.getGuildAudioPlayer(guild).scheduler.playingTrack;

        if(vChannel != null && playingTrack == null) {
            String folder = Bot.getBotPath().concat("\\resource\\randomSounds\\");

            String path = folder + Rand.randomFrom(new File(folder).list());

            Bot.musicManager.loadAndPlay(path, vChannel, Bot.getUser(), true, true);
        }
    }

    //========================================================================================================================

    private AudioChannel getIdealVoiceChannel() {
        AudioChannel botVc = getVoiceChannel();

        if(getVoiceChannelScore(botVc) > 0) return botVc;

        VoiceChannel target = null;
        int targetScore = 0;

        for(VoiceChannel vc : guild.getVoiceChannels()) {
            int vcScore = getVoiceChannelScore(vc);

            if(vcScore > targetScore) {
                targetScore = vcScore;
                target = vc;
            }
        }

        return target;
    }

    private int getVoiceChannelScore(AudioChannel voiceChannel) {
        if(voiceChannel == null) return 0;

        VoiceChannel afk = guild.getAfkChannel();
        if(afk != null && voiceChannel.getIdLong() == afk.getIdLong()) return 0;

        int score = 0;

        for(Member member : voiceChannel.getMembers()) {
            if(!member.getVoiceState().isDeafened() && !member.getUser().isBot()) score++;
        }

        return score;
    }

    //========================================================================================================================

}
