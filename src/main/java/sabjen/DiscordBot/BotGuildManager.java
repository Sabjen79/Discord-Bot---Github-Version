package sabjen.DiscordBot;

import net.dv8tion.jda.api.entities.*;
import sabjen.DiscordBot.core.VoiceConnectHandler;
import sabjen.DiscordBot.history.ChannelHistory;
import sabjen.DiscordBot.history.MusicChannelHistory;

import java.util.ArrayList;

public class BotGuildManager {
    private Guild guild;

    public TextChannel mainTextChannel;

    public TextChannel musicChannel;

    public TextChannel restrictedChannel;
    public Role restrictedRole;

    public ArrayList<ChannelHistory> channelHistories;
    public MusicChannelHistory musicChannelHistory;

    public BotCommands commands;
    public VoiceConnectHandler voice;

    public BotGuildManager(Guild g) {
        this.guild = g;

        //MAIN CHANNEL
        mainTextChannel = guild.getSystemChannel();

        //RESTRICTED CHANNEL
        for(TextChannel textChannel : guild.getTextChannels()) {
            if(Bot.config.isRestrictedKeyword(textChannel.getName())) {
                restrictedChannel = textChannel;
                break;
            }
        }

        if(restrictedChannel != null) {
            for(Role role : guild.getRoles()) {
                if(Bot.config.isRestrictedKeyword(role.getName())) {
                    restrictedRole = role;
                    break;
                }
            }
        }

        //MUSIC CHANNEL
        musicChannel = guild.getTextChannelsByName("music", true).get(0);

        channelHistories = new ArrayList<>();
        for(TextChannel channel : guild.getTextChannels()) {
            if(channel.getIdLong() != musicChannel.getIdLong()) {
                channelHistories.add(new ChannelHistory(channel));
            }
        }

        musicChannelHistory = new MusicChannelHistory(musicChannel);

        commands = new BotCommands(guild);
        voice = new VoiceConnectHandler(guild);
    }

}
