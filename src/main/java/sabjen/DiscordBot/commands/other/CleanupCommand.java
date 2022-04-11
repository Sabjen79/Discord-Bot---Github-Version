package sabjen.DiscordBot.commands.other;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import sabjen.DiscordBot.Bot;
import sabjen.DiscordBot.commands.Command;
import sabjen.DiscordBot.utils.Rand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CleanupCommand extends Command {
    private Guild guild;
    private String cooldownMessage;
    private String emoji = " :radioactive: ";

    //==================================================================================

    private static String[] cooldownMessages = {
            "MAI INCOLO o sa pot sa dau un cleanup",
            "NU ACUM, cleanup mai INCOLO",
    };

    /////////

    private static String[] cleanupAlerts = {
            "Cleanup in $ minute",
            "Exista zi fara cleanup? Fac unul in $ minute"
    };

    private static String[] cleanupAlertsUser = {
            "OK @ VREI CLEANUP? Revin in $ minute, daca nu e nimeni pe mute iti dau kick tie",
            "In $ minute dau cleanup, daca nu e nimeni pe mute ii dau kick lui @"
    };

    /////////

    private static String[] cleanupNoneUser = {
            "Ai vrut cleanup @...ti-am dat cleanup",
            "Kick @ pentru ca dai cleanup degeaba"
    };

    private static String[] cleanupNoneUserDisconnect = {
            "A ok @ ai iesit de unul singur? Mai bine asa"
    };

    private static String[] cleanupNone = {
            "Se putea mai bine",
            "Kick la...nimeni....."
    };

    private static String[] cleanupSmall = {
            "Kick la @, foarte bine bravo mie",
            "@?",
    };

    private static String[] cleanupMedium = {
            "Kick la @...Pot sa dorm linistit acum",
            "Cleanup satisfacator de $ oameni: @",
    };

    private static String[] cleanupBig = {
            "Cleanup mare de $ persoane"
    };

    //==================================================================================

    private boolean isUserInactive(Member member) {
        if(Bot.isUserThisBot(member)) return false;
        GuildVoiceState vc = member.getVoiceState();

        if(vc == null || vc.getChannel() == null) return false;

        VoiceChannel afk = guild.getAfkChannel();
        if(afk != null && afk.getIdLong() == vc.getChannel().getIdLong()) return true;

        return vc.isMuted() || vc.isDeafened();
    }

    private int getInactiveUsersNum() {
        int num = 0;

        for(Member member : guild.getMembers()) {
            if(isUserInactive(member)) {
                num++;
            }
        }

        return num;
    }

    //==================================================================================

    public boolean canRandomCleanup() {
        return Math.random() <= 0.15 * getInactiveUsersNum();
    }

    private boolean canCleanup() {
        return (new Date().getTime()) >= Bot.timers.getCleanupDate(guild);
    }

    public void cleanupAnnounce(Member member) {
        if(!Bot.isUserThisBot(member)) {
            GuildVoiceState state = member.getVoiceState();
            if(state == null || !state.inAudioChannel()) return;
        } else if(getInactiveUsersNum() == 0) return;

        Bot.timers.updateCleanupDate(guild);
        int minutes = Rand.randomInt(2, 3);

        TextChannel channel = Bot.get(guild).mainTextChannel;

        if(Bot.isUserThisBot(member)) {
            String text = Rand.randomFrom( CleanupCommand.cleanupAlerts )
                    .replace("$", String.valueOf(minutes));

            channel.sendTyping().queue();
            channel.sendMessage(emoji + text + emoji).queueAfter(Rand.randomInt(7, 10), TimeUnit.SECONDS);
        } else {
            String text = Rand.randomFrom( CleanupCommand.cleanupAlertsUser )
                    .replace("$", String.valueOf(minutes))
                    .replace("@", member.getAsMention());

            channel.sendMessage(emoji + text + emoji).queue();
        }

        Bot.timers.newTimer().schedule(() -> {
            cleanup(member);
        }, minutes, TimeUnit.MINUTES);
    }

    private void cleanup(Member member) {
        cooldownMessage = Rand.randomFrom(cooldownMessages);

        TextChannel channel = Bot.get(guild).mainTextChannel;
        List<Member> kickedMembers = new ArrayList<>();

        for(Member m : guild.getMembers()) {
            if(isUserInactive(m)) {
                kickedMembers.add(m);
            }
        }

        if(!Bot.isUserThisBot(member) && kickedMembers.size() < 2) {
            if(kickedMembers.size() == 1 && kickedMembers.contains(member)) kickedMembers.remove(member);

            if(kickedMembers.size() == 0) {
                String[] arr;

                if(member.getVoiceState().inAudioChannel()) {
                    guild.kickVoiceMember(member).queue();
                    arr = CleanupCommand.cleanupNoneUser;
                } else arr = CleanupCommand.cleanupNoneUserDisconnect;

                channel.sendMessage(Rand.randomFrom( arr ).replace("@", member.getAsMention())).queue();
                return;
            }
        }

        kickedMembers.forEach((m) -> guild.kickVoiceMember(m).queue());

        String[] array;
        int userCount = kickedMembers.size();

        String mention = "";
        for(Member m : kickedMembers) {
            mention += " " + m.getAsMention();
        }

        if(userCount == 0) array = CleanupCommand.cleanupNone;
        else if(userCount == 1) array = CleanupCommand.cleanupSmall;
        else if(userCount <= 4) array = CleanupCommand.cleanupMedium;
        else array = CleanupCommand.cleanupBig;

        channel.sendMessage(
                Rand.randomFrom(array)
                        .replace("$", String.valueOf(userCount))
                        .replace("@", mention.trim())
        ).queue();
    }

    //==================================================================================

    public CleanupCommand(Guild g) {
        super();
        stopAfterExecuted = true;

        guild = g;
        cooldownMessage = Rand.randomFrom( CleanupCommand.cooldownMessages );
    }

    @Override
    public boolean check(MessageReceivedEvent event) {
        String text = event.getMessage().getContentRaw().toLowerCase();

        return text.startsWith(Bot.config.prefix + "nuke") || text.startsWith(Bot.config.prefix + "clean");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().delete().queue();

        if(!checkChannel(event, Bot.get(event.getGuild()).mainTextChannel)) return;

        if(!canCleanup()) {
            event.getChannel().sendMessage(cooldownMessage).queue(message -> message.delete().queueAfter(7, TimeUnit.SECONDS));
            return;
        }

        cleanupAnnounce(event.getMember());
    }
}