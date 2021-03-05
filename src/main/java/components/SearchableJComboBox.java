package components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class SearchableJComboBox extends JComboBox<String> {
    private final JTextField textField;
    private boolean lock;
    private TreeSet<String> items;
    private String safe_value;
    private String unsafe_value;
    private int caret_position;

    public SearchableJComboBox() {
        super();
        items = new TreeSet<>();
        textField = (JTextField) this.editor.getEditorComponent();
        textField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            if (!lock) {
                SwingUtilities.invokeLater(() -> {
                    lock = true;
                    fill(textField.getText());
                    lock = false;
                });
            }
        });
    }
    public SearchableJComboBox(List<String> items) {
        this();
        addItems(items);
    }
    public SearchableJComboBox(List<String> items, boolean startNull) {
        this();
        addItems(items, startNull);
    }

    public void refresh() {refresh(true);}
    public void refresh(boolean startNull) {
        if (startNull)
            fill("");
        else
            fill(items.first());
    }
    private void fill(String text) {
        if (items.contains(text)) {
            fill("");
            safe_value = text;
            unsafe_value = text;
            textField.setForeground(Color.black);
        } else {
            caret_position = textField.getCaretPosition();
            this.removeAllItems();
            textField.setForeground(Color.red);
            for (String name : items) {
                if (name.toUpperCase().contains(text.toUpperCase()))
                    super.addItem(name);
            }
            unsafe_value = text;
        }
        this.setSelectedItem(text);
        if (text.length() < caret_position)
            caret_position = text.length();
        textField.setCaretPosition(caret_position);
    }

    public void addItems(List<String> items, boolean startNull) {
        this.items = new TreeSet<>(items);
        if (startNull)
            fill("");
        else
            fill(items.get(0));
    }
    public void addItems(List<String> items) {
        addItems(items, true);
    }

    @Override
    public void addItem(String item) {
        items.add(item);
    }

    public String getValue() {
        return unsafe_value;
    }
    public String getSafeValue() {
        return safe_value;
    }

    public void setNotEditable() {
        textField.setEditable(false);

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