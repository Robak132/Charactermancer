package tools;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Dice {
    public static int randomDice(int sides) {
        return 1 + ThreadLocalRandom.current().nextInt(sides);
    }
    public static int randomDice(int amount, int sides) {
        int sum = 0;
        for (int i = 0; i < amount; i++)
            sum += randomDice(sides);
        return sum;
    }
    public static int randomInt(int min, int max) {
        return min + ThreadLocalRandom.current().nextInt(max-min+1);
    }
    public static Object randomItem(List<?> list) {
        int rnd = randomInt(0, list.size()-1);
        return list.get(rnd);
    }
}
