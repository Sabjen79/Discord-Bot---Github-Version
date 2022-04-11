package sabjen.DiscordBot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.VoiceChannel;
import sabjen.DiscordBot.commands.other.CleanupCommand;
import sabjen.DiscordBot.utils.Rand;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotTimers {
    private Map<Long, Long> cleanupDates;
    private Map<Long, Long> insultDates;
    private Long avatarDate;

    public BotTimers(JDA jda) {
        timerCleanup(jda);
        timerRandomSound(jda);
        timerAvatar(jda);
        timerRandomInsult(jda);
        //timerAnnounce(jda);

        openFile();
    }

    public ScheduledExecutorService newTimer() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    //=================================================================================
    //      FILES
    //=================================================================================

    public void openFile() {
        Gson gson = new Gson();
        try {
            BotTimers tmp = gson.fromJson(new FileReader(Bot.getBotPath() + "\\resource\\timers.json"), BotTimers.class);

            cleanupDates = tmp.cleanupDates;
            insultDates = tmp.insultDates;
            avatarDate = tmp.avatarDate;


        } catch (FileNotFoundException e) {
            System.out.println("NU MAI AM FISIERUL CU TIMERE...FAC ALTUL =}}}}");

            cleanupDates = new HashMap<>();
            insultDates = new HashMap<>();
            avatarDate = 0L;


            saveFile();
        }
    }

    public void saveFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        try(FileWriter writer = new FileWriter(Bot.getBotPath() + "\\resource\\timers.json")) {
            gson.toJson(this, writer);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    //=================================================================================
    //      CLEANUP DATES
    //=================================================================================

    public Long getCleanupDate(Guild guild) {
        openFile();

        Long guildId = guild.getIdLong();

        if(!cleanupDates.containsKey(guildId)) {
            cleanupDates.put(guildId, 0L);
        }

        return cleanupDates.get(guildId);
    }


    public void updateCleanupDate(Guild guild) {
        cleanupDates.put(guild.getIdLong(), new Date().getTime() + 60L * 60L * 1000L);

        saveFile();
    }

    //=================================================================================
    //      INSULT DATES
    //=================================================================================

    public void updateInsultDate(Guild guild) {
        insultDates.put(guild.getIdLong(), new Date().getTime() + 1000L * 60L * Rand.randomInt(120, 240));

        saveFile();
    }

    public boolean canInsult(Guild guild) {
        openFile();

        Long guildId = guild.getIdLong();

        if(!insultDates.containsKey(guildId)) {
            insultDates.put(guildId, 0L);
        }

        return (new Date().getTime() >= insultDates.get(guildId));
    }
    
    //=================================================================================
    //      T I M E R S
    //=================================================================================

    private void timerCleanup(JDA jda) {
        newTimer().scheduleAtFixedRate(() -> {
            for (Guild guild : jda.getGuilds()) {
                CleanupCommand command = Bot.get(guild).commands.cleanupCommand;

                if(command.canRandomCleanup()) {
                    command.cleanupAnnounce( guild.getMember(Bot.getUser()) );
                }
            }
        }, 5, 20, TimeUnit.MINUTES);
    }

    private void timerRandomSound(JDA jda) {
        newTimer().schedule(() -> {
            for(Guild guild : jda.getGuilds()) {
                Bot.get(guild).voice.playRandomSound();
            }

            timerRandomSound(jda);
        }, Rand.randomInt(10, 20), TimeUnit.MINUTES);
    }

    private void timerRandomInsult(JDA jda) {
        newTimer().schedule(() -> {
            for(Guild guild : jda.getGuilds()) {
                if(Bot.config.randomInsultActivityChance() && canInsult(guild) && Bot.get(guild).commands.randomInsult.activityInsult()) {
                    updateInsultDate(guild);
                }
            }

            timerRandomInsult(jda);
        }, 30, TimeUnit.MINUTES);
    }

    private void timerAvatar(JDA jda) {
        newTimer().schedule(() -> {

            if(new Date().getTime() >= avatarDate) {
                try {
                    File[] matchingFiles = new File(Bot.getBotPath() + "\\resource\\images\\").listFiles((file, s) -> s.startsWith("profile"));

                    jda.getSelfUser().getManager().setAvatar(Icon.from(Rand.randomFrom(matchingFiles))).queue();
                } catch(IOException e) {
                    e.printStackTrace();
                }

                avatarDate = new Date().getTime() + 1000L * 60 * 60 * 2;
                saveFile();
            }

            timerAvatar(jda);
        }, 5, TimeUnit.MINUTES);
    }
}
