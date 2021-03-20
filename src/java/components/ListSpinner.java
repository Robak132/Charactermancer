package components;

import javax.swing.*;
import java.util.*;

public class ListSpinner<T> extends JSpinner {
    private final JTextField textField;

    public Object getLastValue() {
        return ((CustomJSpinnerModel<?>) getModel()).getLastValue();
    }

    public int getIndex() {
        return ((CustomJSpinnerModel<?>) getModel()).getIndex();
    }
    public int getLastIndex() {
        return ((CustomJSpinnerModel<?>) getModel()).getLastIndex();
    }

    public ListSpinner(T[] validValues) {
        this(Arrays.asList(validValues));
    }
    public ListSpinner(List<T> validValues) {
        super();
        this.textField = ((JSpinner.DefaultEditor) getEditor()).getTextField();
        setModel(new CustomJSpinnerModel<T>(validValues));
    }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
    }
    public void setHorizontalAlignment(int alignment) {
        textField.setHorizontalAlignment(alignment);
    }
}

class CustomJSpinnerModel<T> extends SpinnerNumberModel {
    List<T> validValues;
    T lastValue;
    int index = 0;
    int lastIndex = 0;

    public CustomJSpinnerModel(List<T> validValues) {
        super();
        this.validValues = validValues;
        this.lastValue = validValues.get(index);
        this.lastIndex = index;
    }

    @Override
    public Object getValue() {
        return validValues.get(index);
    }
    public int getIndex() {
        return index;
    }
    @Override
    public Object getNextValue() {
        if (index != validValues.size() - 1) {
            lastValue = validValues.get(index);
            lastIndex = index;

            index++;
        }
        return validValues.get(index);
    }
    @Override
    public Object getPreviousValue() {
        if (index != 0) {
            lastValue = validValues.get(index);
            lastIndex = index;

            index--;
        }
        return validValues.get(index);
    }

    public Object getLastValue() {
        return lastValue;
    }
    public int getLastIndex() {
        return lastIndex;
    }
}
