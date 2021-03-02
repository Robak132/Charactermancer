import javax.swing.*;

public class JIntegerField extends JTextField {
    Integer value;

    public JIntegerField() {
        super();
        this.value = 0;
    }
    public JIntegerField(int value) {
        super(null, "" + value, 0);
        this.value = value;
    }

    public void setValue(int value) {
        this.setText("" + value);
        this.value = value;
    }
    public int getValue() {
        return value;
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
