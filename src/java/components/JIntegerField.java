package components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import tools.RunnableWithObject;

public class JIntegerField extends JTextField implements PropertyChangeListener {
    private RunnableWithObject runnable;
    private String format;
    private int value;

    public JIntegerField() {
        this(0);
    }
    public JIntegerField(int value) {
        this(value,"%d");
    }
    public JIntegerField(int value, String format) {
        this.format = format;
        setValue(value);
    }

    public void decrement() {
        changeValue(-1);
    }
    public void changeValue(int number) {
        this.setValue(value + number);
    }
    public void increment() {
        changeValue(1);
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
        this.setText(String.format(format, value));
    }

    public Integer getValue() {
        try {
            value = Integer.parseInt(getText());
        } catch (NumberFormatException ex) {
            LogManager.getLogger(getClass().getName()).error(String.format("Parse error: %s", ex.getMessage()));
            setText("");
            return null;
        }
        return value;
    }
    public void setValue(int value) {
        this.setText(String.format(format, value));
        this.value = value;

        if (runnable != null) {
            runnable.run(this, null);
        }
    }

    public RunnableWithObject getRunnable() {
        return runnable;
    }
    public void setRunnable(RunnableWithObject runnable) {
        this.runnable = runnable;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setValue((Integer) evt.getNewValue());
    }
}