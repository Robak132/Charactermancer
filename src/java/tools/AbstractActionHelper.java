package tools;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class AbstractActionHelper {
    private AbstractActionHelper() {
        // To prevent instantiation
    }

    public static Action getAction(Runnable runnable) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        };
    }
    public static void createActionMnemonic(JPanel panel, KeyStroke keyStroke, Runnable runnable) {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, keyStroke.toString());
        panel.getActionMap().put(keyStroke.toString(), getAction(runnable));
    }}
