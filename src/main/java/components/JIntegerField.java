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
class JTypeField<E> extends JTextField {
    E value;

    public JTypeField() {
        super();
    }
    public JTypeField(E value) {
        super(null, "" + value, 0);
        this.value = value;
    }

    public void setValue(E value) {
        this.setText("" + value);
        this.value = value;
    }
    public E getValue() {
        return value;
    }

    @Override
    public void setText(String t) {
        try {
            value = (E) t;
            super.setText(t);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}