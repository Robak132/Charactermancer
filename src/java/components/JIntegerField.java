package components;

import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import tools.RunnableWithObject;

public class JIntegerField extends JTypeField<Integer> {
    public JIntegerField() {
        this(0);
    }
    public JIntegerField(int value) {
        this(value,"%d");
    }
    public JIntegerField(int value, String format) {
        super(format);
        setValue(value);
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

    @Override
    public Integer getValue() {
        try {
            value = Integer.valueOf(getText());
        } catch (NumberFormatException ex) {
            LogManager.getLogger(getClass().getName()).error(String.format("Parse error: %s", ex.getMessage()));
            setText("");
        }
        return value;
    }
}
abstract class JTypeField<T> extends JTextField {
    protected T value;
    private String format;
    private RunnableWithObject runnable = null;

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
    public abstract T getValue();

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