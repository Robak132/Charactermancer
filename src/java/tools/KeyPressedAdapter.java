package tools;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@FunctionalInterface
public interface KeyPressedAdapter extends KeyListener {
    void press(KeyEvent e);

    @Override
    default void keyTyped(KeyEvent e) {
        // Ignored
    }

    @Override
    default void keyPressed(KeyEvent e) {
        press(e);
    }

    @Override
    default void keyReleased(KeyEvent e) {
        // Ignored
    }
}
