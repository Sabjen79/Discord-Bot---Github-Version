package sabjen.DiscordBot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.music.MusicPlay;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MusicManager {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public Map<Long, GuildMusicManager> getMusicManagers() {
        return musicManagers;
    }

    public MusicManager(JDA jda) {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(final String trackUrl, AudioChannel voiceChannel, User author, boolean isUnskippable, boolean isHidden) {
        GuildMusicManager musicManager = getGuildAudioPlayer(voiceChannel.getGuild());
        Guild guild = voiceChannel.getGuild();

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                AudioChannel botChannel = Bot.get(guild).voice.getVoiceChannel();

                if(musicManager.scheduler.playingTrack == null || voiceChannel == botChannel) {
                    Bot.get(guild).voice.connectTo(voiceChannel);
                } else {

                    Bot.get(guild).commands.musicPlay.sendConnectMessage();
                    return;
                }

                musicManager.scheduler.queue(new TrackEvent(track, trackUrl, author, isUnskippable, isHidden));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                trackLoaded(firstTrack);
            }

            @Override
            public void noMatches() {
                Bot.get(guild).musicChannel.sendMessage("NU AM GASIT NIMIC").queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                Bot.get(guild).musicChannel.sendMessage("Nu pot sa dau play: " + exception.getMessage()).queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
            }
        });
    }

    public void skipTrack(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());

        musicManager.scheduler.skipTrack(event.getAuthor());
    }
}