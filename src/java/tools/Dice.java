package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public static <T> T randomItem(List<T> list) {
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
    public static int[] randomInts(int amount, int minimum, int maximum, int sum) {
        int[] numberList = new int[amount];

        for (int index = 0; index<amount-1; index++) {
            int rest = amount - (index + 1);

            int restMinimum = minimum * rest;
            int restMaximum = maximum * rest;

            minimum = Math.max(minimum, sum - restMaximum);
            maximum = Math.min(maximum, sum - restMinimum);

            int newRandomValue = Dice.randomInt(minimum, maximum);
            numberList[index] = newRandomValue;
            sum -= newRandomValue;
        }
        numberList[amount-1] = sum;

        return numberList;
    }
    public static List<Integer> randomIntPermutation(int amount, Integer[] values) {
        List<Integer> integerList = new ArrayList<>(Arrays.asList(values));
        for (int i=integerList.size(); i<amount; i++) {
            integerList.add(0);
        }
        Collections.shuffle(integerList);
        return integerList;
    }

    private static ThreadLocalRandom randomThread() {
        return ThreadLocalRandom.current();
    }
}
