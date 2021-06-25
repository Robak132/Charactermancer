package components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import tools.KeyPressedAdapter;

public class FilteredComboBox<T> extends JComboBox<T> {
    private Popup filterPopup;
    private JLabel filterLabel;
    private Object selectedItem;
    private List<T> items;
    private BiPredicate<T, String> userFilter;
    private final TextHandler textHandler = new TextHandler();
    private boolean noValid;

    private final Map<AbstractButton, MouseListener[]> componentsMap = new ConcurrentHashMap<>();
    private MouseListener[] mouseListeners;
    private boolean isLocked;

    public FilteredComboBox() {
        super();
        init();
    }
    public FilteredComboBox(Function<T, String> stringParser) {
        this();
        setUserFilter(stringParser);
    }

    private void init() {
        initFilterLabel();
        initComboPopupListener();
        initComboKeyListener();
    }
    private void prepareComboFiltering() {
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) getModel();
        items = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            items.add(model.getElementAt(i));
        }
    }
    private void initComboKeyListener() {
        addKeyListener((KeyPressedAdapter) e -> {
            char keyChar = e.getKeyChar();
            if (!Character.isDefined(keyChar)) {
                return;
            }
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_DELETE:
                    return;
                case KeyEvent.VK_ENTER:
                    resetFilterPopup();
                    return;
                case KeyEvent.VK_ESCAPE:
                    if (selectedItem != null) {
                        setSelectedItem(selectedItem);
                    }
                    resetFilterPopup();
                    return;
                case KeyEvent.VK_BACK_SPACE:
                    textHandler.removeCharAtEnd();
                    break;
                default:
                    textHandler.add(keyChar);
            }

            if (!isPopupVisible()) {
                showPopup();
            }

            if (!textHandler.text.isEmpty()) {
                showFilterPopup();
                performFilter();
            } else {
                resetFilterPopup();
            }
            e.consume();
        });
    }
    private void initFilterLabel() {
        filterLabel = new JLabel();
        filterLabel.setOpaque(true);
        filterLabel.setBackground(new Color(255, 248, 220));
        filterLabel.setFont(filterLabel.getFont().deriveFont(Font.PLAIN));
        filterLabel.setBorder(BorderFactory.createLineBorder(Color.gray));
    }
    private void initComboPopupListener() {
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                resetFilterPopup();
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                resetFilterPopup();
            }
        });
    }
    private void showFilterPopup() {
        if (textHandler.getText().isEmpty()) {
            return;
        }
        if (filterPopup == null) {
            Point p = new Point(0, 0);
            SwingUtilities.convertPointToScreen(p, this);
            Dimension comboSize = getPreferredSize();
            filterLabel.setPreferredSize(new Dimension(comboSize));
            filterPopup = PopupFactory.getSharedInstance().getPopup(this, filterLabel, p.x, p.y - filterLabel.getPreferredSize().height);
            selectedItem = getSelectedItem();
        }
        filterPopup.show();
    }
    private void resetFilterPopup() {
        if (!textHandler.isEditing()) {
            return;
        }
        if (filterPopup != null) {
            filterPopup.hide();
            filterPopup = null;
            filterLabel.setText("");

            if (noValid) {
                //TODO: Create popup for creating
                System.out.printf("Invalid item: %s%n", textHandler.text);
            }

            // add items in the original order
            Object selectedItem = getSelectedItem();
            DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) getModel();
            model.removeAllElements();
            for (T item : items) {
                model.addElement(item);
            }
            // preserve the selection
            model.setSelectedItem(selectedItem);
            textHandler.reset();
        }
    }
    private void performFilter() {
        filterLabel.setText(textHandler.getText());
        //  System.out.println("'" + textHandler.getText() + "'");
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) getModel();
        model.removeAllElements();
        List<T> filteredItems = new ArrayList<>();
        //add matched items first
        for (T item : items) {
            if (userFilter.test(item, textHandler.getText())) {
                model.addElement(item);
            } else {
                filteredItems.add(item);
            }
        }
        if (model.getSize() == 0) {
            //if no match then red font
            noValid = true;
            filterLabel.setForeground(Color.red);
        } else {
            noValid = false;
            filterLabel.setForeground(Color.blue);
        }
        //add unmatched items
        filteredItems.forEach(model::addElement);
    }

    public void setUserFilter(Function<T, String> stringParser) {
        this.userFilter = (object, textToFilter) -> {
            if (textToFilter.isEmpty()) {
                return true;
            }
            return stringParser.apply(object).toLowerCase().contains(textToFilter.toLowerCase());
        };
        renderer = new FilteredComboRenderer<>(filterLabel, stringParser);
        setRenderer(renderer);
    }
    public void addItems(List<T> list) {
        list.forEach(this::addItem);
        prepareComboFiltering();
    }
    public boolean isEditing() {
        return textHandler.editing;
    }

    @Override
    public void setSelectedItem(Object selectedItem) {
        Object lastObject = getModel().getSelectedItem();
        this.selectedItem = selectedItem;

        getModel().setSelectedItem(selectedItem);
        if (lastObject == selectedItem) {
            fireActionEvent();
        }
        resetFilterPopup();
    }
    @Override
    public Object getSelectedItem() {
        return super.getSelectedItem();
    }

    public void setLocked(boolean aFlag) {
        if (isLocked == aFlag) {
            return;
        }

        if (aFlag) {
            lock();
        } else {
            unlock();
        }
    }
    private void lock() {
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
    private void unlock() {
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
    }

    private static class TextHandler {
        private String text = "";
        private boolean editing;

        public void add(char c) {
            text += c;
            editing = true;
        }
        public void removeCharAtEnd() {
            if (text.length() > 0) {
                text = text.substring(0, text.length() - 1);
                editing = true;
            }
        }
        public void reset() {
            text = "";
            editing = false;
        }
        public String getText() {
            return text;
        }
        public boolean isEditing() {
            return editing;
        }
    }
}