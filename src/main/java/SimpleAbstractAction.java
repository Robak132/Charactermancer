import javax.swing.*;
import java.awt.event.ActionEvent;

public class SimpleAbstractAction extends AbstractAction {
    Runnable action;

    SimpleAbstractAction(Runnable action) {
        super();
        this.action = action;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        action.run();
    }
}
