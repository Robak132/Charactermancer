package components;

import javax.swing.*;

public class SpinnerTypeListModel<T> extends SpinnerNumberModel {
    private final T[] validValues;
    private T lastValue;
    private int index = 0;
    private int lastIndex;

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

    @Override
    public void setValue(Object value) {
        for (int i = 0; i<validValues.length; i++) {
            if (validValues[i] == value) {
                lastValue = validValues[index];
                lastIndex = index;

                index = i;
                super.setValue(value);
            }
        }
    }

    public Object getLastValue() {
        return lastValue;
    }

    public int getLastIndex() {
        return lastIndex;
    }
}
