package sabjen.DiscordBot.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.poi.ss.formula.functions.T;
import sabjen.DiscordBot.Bot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class MessageUtil {
    public static void log(String s) {
        System.out.println(new SimpleDateFormat("kk:mm:ss.SS").format(new Date()) + " " + s);
    }

    public static void typeMessage(MessageAction action) {
        typeMessage(action, null);
    }

    public static void typeMessage(MessageAction action, Consumer<MessageAction> function) {
        action.getChannel().sendTyping().queueAfter(Rand.randomInt(3, 5), TimeUnit.SECONDS, (v) -> {
            action.queueAfter(Rand.randomInt(3, 5), TimeUnit.SECONDS, message -> {
                if(function != null) function.accept(action);
            });

        });
    }

    public static String removeDuplicates(String s) {
        String str = s.toLowerCase();
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < str.length(); i++) {
            if(i == 0 || str.charAt(i) != str.charAt(i - 1)) result.append(str.charAt(i));
        }
        
        return result.toString();
    }
}
