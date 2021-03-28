package tools;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@FunctionalInterface
public interface MouseClickedAdapter extends MouseListener {
    void click(MouseEvent e);

    @Override
    default void mouseClicked(MouseEvent e) {
        click(e);
    }

    @Override
    default void mousePressed(MouseEvent e) {

    }

    @Override
    default void mouseReleased(MouseEvent e) {
    }

    @Override
    default void mouseEntered(MouseEvent e) {
    }

    @Override
    default void mouseExited(MouseEvent e) {
    }
}
