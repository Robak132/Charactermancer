/*
 *  Author: Jakub Robaczewski
 *  Git: github.com/Robak132
 */
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
    private final Map<AbstractButton, MouseListener[]> componentsMap = new HashMap<>();
    private MouseListener[] mouseListeners;
    private TreeSet<String> items;

    private String safeValue;
    private String unsafeValue;

    private Color goodColor = Color.BLACK;
    private boolean listenerLock;
    private boolean isLocked = false;
    private int caretPosition;

    public SearchableJComboBox() {
        super();
        items = new TreeSet<>();
        textField = (JTextField) this.editor.getEditorComponent();
        textField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            if (!listenerLock) {
                SwingUtilities.invokeLater(() -> {
                    listenerLock = true;
                    Dimension currentDimension = getSize();
                    fill(textField.getText());
                    setMinimumSize(currentDimension);
                    listenerLock = false;
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
        if (!startNull && items.size()!=0) {
            fill(items.first());
        } else {
            fill("");
        }
    }
    private void fill(String text) {
        if (items.contains(text)) {
            fill("");
            safeValue = text;
            unsafeValue = text;
            editor.getEditorComponent().setForeground(goodColor);
        } else {
            caretPosition = textField.getCaretPosition();
            this.removeAllItems();
            editor.getEditorComponent().setForeground(Color.red);
            for (String name : items) {
                if (name.toUpperCase().contains(text.toUpperCase()))
                    super.addItem(name);
            }
            unsafeValue = text;
        }
        this.setSelectedItem(text);
        if (text.length() < caretPosition)
            caretPosition = text.length();
        textField.setCaretPosition(caretPosition);
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
        return unsafeValue;
    }
    public String getSafeValue() {
        return safeValue;
    }

    @Override
    public void setForeground(Color fg) {
        goodColor = fg;
        super.setForeground(fg);
    }

    public void setLocked(boolean aFlag) {
        if (!isLocked) {
            return;
        }

        textField.setEditable(aFlag);
        if (!aFlag) {
            for (MouseListener listener : mouseListeners) {
                this.addMouseListener(listener);
            }

            for (Component c : getComponents()) {
                if (c instanceof AbstractButton) {
                    c.setEnabled(true);

                    MouseListener[] innerMouseListeners = componentsMap.get(c);
                    for (MouseListener listener : innerMouseListeners) {
                        c.addMouseListener(listener);
                    }
                }
            }
        } else {
            isLocked = true;
            mouseListeners = this.getMouseListeners();
            for (MouseListener listener : mouseListeners) {
                this.removeMouseListener(listener);
            }

            for (Component c : getComponents()) {
                if (c instanceof AbstractButton) {
                    c.setEnabled(false);

                    MouseListener[] innerMouseListeners = c.getMouseListeners();
                    for (MouseListener listener : innerMouseListeners) {
                        c.removeMouseListener(listener);
                    }
                    componentsMap.put((AbstractButton) c, innerMouseListeners);
                }
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