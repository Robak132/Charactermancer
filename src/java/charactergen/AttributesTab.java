package charactergen;

import components.GridPanel;
import components.JIntegerField;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import main.CharacterSheet;
import main.Connection;
import mappings.Attribute;
import mappings.Race.Size;
import tools.ColorPalette;
import tools.MouseClickedAdapter;

class AttributesTab {
    private CharacterSheet sheet;
    private Connection connection;

    private JPanel mainPanel;
    private GridPanel attrAttributesTable;
    private JIntegerField attrSumField;
    private JButton attrOption3Button;
    private JButton attrOKButton;
    private JButton attrRollAllButton;
    private JButton attrRollButton;
    private JButton attrOption1Button;

    private Map<Integer, Attribute> attributes;
    private int attrItr = 1;
    private int attrMaxExp = 50;

    private JIntegerField mouseSource;
    private Color mouseColor;
    private boolean attrLocked = true;

    public AttributesTab() {
        // Needed for GUI Designer
    }
    public AttributesTab(CharacterGen parent, CharacterSheet sheet) {
        initialise(parent, sheet);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet) {
        this.sheet = sheet;
        this.connection = parent.getConnection();

        attributes = sheet.getRace().getAttributes(connection, 0);
        createTable();

        attrRollButton.addActionListener(e -> roll());
        attrRollAllButton.addActionListener(e -> {
            for (int i = attrItr; i <= 10; i++) {
                roll();
            }
        });
        attrRollAllButton.setMnemonic(KeyEvent.VK_R);
        attrOKButton.addActionListener(e -> {
            JIntegerField fld;
            int is_it_ok = 0;
            for (int j = 1; j <= 10; j++) {
                fld = (JIntegerField) attrAttributesTable.getComponent(j, 2);
                int value = fld.getValue();
                if ((value <= 20) && (value >= 2)) {
                    is_it_ok++;
                }
                else {
                    break;
                }
            }
            if (is_it_ok == 10) {
//                JOptionPane.showMessageDialog(mainPanel, "Jest ok", "Brawo!", JOptionPane.INFORMATION_MESSAGE);
                for (int k = attrItr; k <= 10; k++) {
                    fld = (JIntegerField) attrAttributesTable.getComponent(k, 2);
                    int value = fld.getValue();
                    attrSumField.changeValue(value);
                    attributes.get(k).setRndValue(value);
                    ((JIntegerField) attrAttributesTable.getComponent(k, 3)).setValue(attributes.get(k).getTotalValue());
                    fld.setEditable(false); // czy można potem przejść do kolejnej zakładki?
                    calculateHP();
                }
                moveToOptions();
            }
            else {
                JOptionPane.showMessageDialog(mainPanel, "Wpisano wartosci spoza zakresu", "Ogarnij sie, tworco", JOptionPane.ERROR_MESSAGE);
                for (int l = attrItr; l <= 10; l++) {
                    fld = (JIntegerField) attrAttributesTable.getComponent(l, 2);
                    fld.setValue(0);
                    fld.setEditable(true);
                }
            }
        });
        attrOption1Button.addActionListener(e -> {
            sheet.setAttributes(attributes);
            attrOption1Button.setEnabled(false);
            attrOption3Button.setEnabled(false);
            attrLocked = true;
            sheet.addExp(attrMaxExp);
            parent.moveToNextTab();
        });
        attrOption1Button.setMnemonic(KeyEvent.VK_1);
        attrOption3Button.addActionListener(e -> {
            //TODO: Make custom values input
        });
    }

    private void roll() {
        Attribute active = attributes.get(attrItr);
        int value = active.roll();

        JIntegerField field1 = (JIntegerField) attrAttributesTable.getComponent(attrItr, 2);
        field1.setValue(value);
        field1.setEditable(false);
        field1.setFocusable(false);
        JIntegerField field2 = (JIntegerField) attrAttributesTable.getComponent(attrItr, 3);
        field2.setValue(active.getTotalValue());

        calculateHP();
        attrSumField.changeValue(value);

        attrItr++;
        if (attrItr == 11) {
            moveToOptions();
        }
    }
    private void moveToOptions() {
        attrLocked = false;
        attrRollButton.setEnabled(false);
        attrRollAllButton.setEnabled(false);
        attrOKButton.setEnabled(false);
        attrOption1Button.setEnabled(true);
        attrOption3Button.setEnabled(true);

        if (attrSumField.getValue() > 100) {
            attrOption3Button.setForeground(Color.RED);
            attrOption3Button.setText("SELECT (You will lose some points)");
        }
    }

    private void calculateHP() {
        JIntegerField field3 = (JIntegerField) attrAttributesTable.getComponent(12, 1);
        field3.setValue(sheet.getMaxHealthPoints());
    }

    private void createTable() {
        attrAttributesTable.createJLabel(0, 0, attributes.get(Attribute.MOVE).getName());
        attrAttributesTable.createIntegerField(1, 0, 3, 1, attributes.get(Attribute.MOVE).getBaseValue(), GridPanel.STANDARD_INTEGER_FIELD, false);

        for (int i = 1; i < attributes.size(); i++) {
            int finalI = i;
            boolean changeBackground = sheet.getRace().getSize() == Size.NORMAL && i == 3 || i == 4 || i == 9;
            Color foregroundColor = Color.black;
            if (sheet.getProfession().hasAttribute(i)) {
                foregroundColor = ColorPalette.HALF_GREEN;
            }
            attrAttributesTable.createJLabel(0, i, attributes.get(i).getName());
            JIntegerField baseAttr = attrAttributesTable.createIntegerField(1, i, 1, 1, attributes.get(i).getBaseValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
            baseAttr.setForeground(foregroundColor);
            if (changeBackground) {
                baseAttr.setBackground(ColorPalette.WHITE_BLUE);
            }

            JIntegerField attr = attrAttributesTable.createIntegerField(2, i, null, GridPanel.STANDARD_INTEGER_FIELD);
            attr.setForeground(foregroundColor);
            attr.addActionListener(e -> attributes.get(finalI).setRndValue(attr.getValue()));
            attr.addMouseListener((MouseClickedAdapter) this::replaceValues);
            if (changeBackground) {
                attr.setBackground(ColorPalette.WHITE_BLUE);
            }

            JIntegerField sumAttr = attrAttributesTable.createIntegerField(3, i, 1, 1, attributes.get(i).getTotalValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(),Font.ITALIC+Font.BOLD,sumAttr.getFont().getSize()+2));
            sumAttr.setForeground(foregroundColor);
            if (changeBackground) {
                sumAttr.setBackground(ColorPalette.WHITE_BLUE);
            }
        }
        attrAttributesTable.createJLabel(0, 12, "HP");
        JIntegerField attrHP = attrAttributesTable.createIntegerField(1, 12, 3, 1, 0, GridPanel.STANDARD_INTEGER_FIELD, false);
        attrHP.setFont(new Font(attrHP.getFont().getName(),Font.ITALIC+Font.BOLD, attrHP.getFont().getSize()+2));
        attrHP.setBackground(ColorPalette.WHITE_BLUE);
        sheet.addObserver("maxhp", attrHP);
        attrAttributesTable.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void replaceValues(MouseEvent e) {
        if (!attrLocked) {
            if (mouseSource == null) {
                mouseSource = (JIntegerField) e.getSource();
                mouseColor = mouseSource.getForeground();
                mouseSource.setForeground(Color.red);
                mouseSource.setFont(new Font(mouseSource.getFont().getName(), Font.BOLD, mouseSource.getFont().getSize()));
            }
            else {
                JIntegerField target = (JIntegerField) e.getSource();
                int temp = target.getValue();
                target.setValue(mouseSource.getValue());
                mouseSource.setValue(temp);
                mouseSource.setForeground(mouseColor);
                mouseSource.setFont(new Font(mouseSource.getFont().getName(), Font.PLAIN, mouseSource.getFont().getSize()));
                mouseSource = null;
            }
            attrMaxExp = 25;
            calculateHP();
            attrAttributesTable.iterateThroughColumns(3, (o, i) -> ((JIntegerField) o).setValue(attributes.get(i).getTotalValue()));
        }
    }
}
