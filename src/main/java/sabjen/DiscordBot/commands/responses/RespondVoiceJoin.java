package sabjen.DiscordBot.commands.responses;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.BotStrings;
import sabjen.DiscordBot.utils.MessageUtil;
import sabjen.DiscordBot.utils.Rand;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class RespondVoiceJoin {
    private boolean onCooldown = false;

    private BotStrings responses = new BotStrings(new String[]{
            "@ ai intrat pe server-ul gresit, poti sa iesi",
            "@ ti-am dat cumva permisiunea sa intri pe voice channel?"
    });

    public void execute(@Nonnull GuildVoiceJoinEvent event) {
        if(onCooldown || Bot.get(event.getGuild()).voice.getVoiceChannel() == null) return;
        onCooldown = true;

        Bot.timers.newTimer().schedule(() -> {
            onCooldown = false;
        }, 30, TimeUnit.MINUTES);

        if(!Rand.randomChance(0.20)) return;

        String message = responses.getRandomString().replace("@", event.getMember().getAsMention());
        MessageAction action = Bot.get(event.getGuild()).mainTextChannel.sendMessage(message);
        MessageUtil.typeMessage(action);
    }
}
