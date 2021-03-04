package components;

import javax.swing.*;

public class JIntegerField extends JTypeField<Integer> {
    public JIntegerField() {
        super("%d");
        this.value = 0;
    }
    public JIntegerField(int value) {
        super("%d");
        setValue(value);
    }
    public JIntegerField(int value, String format) {
        super(format);
        setValue(value);
    }

    public void decrement(int number) {
        this.value-=number;
        this.setText(String.format(format, value));
    }
    public void decrement() {
        decrement(1);
    }
    public void increment(int number) {
        this.value+=number;
        this.setText(String.format(format, value));
    }
    public void increment() {
        increment(1);
    }
}
class JTypeField<T> extends JTextField {
    T value;
    String format;

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
    }
    public T getValue() {
        return value;
    }

    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
        this.setText(String.format(format, value));
    }
}