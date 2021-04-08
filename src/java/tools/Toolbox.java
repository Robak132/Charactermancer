package tools;

import java.util.List;

public final class Toolbox {
    public static int avg(List<Integer> list) {
        int sum = 0;
        for (int value : list) {
            sum += value;
        }
        return sum/list.size();
    }
    public static float avgFloat(List<Float> list) {
        float sum = 0;
        for (float value : list) {
            sum += value;
        }
        return sum/list.size();
    }
    public static double varFloat(List<Float> list) {
        double var = 0;
        float avg = avgFloat(list);
        for (float value : list) {
            var += (value - avg) * (value - avg);
        }
        var /= list.size();
        var = Math.sqrt(var);
        return var;
    }
    public static float avgFloat(Float[] array) {
        float sum = 0;
        for (float value : array) {
            sum += value;
        }
        return sum/array.length;
    }
    public static float avgSize(List<String> list) {
        float sum = 0;
        for (String value : list) {
            sum += value.length();
        }
        return sum/list.size();
    }
}
