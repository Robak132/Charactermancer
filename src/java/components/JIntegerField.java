package components;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTextField;
import org.apache.logging.log4j.LogManager;

public class JIntegerField extends JTextField implements PropertyChangeListener {
    private Runnable runnable;
    private String format;
    private Integer value;

    public JIntegerField() {
        this(0);
    }
    public JIntegerField(Integer value) {
        this(value,"%d");
    }
    public JIntegerField(Integer value, String format) {
        this.format = format;
        setValue(value);
    }

    public void decrement() {
        changeValue(-1);
    }
    public void changeValue(Integer number) {
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
    public Integer getSavedValue() {
        return value;
    }
    public void setValue(Integer value) {
        if (value==null) {
            this.setText("");
        } else {
            this.setText(String.format(format, value));
        }
        this.value = value;

        if (runnable != null) {
            runnable.run();
        }
    }

    public Runnable getRunnable() {
        return runnable;
    }
    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        setValue((Integer) evt.getNewValue());
    }
}