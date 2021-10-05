package tools;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public final class AbstractActionHelper {
    private AbstractActionHelper() {
        // To prevent instantiation
    }

    public static void createActionMnemonic(JPanel panel, int keyEvent, int inputEvent, Runnable runnable) {
        KeyStroke stroke = KeyStroke.getKeyStroke(keyEvent, inputEvent);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, stroke.toString());
        panel.getActionMap().put(stroke.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        });
    }
}
