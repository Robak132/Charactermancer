package tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.List;

public abstract class Toolbox {
    public static void setAll(List list, int value) {
        for (int i=0;i<list.size();i++)
            list.set(i, value);
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
    public static int avg(List<Integer> list) {
        int sum = 0;
        for (int value : list) {
            sum += value;
        }
        return sum/list.size();
    }
    public static float avgFloat(List<Float> list) {
        float sum = 0;
        for (float value : list) {
            sum += value;
        }
        return sum/list.size();
    }
    public static double varFloat(List<Float> list) {
        double var = 0;
        float avg = avgFloat(list);
        for (float value : list) {
            var += (value - avg) * (value - avg);
        }
        var /= list.size();
        var = Math.sqrt(var);
        return var;
    }
    public static float avgFloat(Float[] array) {
        float sum = 0;
        for (float value : array) {
            sum += value;
        }
        return sum/array.length;
    }
    public static float avgSize(List<String> list) {
        float sum = 0;
        for (String value : list) {
            sum += value.length();
        }
        return sum/list.size();
    }
}
