package sabjen.DiscordBot.history;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import sabjen.DiscordBot.Bot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChannelHistory {
    //===================================================================================

    public static class MessageInfo {
        public String content;
        public Long messageID;
        public Long authorID;

        public MessageInfo(Message message) {
            content = message.getContentRaw();
            authorID = message.getAuthor().getIdLong();
            messageID = message.getIdLong();
        }
    }

    //===================================================================================

    private TextChannel channel;
    private List<MessageInfo> fullHistory;


    public ChannelHistory(TextChannel c) {
        channel = c;
    }

    //===================================================================================

    public List<MessageInfo> getHistory() {
        updateMessages();

        return fullHistory;
    }

    public void updateMessages() {

        if(fullHistory == null) {
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<ArrayList<MessageInfo>>(){}.getType();
                fullHistory = gson.fromJson(new FileReader(Bot.getBotPath() + "\\resource\\messages\\" + channel.getId() + ".json"), listType);
            } catch (FileNotFoundException e) {
                System.out.println("NU GASESC FISIERUL CU MESAJE, BAFTA");

                saveAllMessages();
                return;
            }
        }

        MessageHistory history = MessageHistory.getHistoryAfter(channel, fullHistory.get(0).messageID.toString()).complete();

        List<Message> newMessages = new ArrayList<>(history.getRetrievedHistory());

        while(!history.isEmpty()) {
            System.out.println("AM GASIT: " + newMessages.size() + " MESAJE");

            history = MessageHistory.getHistoryAfter(channel, newMessages.get(0).getId()).limit(100).complete();

            newMessages.addAll(0, history.getRetrievedHistory());
        }

        for(int i = newMessages.size()-1; i >= 0; i--) {
            fullHistory.add(0, new MessageInfo(newMessages.get(i)));
        }

        saveFile();
    }

    public void saveAllMessages() {
        MessageHistory history = MessageHistory.getHistoryBefore(channel, channel.getLatestMessageId()).complete();

        List<Message> allMessages = new ArrayList<>(history.getRetrievedHistory());

        while(history.getRetrievedHistory().size() > 1) {
            System.out.println("Am gasit pana acum: " + allMessages.size() + " mesaje");

            List<Message> messages = history.getRetrievedHistory();

            history = MessageHistory.getHistoryBefore(channel, messages.get(messages.size()-1).getId()).limit(100).complete();

            allMessages.addAll(history.getRetrievedHistory());
        }

        fullHistory = new ArrayList<>();
        for(Message message : allMessages) {
            fullHistory.add(new MessageInfo(message));
        }

        saveFile();

        System.out.println("GATA BA");
    }

    public void saveFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try(FileWriter writer = new FileWriter(Bot.getBotPath() + "\\resource\\messages\\" + channel.getId() + ".json")) {
            gson.toJson(fullHistory, writer);
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }
}
