package sabjen.DiscordBot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.User;

public class TrackEvent {
    public AudioTrack audioTrack;
    public String trackUrl;
    public User author;

    public int volume;

    public boolean isHidden;
    public boolean isUnskippable;

    public TrackEvent(AudioTrack a, String t, User auth, boolean flag1, boolean flag2) {
        audioTrack = a;
        trackUrl = t;
        author = auth;

        isUnskippable = flag1;
        isHidden = flag2;
        volume = (isHidden) ? 100 : 20;
    }

}
