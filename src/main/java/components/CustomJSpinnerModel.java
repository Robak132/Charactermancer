package components;

import javax.swing.*;

public class CustomJSpinnerModel<T> extends SpinnerNumberModel {
    T[] validValues;
    int index = 0;

    public CustomJSpinnerModel(T[] validValues) {
        super();
        this.validValues = validValues;
        setValue(validValues[index]);
    }
    @Override
    public Object getNextValue() {
        if (index != validValues.length - 1)
            index++;
        return validValues[index];
    }
    @Override
    public Object getPreviousValue() {
        if (index != 0)
            index--;
        return validValues[index];
    }
}
