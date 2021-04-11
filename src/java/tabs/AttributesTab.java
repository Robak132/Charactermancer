package tabs;

import components.GridPanel;
import components.JIntegerField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import main.CharacterGen;
import main.CharacterSheet;
import main.Connection;
import mappings.Attribute;
import mappings.Race.Size;
import tools.ColorPalette;
import tools.MouseClickedAdapter;

public class AttributesTab {
    private CharacterSheet sheet;

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
    public AttributesTab(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        initialise(parent, sheet, connection);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        this.sheet = sheet;
        attributes = sheet.getRace().getAttributes();
        createTable();

        attrRollButton.addActionListener(e -> roll());
        attrRollAllButton.addActionListener(e -> {
            for (int i = 0; i < 10; i++) {
                roll();
            }
        });
        attrRollAllButton.setMnemonic(KeyEvent.VK_R);
        attrOKButton.addActionListener(e -> {
            //TODO Add custom values given by user
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
        Attribute attribute = CharacterGen.getOneRandomAttr(attrItr, sheet.getRace());

        JIntegerField field1 = (JIntegerField) attrAttributesTable.getComponent(attrItr, 2);
        field1.setValue(attribute.getRndValue());
        field1.setEditable(false);
        JIntegerField field2 = (JIntegerField) attrAttributesTable.getComponent(attrItr, 3);
        field2.setValue(attribute.getTotalValue());

        calculateHP();
        attrSumField.changeValue(attribute.getRndValue());
        sheet.addAttribute(attribute.getID(), attribute);

        attrItr++;
        if (attrItr == 11) {
            int HP = sheet.getMaxHealthPoints();
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
    }
    private void calculateHP() {
        JIntegerField field3 = (JIntegerField) attrAttributesTable.getComponent(12, 1);
        field3.setValue(sheet.getMaxHealthPoints());
    }

    private void createTable() {
        for (int i = 0; i < attributes.size(); i++) {
            int finalI = i;
            boolean changeBackground = sheet.getRace().getSize()== Size.NORMAL && i == 3 || i == 4 || i == 9;
            Color foregroundColor = Color.black;
            if (sheet.getProfession().hasAttribute(i)) {
                foregroundColor = ColorPalette.HALF_GREEN;
            }
            attrAttributesTable.createJLabel(0, i, attributes.get(i).getName());
            if (attributes.get(i).isRollable()) {
                JIntegerField baseAttr = attrAttributesTable.createIntegerField(1, i, 1, 1, attributes.get(i).getBaseValue(), new Dimension(30, -1), false);
                baseAttr.setForeground(foregroundColor);
                if (changeBackground) {
                    baseAttr.setBackground(ColorPalette.WHITE_BLUE);
                }

                JIntegerField attr = attrAttributesTable.createIntegerField(2, i, 0, new Dimension(30, -1));
                attr.setForeground(foregroundColor);
                attr.setRunnable((o, j) -> attributes.get(finalI).setRndValue(attr.getValue()));
                attr.addMouseListener((MouseClickedAdapter) this::replaceValues);
                if (changeBackground) {
                    attr.setBackground(ColorPalette.WHITE_BLUE);
                }

                JIntegerField sumAttr = attrAttributesTable.createIntegerField(3, i, 1, 1, attributes.get(i).getTotalValue(), new Dimension(30, -1), false);
                sumAttr.setFont(new Font(sumAttr.getFont().getName(),Font.ITALIC+Font.BOLD,sumAttr.getFont().getSize()+2));
                sumAttr.setForeground(foregroundColor);
                if (changeBackground) {
                    sumAttr.setBackground(ColorPalette.WHITE_BLUE);
                }
            } else {
                JIntegerField baseAttr = attrAttributesTable.createIntegerField(1, i, 3, 1, attributes.get(i).getBaseValue(), new Dimension(30, -1), false);
                baseAttr.setForeground(foregroundColor);
                if (changeBackground) {
                    baseAttr.setBackground(ColorPalette.WHITE_BLUE);
                }
            }
        }
        attrAttributesTable.createJLabel(0, 12, "HP");

        JIntegerField attrHP = attrAttributesTable.createIntegerField(1, 12, 3, 1, 0, new Dimension(30, -1), false);
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
                mouseSource.setFont(new Font(mouseSource.getFont().getName(),Font.BOLD, mouseSource.getFont().getSize()));
            }
            else {
                JIntegerField target = (JIntegerField) e.getSource();
                int temp = target.getValue();
                target.setValue(mouseSource.getValue());
                mouseSource.setValue(temp);
                mouseSource.setForeground(mouseColor);
                mouseSource.setFont(new Font(mouseSource.getFont().getName(),Font.PLAIN, mouseSource.getFont().getSize()));
                mouseSource = null;
            }
            attrMaxExp = 25;
            calculateHP();
            attrAttributesTable.iterateThroughColumns(3, (o, i) -> ((JIntegerField) o).setValue(attributes.get(i).getTotalValue()));
        }
    }
}
