package components;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.hibernate.event.spi.DirtyCheckEvent;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GridPanel extends JPanel {
    private int columns=1;
    private int rows=1;
    private final DynamicMatrix2D<Component> components = new DynamicMatrix2D<>();
    private final Map<Component, GridConstraints> items = new HashMap<>();

    public static final int ALIGNMENT_HORIZONTAL = 0;
    public static final int ALIGNMENT_NOTOP = 1;
    public static final int ALIGNMENT_CENTER = 2;

    public GridPanel() {
        super();
    }

    public void addAuto(Component comp, Object constraints, boolean buildNow) {
        GridConstraints editConstr = (GridConstraints) constraints;
        int columnCount = editConstr.getColumn();
        editConstr.setColumn(columnCount+1);
        if (columns < columnCount+1) {
            columns = columnCount+1;
        }
        int rowCount = editConstr.getRow();
        editConstr.setRow(rowCount+1);
        if (rows < rowCount+1) {
            rows = rowCount+1;
        }
        items.put(comp, editConstr);
        components.set(rowCount, columnCount, comp);

        if (buildNow) {
            build();
        }
    }
    public void addAuto(Component comp, Object constraints) {
        addAuto(comp, constraints, false);
    }

    public void build() {
        build(GridPanel.ALIGNMENT_NOTOP);
    }
    public void build(int alignment) {
        setLayout(new GridLayoutManager(rows+2, columns+2, new Insets(0, 0, 0, 0), -1, -1));
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
    }

    public Component getComponent(int col, int row) {
        return components.get(row, col);
    }
    public Component[] getComponents(int row) {
        return components.get(row);
    }

    private void addSpacers(int alignment) {
        super.add(new Spacer(), new GridConstraints(0, 0, rows, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        super.add(new Spacer(), new GridConstraints(0, columns+1, rows, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));

        if (alignment > 0) {
            super.add(new Spacer(), new GridConstraints(rows+1, 0, 1, columns+2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null));
        }
        if (alignment == 2) {
            super.add(new Spacer(), new GridConstraints(0, 0, 1, columns+2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null));
        }
    }
}