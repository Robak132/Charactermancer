import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentAdapter implements DocumentListener {
    public void insertUpdate(DocumentEvent e) {}
    public void removeUpdate(DocumentEvent e) {}
    public void changedUpdate(DocumentEvent e) {}
}
