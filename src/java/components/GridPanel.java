package components;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import tools.DynamicMatrix2D;
import tools.RunnableWithObject;

public class GridPanel extends JPanel {
    private int columns = 1;
    private int rows = 1;
    private final DynamicMatrix2D<Component> components = new DynamicMatrix2D<>();
    private final Map<Component, GridConstraints> items = new LinkedHashMap<>();

    public static final int ALIGNMENT_HORIZONTAL = 0;
    public static final int[] ALIGNMENT_HORIZONTAL_2 = {0, 2};
    public static final int ALIGNMENT_NOTOP = 1;
    public static final int ALIGNMENT_CENTER = 2;

    @Override
    public void add(Component comp, Object constraints) {
        GridConstraints editConstr = (GridConstraints) constraints;
        Component lastComponent = components.get(editConstr.getRow(), editConstr.getColumn());
        if (lastComponent != null) {
            items.remove(lastComponent);
        }
        int columnCount = editConstr.getColumn();
        editConstr.setColumn(columnCount + 1);
        if (columns < columnCount + 1) {
            columns = columnCount + 1;
        }
        int rowCount = editConstr.getRow();
        editConstr.setRow(rowCount + 1);
        if (rows < rowCount + 1) {
            rows = rowCount + 1;
        }
        items.put(comp, editConstr);
        components.set(rowCount, columnCount, comp);
    }

    public void build() {
        build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    public void build(int alignment) {
        removeAll();
        System.out.printf("GridPanel@%s: Panel built (col: %d, row: %d)\n", this.hashCode(), columns, rows);
        setLayout(new GridLayoutManager(rows + 2, columns + 2, new Insets(0, 0, 0, 0), -1, -1));
        addSpacers(alignment);
        for (Map.Entry<Component, GridConstraints> pair : items.entrySet()) {
            if (pair.getValue().getColSpan() == -1) {
                pair.getValue().setColSpan(columns);
            }
            if (pair.getValue().getRowSpan() == -1) {
                pair.getValue().setRowSpan(rows);
            }
            super.add(pair.getKey(), pair.getValue());
        }
        revalidate();
        repaint();
    }

    public int getColumns() {
        return columns;
    }
    public int getRows() {
        return rows;
    }

    public Component getComponent(int col, int row) {
        return components.get(row, col);
    }

    public void iterateThroughRows(int col, RunnableWithObject runnable) {
        iterateThroughRows(col, 0, components.getYSize(), runnable);
    }
    public void iterateThroughRows(int col, int rowStart, int rowEnd, RunnableWithObject runnable) {
        for (int i = rowStart; i < rowEnd; i++) {
            Component active = getComponent(col, i);
            if (active != null) {
                runnable.run(active, i);
            }
        }
    }
    public void iterateThroughColumns(int row, RunnableWithObject runnable) {
        iterateThroughColumns(row, 0, components.getXSize(row), runnable);
    }
    public void iterateThroughColumns(int row, int columnStart, int columnEnd, RunnableWithObject runnable) {
        for (int i = columnStart; i < columnEnd; i++) {
            Component active = getComponent(i, row);
            if (active != null) {
                runnable.run(active, i);
            }
        }
    }

    private void addSpacers(int alignment) {
        super.add(new Spacer(), new GridConstraints(0, 0, rows, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        super.add(new Spacer(), new GridConstraints(0, columns + 1, rows, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));

        if (alignment > 0) {
            super.add(new Spacer(), new GridConstraints(rows + 1, 0, 1, columns + 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null));
        }
        if (alignment == 2) {
            super.add(new Spacer(), new GridConstraints(0, 0, 1, columns + 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null));
        }
    }

    // Components creators
    public JLabel createJLabel(int row, int column, ImageIcon icon, String tooltip) {
        JLabel label = new JLabel(icon);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setHorizontalTextPosition(0);
        label.setToolTipText(tooltip);
        add(label, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        return label;
    }
    public JLabel createJLabel(int row, int column, String name) {
        return createJLabel(row, column, 1, 1, name);
    }
    public JLabel createJLabel(int row, int column, int rowSpan, int colSpan, String name) {
        JLabel label = new JLabel(name);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setHorizontalTextPosition(0);
        add(label, new GridConstraints(row, column, rowSpan, colSpan, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        return label;
    }

    public JIntegerField createIntegerField(int row, int column, int value, Dimension dimension) {
        return createIntegerField(row, column, 1, 1, value, dimension, true);
    }
    public JIntegerField createIntegerField(int row, int column, int value, Dimension dimension, boolean editable) {
        return createIntegerField(row, column, 1, 1, value, dimension, editable);
    }
    public JIntegerField createIntegerField(int row, int column, int rowSpan, int colSpan, int value, Dimension dimension, boolean editable) {
        JIntegerField integerField = new JIntegerField(value);
        integerField.setHorizontalAlignment(JTextField.CENTER);
        integerField.setEditable(editable);
        add(integerField, new GridConstraints(row, column, rowSpan, colSpan, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, dimension, null));
        return integerField;
    }

    public JTextField createTextField(int row, int column, String value, Dimension dimension, boolean editable) {
        return createTextField(row, column, 1, 1, value, dimension, editable);
    }
    public JTextField createTextField(int row, int column, int rowSpan, int colSpan, String value, Dimension dimension, boolean editable) {
        JTextField textField = new JTextField(value);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setEditable(editable);
        add(textField, new GridConstraints(row, column, rowSpan, colSpan, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, dimension, null));
        return textField;
    }

    public JTextArea createTextArea(int row, int column, String value, Dimension dimension, boolean editable) {
        JTextArea textArea = new JTextArea(value);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(editable);
        add(textArea, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, dimension, null));
        return textArea;
    }

    public AdvancedSpinner createAdvancedSpinner(int row, int column, int rowSpan, int colSpan, SpinnerModel model, Dimension dimension, boolean enabled) {
        AdvancedSpinner advancedSpinner = new AdvancedSpinner(model);
        advancedSpinner.setHorizontalAlignment(JTextField.CENTER);
        advancedSpinner.setEnabled(enabled);
        add(advancedSpinner, new GridConstraints(row, column, rowSpan, colSpan, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, dimension, null));
        return advancedSpinner;
    }

    public SearchableComboBox createSearchableComboBox(int row, int column, Dimension dimension, boolean locked) {
        SearchableComboBox searchableComboBox = new SearchableComboBox();
        searchableComboBox.setLocked(locked);
        add(searchableComboBox, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, dimension, null));
        return searchableComboBox;
    }
}
