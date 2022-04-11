package sabjen.DiscordBot.history;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import sabjen.DiscordBot.Bot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MusicChannelHistory {

    TextChannel channel;
    private List<String> links;

    public MusicChannelHistory(TextChannel c) {
        channel = c;
    }

    public List<String> getLinks() {
        updateLinks();

        return links;
    }

    private void updateLinks() {
        if(links == null) {
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                links = gson.fromJson(new FileReader(Bot.getBotPath() + "\\resource\\messages\\" + channel.getId() + ".json"), listType);
            } catch (FileNotFoundException e) {
                System.out.println("NU GASESC FISIERUL CU LINK-URI, BAFTA");

                saveAllLinks();
                return;
            }
        }

        long mostRecentMessageDate = Long.parseLong(links.get(0));
        long updatedDate = 0;

        List<String> newUrls = new ArrayList<>();

        for(Message message : channel.getIterableHistory().cache(false)) {
            if(updatedDate == 0) {
                updatedDate = message.getTimeCreated().toEpochSecond() * 1000;
            }

            if(message.getTimeCreated().toEpochSecond() * 1000 <= mostRecentMessageDate) break;

            if(message.getEmbeds().size() > 0 && Bot.isUserThisBot(message.getAuthor())) {
                String url = message.getEmbeds().get(0).getUrl();

                if(url != null && !links.contains(url) && !newUrls.contains(url)) newUrls.add(url);
            }
        }

        links.set(0, String.valueOf(updatedDate));
        links.addAll(1, newUrls);

        saveFile();
        //System.out.println("GATA BA");
    }

    private void saveAllLinks() {
        links = new ArrayList<>();

        long n = 0;
        for(Message message : channel.getIterableHistory().cache(false)) {
            System.out.println(n++);
            if(links.size() == 0) links.add( String.valueOf(message.getTimeCreated().toEpochSecond() * 1000) );

            if(message.getEmbeds().size() > 0 && Bot.isUserThisBot(message.getAuthor())) {
                String url = message.getEmbeds().get(0).getUrl();

                if(url != null && !links.contains(url)) links.add(url);
            }
        }

        saveFile();
        System.out.println("GATA BA");
    }


    private void saveFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        try(FileWriter writer = new FileWriter(Bot.getBotPath() + "\\resource\\messages\\" + channel.getId() + ".json")) {
            gson.toJson(links, writer);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }
}
