import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Toolbox {
    public static int randomIntInRange(int min, int max) {
        return min + ThreadLocalRandom.current().nextInt(max - min + 1);
    }
    public static int randomIntInRange(int min, int max, int count) {
        int sum = 0;
        for (int i=0;i<count;i++)
            sum += randomIntInRange(min, max);
        return sum;
    }

    public static void setJComboBoxReadOnly(JComboBox jcb) {
        JTextField jtf = (JTextField) jcb.getEditor().getEditorComponent();
        jtf.setEditable(false);

        MouseListener[] mls = jcb.getMouseListeners();
        for (MouseListener listener : mls)
            jcb.removeMouseListener(listener);

        Component[] comps = jcb.getComponents();
        for (Component c : comps) {
            if (c instanceof AbstractButton) {
                c.setEnabled(false);

                MouseListener[] mls2 = c.getMouseListeners();
                for (MouseListener listener : mls2)
                    c.removeMouseListener(listener);
            }
        }
    }
}
