package tools;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class AbstractActionBuilder {
    public static Action getAction(Runnable runnable) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        };
    }
}
