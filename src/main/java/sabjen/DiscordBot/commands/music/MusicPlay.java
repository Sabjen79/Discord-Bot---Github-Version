package sabjen.DiscordBot.commands.music;


import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.Command;
import sabjen.DiscordBot.utils.JsonReader;
import sabjen.DiscordBot.utils.Rand;

import java.util.concurrent.TimeUnit;

public class MusicPlay extends Command {
    private Guild guild;
    //==================================================================================================================

    private String[] connectMessages = {
            "Hai pe voice channel-ul pe care sunt eu",
            "NU! Vino pe voice channel-ul pe care sunt si eu"
    };

    private String currentConnectMessage;

    public void newConnectMessage() {
        currentConnectMessage = Rand.randomFrom(connectMessages);
    }

    public void sendConnectMessage() {
        Bot.get(guild).musicChannel.sendMessage(currentConnectMessage).queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
    }

    //==================================================================================================================

    public MusicPlay(Guild g) {
        super();
        stopAfterExecuted = true;
        guild = g;

        newConnectMessage();
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw().toLowerCase();

        return content.startsWith(Bot.config.prefix + "p ") || content.startsWith(Bot.config.prefix + "play ");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().delete().queue();

        if(!checkChannel(event, Bot.get(event.getGuild()).musicChannel)) return;

        AudioChannel vChannel = event.getMember().getVoiceState().getChannel();

        if(vChannel == null) {
            event.getTextChannel().sendMessage("Intra mai intai pe un voice channel").queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
            return;
        }

        String finalUrl = event.getMessage().getContentRaw().split(" ", 2)[1];

        if(!finalUrl.startsWith("http")) {
            try {
                String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&order=relevance&q="
                        + finalUrl.replaceAll(" ", "+") 
                        + "YOUTUBE-KEY-HERE";
                String videoId = JsonReader.readJsonFromUrl(url).getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");

                finalUrl = "https://www.youtube.com/watch?v=" + videoId;
            } catch (Exception e) {
                System.out.println(e.toString());

                if(e.toString().contains("JSONArray[0] not found")) {
                    event.getChannel().sendMessage("NU AM GASIT NIMIC").queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
                    return;
                }
            }
        }

        Bot.musicManager.loadAndPlay(finalUrl, vChannel, event.getAuthor(), Bot.config.randomSkipChance(), false);
    }
}
