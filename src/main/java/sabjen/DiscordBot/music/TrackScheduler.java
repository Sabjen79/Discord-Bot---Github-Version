package sabjen.DiscordBot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.BotTimers;
import sabjen.DiscordBot.commands.music.MusicSkip;
import sabjen.DiscordBot.utils.MessageUtil;
import sabjen.DiscordBot.utils.Rand;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final ArrayList<TrackEvent> trackList;
    private MilestoneTimer milestoneTimer;

    public Guild guild;
    public boolean loop = false;
    public TrackEvent playingTrack = null;
    public String previousTrackName = null;

    public TrackScheduler(AudioPlayer player, Guild g) {
        guild = g;

        this.player = player;
        this.trackList = new ArrayList<>();
        this.milestoneTimer = new MilestoneTimer(this);

        player.setVolume(25);
        replayTimer();
        skipTimer();
    }

    public void queue(TrackEvent track) {
        if(!track.isHidden && !Bot.isUserThisBot(track.author) && queueContains(track)) {
            Bot.get(guild).musicChannel.sendMessage("Am pus asta deja").queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
            return;
        }

        trackList.add(track);
        sendQueueMessage(track, track.author);

        if(playingTrack == null) {
            nextTrack();
        }
    }

    public void skipTrack(User messageAuthor) {
        if(!messageAuthor.isBot() && playingTrack.isUnskippable && !playingTrack.audioTrack.getInfo().title.equals(previousTrackName)) {
            Bot.get(guild).commands.musicSkip.sendSkipMessage();
            return;
        }

        sendSkipMessage(playingTrack, messageAuthor);

        nextTrack();
    }

    public void clearQueue(MessageReceivedEvent event) {
        for(TrackEvent track : trackList) {
            if(Bot.isUserThisBot(track.author) && !track.isHidden) {
                event.getChannel().sendMessage("Dar poate nu vreau sa dau clear").queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
                return;
            }
        }

        sendClearMessage(event.getAuthor());

        trackList.clear();
    }

    public void switchLoop(User user) {
        loop = !loop;

        sendLoopMessage(user);
    }

    //--------------------------------------------------------------------------------

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        Bot.get(guild).commands.musicPlay.newConnectMessage();
        Bot.get(guild).commands.musicSkip.newSkipMessage();

        previousTrackName = track.getInfo().title;

        if (endReason.mayStartNext) {
            if(loop) player.startTrack(track.makeClone(), false);
            else nextTrack();
        }

    }

    public void nextTrack() {
        playingTrack = null;

        milestoneTimer.resetTimer();

        if(trackList.size() == 0) {
            player.startTrack(null, false);
            return;
        }

        TrackEvent track = trackList.remove(0);

        playingTrack = track;

        player.setVolume(track.volume);
        player.startTrack(track.audioTrack, false);

        milestoneTimer.startTimer();

        if(!Bot.isUserThisBot(track.author) && Bot.config.randomImmediateSkipChance()) {
            Bot.timers.newTimer().schedule(() -> {
                if(playingTrack != null && !Bot.isUserThisBot(track.author)) {
                    skipTrack(Bot.getUser());
                }

            }, Rand.randomInt(5, 30), TimeUnit.SECONDS);
        }

        if(track.isHidden && Bot.config.randomLoopChance() && !loop) {
            switchLoop(Bot.getUser());
        }
    }

    //--------------------------------------------------------------------------------

    public void resetQueue() {
        trackList.clear();
        milestoneTimer.resetTimer();
        nextTrack();
    }

    public void replayTimer() {
        Bot.timers.newTimer().schedule(() -> {
            AudioChannel v = Bot.get(guild).voice.getVoiceChannel();
            if(v != null) {
                List<String> links = Bot.get(guild).musicChannelHistory.getLinks();

                String url = links.get(1 + new Random().nextInt(links.size()-2));

                MessageUtil.log("AM PUS ASTA PE MUSIC: " + url);

                Bot.musicManager.loadAndPlay(url, v, Bot.getUser(), true, false);
            }

            replayTimer();
        }, Rand.randomInt(10, 20), TimeUnit.MINUTES);
    }

    public void skipTimer() {
        Bot.timers.newTimer().schedule(() -> {
            AudioChannel v = Bot.get(guild).voice.getVoiceChannel();

            if(v != null && playingTrack != null) {
                if(playingTrack.isUnskippable && playingTrack.audioTrack.getDuration() > 1000 * 60 * 7) {
                    //skipTrack(Bot.getUser());
                    playingTrack.isUnskippable = false;
                }
            }

            skipTimer();
        }, Rand.randomInt(7, 10), TimeUnit.MINUTES);
    }

    public boolean queueContains(TrackEvent track) {
        if(playingTrack == null || playingTrack.audioTrack.getInfo() == null) return false;

        if(playingTrack.audioTrack.getInfo() != null && playingTrack.audioTrack.getInfo().title.equals(track.audioTrack.getInfo().title)) return true;

        for(TrackEvent t : trackList) {
            if(t.audioTrack.getInfo() != null && t.audioTrack.getInfo().title.equals(track.audioTrack.getInfo().title)) return true;
        }

        return false;
    }

    //--------------------------------------------------------------------------------

    private void sendQueueMessage(TrackEvent track, User author) {
        if(track.isHidden) return;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(author.getName() + " a aduagat", null, author.getAvatarUrl());
        eb.setTitle(track.audioTrack.getInfo().title, track.trackUrl);
        eb.addField("Author", track.audioTrack.getInfo().author, true);
        eb.addField("Duration", new SimpleDateFormat("HH:mm:ss").format(new Date(track.audioTrack.getInfo().length - 1000*60*60*2)), true);
        eb.setColor(Color.WHITE);

        Bot.get(guild).musicChannel.sendMessageEmbeds(eb.build()).queue();
    }

    private void sendSkipMessage(TrackEvent track, User author) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(author.getName() + " a dat skip la", null, author.getAvatarUrl());
        eb.setTitle(track.audioTrack.getInfo().title);
        eb.setColor(Color.RED);

        Bot.get(guild).musicChannel.sendMessageEmbeds(eb.build()).queue();
    }

    private void sendLoopMessage(User user) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(user.getName() + " a setat", null, user.getAvatarUrl());
        if(loop) eb.setTitle("LOOP ON");
        else eb.setTitle("LOOP OFF");
        eb.setColor(Color.BLUE);

        Bot.get(guild).musicChannel.sendMessageEmbeds(eb.build()).queue();
    }

    private void sendClearMessage(User user) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(user.getName() + " a dat clear la lista", null, user.getAvatarUrl());
        eb.setColor(Color.GREEN);

        Bot.get(guild).musicChannel.sendMessageEmbeds(eb.build()).queue();
    }

    //--------------------------------------------------------------------------------

}