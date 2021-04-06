package components;

import javax.swing.*;

public class AdvancedSpinner extends JSpinner {
    private final JTextField textField;

    public Object getLastValue() {
        if (getModel() instanceof SpinnerTypeListModel<?>) {
            return ((SpinnerTypeListModel<?>) getModel()).getLastValue();
        }
        return null;
    }
    public Object getLastIndex() {
        if (getModel() instanceof SpinnerTypeListModel<?>) {
            return ((SpinnerTypeListModel<?>) getModel()).getLastIndex();
        }
        return null;
    }
    public Object getIndex() {
        if (getModel() instanceof SpinnerTypeListModel<?>) {
            return ((SpinnerTypeListModel<?>) getModel()).getIndex();
        }
        return null;
    }

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
}
