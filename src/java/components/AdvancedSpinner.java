package components;

import javax.swing.*;

public class AdvancedSpinner extends JSpinner {
    private final JTextField textField;
    private Object lastValue;
    private boolean isLocked;

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
        ((JSpinner.DefaultEditor) getEditor()).getTextField().setHorizontalAlignment(alignment);
    }
    public JTextField getTextField() {
        return textField;
    }
    public Object getLastValue() {
        return lastValue;
    }
    public boolean isLocked() {
        return isLocked;
    }
    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    @Override
    public void setValue(Object value) {
        lastValue = super.getValue();
        super.setValue(value);
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) getEditor();
        editor.getTextField().setEnabled(true);
        editor.getTextField().setEditable(enabled);
    }
}
