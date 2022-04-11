package sabjen.DiscordBot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class BotStrings {
    private int currentIndex = 0;
    private String[] strings;

    public BotStrings(String[] s) {
        strings = s;

        shuffleArray();
    }

    private void shuffleArray() {
        Collections.shuffle(Arrays.asList(strings));
    }

    public String getRandomString() {
        String returnValue = strings[currentIndex];

        currentIndex++;
        if(currentIndex >= strings.length) {
            shuffleArray();
            currentIndex = 0;
        }

        return returnValue;
    }

}
