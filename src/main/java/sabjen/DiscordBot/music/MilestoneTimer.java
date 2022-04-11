package sabjen.DiscordBot.music;

import net.dv8tion.jda.api.entities.Message;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.utils.Rand;

import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MilestoneTimer {
    private TrackScheduler scheduler;
    private ScheduledExecutorService timer;
    private Future<?> future;

    private Message lastMilestoneMessage;
    private int minutesPlaying;

    private HashMap<Integer, String> musicMilestones;

    //==================================================================================

    private String[] milestoneResponses = {
            "sunteti ok?",
            "se vede ca va place",
            "stiti ca puteti sa dati skip, nu?"
    };

    //==================================================================================


    public MilestoneTimer(TrackScheduler s) {
        this.scheduler = s;
        timer = Bot.timers.newTimer();

        minutesPlaying = 0;

        musicMilestones = new HashMap<>();
        musicMilestones.put(15, "Ascultati `$` de **15 minute**...#");
        musicMilestones.put(30, "Ascultati `$` de **30 minute**...#");
        musicMilestones.put(45, "Ascultati `$` de **45 minute**...#");
        musicMilestones.put(60, "Ascultati `$` de **1 ORA**...#");
        musicMilestones.put(90, "Ascultati `$` de **1 ORA SI 30 DE MINUTE**...va rog sa dati skip");
        musicMilestones.put(120, "Ascultati `$` de **2 ORE**");
        musicMilestones.put(180, "Ascultati `$` de **3 ORE**");
    }

    public void startTimer() {
        future = timer.scheduleAtFixedRate(() -> {
            if (scheduler.playingTrack == null) {
                minutesPlaying = 0;
                return;
            }

            minutesPlaying++;

            if(!musicMilestones.containsKey(minutesPlaying)) return;

            if(Bot.get(scheduler.guild).voice.findNewVoiceChannel()) {
                scheduler.skipTrack(Bot.getUser());
                return;
            }

            String text = musicMilestones.get(minutesPlaying)
                    .replace("$", scheduler.playingTrack.audioTrack.getInfo().title)
                    .replace("#", Rand.randomFrom(milestoneResponses));

            if(lastMilestoneMessage != null) lastMilestoneMessage.delete().queue();
            Bot.get(scheduler.guild).musicChannel.sendMessage(text).queue(message -> lastMilestoneMessage = message);

        }, 1, 1, TimeUnit.MINUTES);

    }

    public void resetTimer() {
        if(future != null) future.cancel(true);
        minutesPlaying = 0;
        lastMilestoneMessage = null;
    }

}
