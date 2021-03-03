package components;

import javax.swing.*;

public class JIntegerField extends JTypeField<Integer> {
    public JIntegerField() {
        super();
        this.value = 0;
    }
    public JIntegerField(int value) {
        super(value);
        this.value = value;
    }

    public void decrement(int number) {
        this.value-=number;
        this.setText("" + value);
    }
    public void decrement() {
        decrement(1);
    }
    public void increment(int number) {
        this.value+=number;
        this.setText("" + value);
    }
    public void increment() {
        increment(1);
    }
}
class JTypeField<T> extends JTextField {
    T value;

    public JTypeField() {
        super();
    }
    public JTypeField(T value) {
        super(null, "" + value, 0);
        this.value = value;
    }

    public void setValue(T value) {
        this.setText("" + value);
        this.value = value;
    }
    public T getValue() {
        return value;
    }
}