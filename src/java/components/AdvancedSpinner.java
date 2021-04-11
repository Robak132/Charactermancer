package components;

import javax.swing.*;

public class AdvancedSpinner extends JSpinner {
    private final JTextField textField;
    private Object lastValue;

    public AdvancedSpinner(SpinnerModel model) {
        super(model);
        this.textField = ((JSpinner.DefaultEditor) getEditor()).getTextField();
    }
    public AdvancedSpinner() {
        super();
        this.textField = ((JSpinner.DefaultEditor) getEditor()).getTextField();
    }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
    }
    public void setHorizontalAlignment(int alignment) {
        textField.setHorizontalAlignment(alignment);
    }
    public JTextField getTextField() {
        return textField;
    }

    @Override
    public void setValue(Object value) {
        lastValue = super.getValue();
        super.setValue(value);
    }
    @Override
    public Object getValue() {
        return super.getValue();
    }
    @Override
    public Object getNextValue() {
        lastValue = super.getValue();
        return super.getNextValue();
    }
    @Override
    public Object getPreviousValue() {
        lastValue = super.getValue();
        return super.getPreviousValue();
    }

    public Object getLastValue() {
        return lastValue;
    }
}
