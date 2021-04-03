package components;

import javax.swing.*;
import java.util.List;

public class SpinnerTypeListModel<T> extends SpinnerNumberModel {
    T[] validValues;
    T lastValue;
    int index = 0;
    int lastIndex = 0;

    public SpinnerTypeListModel(T[] validValues) {
        super();
        this.validValues = validValues;
        this.lastValue = validValues[index];
        this.lastIndex = index;
    }

    @Override
    public Object getValue() {
        return validValues[index];
    }

    public int getIndex() {
        return index;
    }

    @Override
    public Object getNextValue() {
        if (index != validValues.length - 1) {
            lastValue = validValues[index];
            lastIndex = index;

            index++;
        }
        return validValues[index];
    }

    @Override
    public Object getPreviousValue() {
        if (index != 0) {
            lastValue = validValues[index];
            lastIndex = index;

            index--;
        }
        return validValues[index];
    }

    public Object getLastValue() {
        return lastValue;
    }

    public int getLastIndex() {
        return lastIndex;
    }
}
