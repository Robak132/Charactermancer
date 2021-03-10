package tools;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class ComponentList<T extends JComponent> extends ArrayList<T>{
    public ComponentList() {
        super();
    }
    public ComponentList(int initialCapacity) {
        super(initialCapacity);
    }
    public ComponentList(Collection<? extends T> c) {
        super(c);
    }

    public void setEnabled(boolean[] conditions, boolean enabled) {
        for (int i=0;i<this.size();i++) {
            if (conditions[i]) {
                get(i).setEnabled(enabled);
            }
        }
    }
}
