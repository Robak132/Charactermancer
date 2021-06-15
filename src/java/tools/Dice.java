package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class Dice {
    private Dice() {
        // To prevent instantiation
    }

    public static int randomDice(int sides) {
        return 1 + randomThread().nextInt(sides);
    }
    public static int randomDice(int amount, int sides) {
        int sum = 0;
        for (int i = 0; i < amount; i++) {
            sum += randomDice(sides);
        }
        return sum;
    }
    public static int randomInt(int min, int max) {
        return min + randomThread().nextInt(max-min+1);
    }
    public static Object randomItem(List<?> list) {
        int rnd = randomInt(0, list.size()-1);
        return list.get(rnd);
    }
    public static List<Integer> randomIntegerList(int size, int[] values) {
        List<Integer> lookupTable = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();
        for (int i=0;i<size;i++) {
            lookupTable.add(i);
            map.put(i, 0);
        }
        for (int value : values) {
            int index = Dice.randomInt(0, lookupTable.size() - 1);
            map.replace(lookupTable.get(index), value);
            lookupTable.remove(index);
        }
        return new ArrayList<>(map.values());
    }

    private static ThreadLocalRandom randomThread() {
        return ThreadLocalRandom.current();
    }
}
