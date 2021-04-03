package components;

import javax.swing.*;
import tools.RunnableWithObject;

public class JIntegerField extends JTypeField<Integer> {
    public JIntegerField() {
        this(0);
    }
    public JIntegerField(int value) {
        this(value,"%d");
    }
    public JIntegerField(int value, String format) {
        this(value, format, JTextField.LEFT);
    }
    public JIntegerField(int value, String format, int alignment) {
        super(format);
        setValue(value);
        setHorizontalAlignment(alignment);
    }

    public void decrement() {
        changeValue(-1);
    }
    public void changeValue(int number) {
        this.setValue(value+number);
    }
    public void increment() {
        changeValue(1);
    }
}
class JTypeField<T> extends JTextField {
    T value;
    String format;
    RunnableWithObject runnable = null;

    public JTypeField(String format) {
        super();
        this.format = format;
    }
    public JTypeField(T value, String format) {
        this(format);
        this.value = value;
    }

    public void setValue(T value) {
        this.setText(String.format(format, value));
        this.value = value;

        if (runnable != null) {
            runnable.run(this, null);
        }
    }

    public T getValue() {
        return value;
    }

    public RunnableWithObject getRunnable() {
        return runnable;
    }
    public void setRunnable(RunnableWithObject runnable) {
        this.runnable = runnable;
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
        this.setText(String.format(format, value));
    }
}