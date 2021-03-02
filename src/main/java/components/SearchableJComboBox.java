package components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class SearchableJComboBox extends JComboBox<String> {
    private JTextField textfield;
    private boolean lock;
    private List items;
    private String good_value;

    public SearchableJComboBox() {
        super();
        textfield = (JTextField) this.editor.getEditorComponent();
    }
    public SearchableJComboBox(List items) {
        this();
        bindItems(items);
    }

    private void fill(String text) {
        this.removeAllItems();
        List<String> duplicates = new ArrayList<>();
        boolean changed = false;
        textfield.setForeground(Color.red);
        for (Object ptr : items) {
            String name = (String) ptr;
            if (name.toUpperCase().contains(text.toUpperCase()) && !duplicates.contains(name)) {
                this.addItem(name);
                duplicates.add(name);
            }
            if (name.equals(text)) {
                textfield.setForeground(Color.black);
                good_value = name;
                changed = true;
            }
        }
        if (!changed)
            good_value = null;
        this.setSelectedItem(text);
    }
    public void bindItems(List items) {
        this.items = items;
        textfield.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            if (!lock) {
                SwingUtilities.invokeLater(() -> {
                    lock = true;
                    fill(textfield.getText());
                    lock = false;
                });
            }
        });
        fill("");
    }
    public String getValue() {
        return good_value;
    }

    public void setNotEditable() {
        textfield.setEditable(false);

        MouseListener[] mls = this.getMouseListeners();
        for (MouseListener listener : mls)
            this.removeMouseListener(listener);

        Component[] comps = this.getComponents();
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

@FunctionalInterface
interface SimpleDocumentListener extends DocumentListener {
    void update(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e) {
        update(e);
    }
    @Override
    default void removeUpdate(DocumentEvent e) {
        update(e);
    }
    @Override
    default void changedUpdate(DocumentEvent e) {
            update(e);
        }
}