package sabjen.DiscordBot;

public class BotConfiguration {
    public final String prefix = "-";

    private final double skipChance = 0.1;
    private final double immediateSkipChance = 0.05;
    private final double loopChance = 0.03;
    private final double insultActivityChance = 0.15;
    private final double insultInvisibleChance = 0.05;

    public String[] restrictedKeywords = {"carcera"};

    //--------------------------------------------------------------------------------

    private boolean randomChance(double chance) {
        return Math.random() <= chance;
    }

    public boolean randomSkipChance() {
        return randomChance(skipChance);
    }

    public boolean randomImmediateSkipChance() {
        return randomChance(immediateSkipChance);
    }

    public boolean randomLoopChance() {
        return randomChance(loopChance);
    }

    public boolean randomInsultActivityChance() {
        return randomChance(insultActivityChance);
    }

    public boolean randomInsultInvisibleChance() {
        return randomChance(insultInvisibleChance);
    }

    public boolean isRestrictedKeyword(String s) {
        for(String keyword : restrictedKeywords) {
            if(s.toLowerCase().contains(keyword.toLowerCase())) return true;
        }
        return false;
    }

}
