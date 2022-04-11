package sabjen.DiscordBot.core;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.utils.MessageUtil;
import sabjen.DiscordBot.utils.Rand;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(Bot.isUserThisBot(event.getAuthor())) return;
        /*
        if(!event.getMessage().isFromGuild()) {
            MessageUtil.log(event.getAuthor().getName() + " a scris: " + event.getMessage().getContentRaw());
            event.getAuthor().openPrivateChannel().queue((channel) -> {
                MessageUtil.typeMessage(channel.sendMessage("Nu e destul ca te suport pe server? Trebuie si aici?"));
            });
            return;
        }
        */

        Guild guild = event.getGuild();

        Bot.musicManager.getGuildAudioPlayer(guild);

        if(event.getMessage().getContentRaw().equals("testme") && event.getAuthor().getIdLong() == event.getGuild().getOwnerIdLong()) {
            //Bot.get(Bot.jda.getGuildById("613486318219034624")).musicChannelHistory.getLinks();
        }

        Bot.getCommands(guild).checkAll(event);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("mars")) {
            event.reply("Mars").queue((message) -> {
                message.deleteOriginal().queueAfter(Rand.randomInt(10, 15), TimeUnit.SECONDS);
            });
        }
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        if(Bot.isUserThisBot(event.getMember())) {
            Bot.get(event.getGuild()).voice.playRandomSound();
            return;
        }

        Bot.get(event.getGuild()).voice.findNewVoiceChannel();
    }

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        if(Bot.isUserThisBot(event.getMember())) {
            Bot.get(event.getGuild()).voice.playRandomSound();
            return;
        }

        Bot.getCommands(event.getGuild()).respondVoiceJoin.execute(event);
        Bot.get(event.getGuild()).voice.findNewVoiceChannel();
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        if(Bot.isUserThisBot(event.getMember())) return;

        Bot.get(event.getGuild()).voice.findNewVoiceChannel();
    }

    @Override
    public void onGenericMessageReaction(@Nonnull GenericMessageReactionEvent event) {
        Bot.musicManager.getGuildAudioPlayer(event.getGuild());
    }

    @Override
    public void onShutdown(@Nonnull ShutdownEvent event) {
        Bot.jda.getPresence().setStatus(OnlineStatus.INVISIBLE);
    }
}
