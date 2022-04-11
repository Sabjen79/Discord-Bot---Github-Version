package sabjen.DiscordBot.utils;

import java.util.Random;

public class Rand {
    private static Random rand = new Random();

    public static int randomInt(int lower, int higher) {
        return rand.nextInt(higher-lower) + lower;
    }

    public static int randomInt(int higher) {
        return randomInt(0, higher);
    }

    public static <T> T randomFrom(T[] items) {
        return items[rand.nextInt(items.length)];
    }

    public static boolean randomChance(double chance) {
        return Math.random() <= chance;
    }
}
