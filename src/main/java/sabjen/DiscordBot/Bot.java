package sabjen.DiscordBot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeHttpContextFilter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import sabjen.DiscordBot.core.EventListener;
import sabjen.DiscordBot.music.MusicManager;
import sabjen.DiscordBot.utils.Rand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class Bot implements net.dv8tion.jda.api.hooks.EventListener {
    private static final String ID = "";
    private static final String TOKEN = "";

    public static JDA jda;
    public static MusicManager musicManager;
    public static Map<Long, BotGuildManager> guildManagers;

    public static BotConfiguration config;
    public static BotTimers timers;

    public static EventWaiter eventWaiter;


    public static void main(String[] args) throws Exception {
        eventWaiter = new EventWaiter();

        jda = JDABuilder
                .createDefault(TOKEN)

                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .setMemberCachePolicy(MemberCachePolicy.ALL) // ignored if chunking enabled
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .enableCache(CacheFlag.ACTIVITY)

                .addEventListeners(new EventListener())
                .addEventListeners(eventWaiter)
                .setAudioSendFactory(new NativeAudioSendFactory())

                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .build();

        musicManager = new MusicManager(jda);

        guildManagers = new HashMap<>();

        config = new BotConfiguration();
        timers = new BotTimers(jda);

        YoutubeHttpContextFilter.setPAPISID("tY4MP2xSaIBSCDzf/AjrsT0dmMLG2F7-gd");
        YoutubeHttpContextFilter.setPSID("FwhLXGSv-_K2GQZzz3JaHWkf_m0AwJWJILSqwe_-BwG-CqgOznwXZKKQOfDRtMBkz3e7eA.");
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent)
            System.out.println("API is ready!");
    }

    public static String getBotPath() {
        return new File("").getAbsolutePath();
    }

    public static File randomImage(String keyword) {
        File[] matchingFiles = new File(Bot.getBotPath() + "\\resource\\images\\").listFiles((file, s) -> s.startsWith(keyword));
        if(matchingFiles == null) return null;

        return Rand.randomFrom(matchingFiles);
    }

    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------

    public static BotGuildManager get(Guild guild) {
        if(!guildManagers.containsKey(guild.getIdLong())) {
            guildManagers.put(guild.getIdLong(), new BotGuildManager(guild));
        }

        return guildManagers.get(guild.getIdLong());
    }

    public static BotCommands getCommands(Guild guild) {
        return get(guild).commands;
    }

    public static User getUser() {
        return jda.getSelfUser();
    }

    public static Member getMember(Guild guild) {
        return guild.getMemberById(ID);
    }

    public static boolean isUserThisBot(User user) {
        return user.getId().equals(Bot.ID);
    }

    public static boolean isUserThisBot(Member member) {
        return member.getUser().getId().equals(Bot.ID);
    }
}