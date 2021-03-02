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
}
