package sabjen.DiscordBot.commands.responses;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.BotStrings;
import sabjen.DiscordBot.commands.Command;
import sabjen.DiscordBot.utils.MessageUtil;
import sabjen.DiscordBot.utils.Rand;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RandomInsult extends Command {
    private Guild guild;
    //==================================================================================

    private BotStrings gameResponse = new BotStrings(new String[]{
            "@ hai inchide #, te-ai jucat destul",
            "@ nu ai altceva de facut decat sa te joci #?"
    });

    private BotStrings listenResponse = new BotStrings(new String[]{
            "Imaginati-va ca unii asculta # in 2022...adica @",
            "Cum sa asculti #?? Explica te rog @"
    });

    private BotStrings invisibleResponse = new BotStrings(new String[]{
            "@ daca stai pe invizibil pe discord macar nu trimite mesaje..."
    });

    //==================================================================================

    public RandomInsult(Guild g) {
        super();
        guild = g;
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        //System.out.println(event.getMember().getOnlineStatus());
        return Bot.timers.canInsult(guild) &&
                Bot.config.randomInsultInvisibleChance() &&
                (event.getMember().getOnlineStatus() == OnlineStatus.OFFLINE);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Bot.timers.updateInsultDate(guild);

        String text = invisibleResponse.getRandomString()
                .replace("@", event.getMember().getAsMention());

        MessageUtil.typeMessage( event.getMessage().reply(text) );
    }

    //==================================================================================

    public boolean activityInsult() {
        //if(guild.getIdLong() != 694525715009372231L) return false;

        List<Member> list = new ArrayList<>(guild.getMembers());
        Collections.shuffle(list);

        for(Member member : list) {
            if(member.getActivities().size() > 0) {

                Activity activity = member.getActivities().get(0);
                if(activity.getType() == Activity.ActivityType.LISTENING) {
                    String text = listenResponse.getRandomString()
                            .replace("@", member.getAsMention())
                            .replace("#", activity.asRichPresence().getState());

                    MessageUtil.typeMessage( Bot.get(guild).mainTextChannel.sendMessage(text) );
                    return true;
                }

                if(activity.getType() == Activity.ActivityType.PLAYING) {
                    String text = gameResponse.getRandomString()
                            .replace("@", member.getAsMention())
                            .replace("#", activity.getName());

                    MessageUtil.typeMessage( Bot.get(guild).mainTextChannel.sendMessage(text) );
                    return true;
                }


            }
        }

        return false;
    }
}
