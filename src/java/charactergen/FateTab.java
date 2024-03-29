package charactergen;

import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.GridPanel;
import components.JIntegerField;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import main.CharacterSheet;
import mappings.Attribute;
import tools.AbstractActionHelper;

class FateTab {
    private CharacterSheet sheet;

    private JPanel mainPanel;
    private JIntegerField fateAttrRemain;
    private GridPanel fateAttributeTable;
    private JButton fateOption1Button;
    private JIntegerField fateExtra;
    private JIntegerField fateResilience;
    private JIntegerField fateFate;
    private AdvancedSpinner fateSpinner;
    private AdvancedSpinner resilienceSpinner;

    private Map<Integer, Attribute> attributes;

    public FateTab() {
        // Needed for GUI Designer
    }
    public FateTab(CharacterGen parent, CharacterSheet sheet) {
        initialise(parent, sheet);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet) {
        this.sheet = sheet;
        createTable();

        AbstractActionHelper.createActionMnemonic(mainPanel, KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK, this::roll);
        fateOption1Button.addActionListener(e -> {
            fateOption1Button.setEnabled(false);
            fateSpinner.setEnabled(false);
            resilienceSpinner.setEnabled(false);
            fateAttributeTable.iterateThroughColumns(2, (o, i) -> {
                AdvancedSpinner spinner = (AdvancedSpinner) o;
                spinner.setEnabled(false);
            });

            sheet.setAttributes(sheet.getAttributes());
            parent.moveToNextTab();
        });
        fateOption1Button.setMnemonic(KeyEvent.VK_1);
    }

    private void roll() {
        attributes = CharacterGen.randomAttributeAdvances(sheet.getProfession(), attributes, 5);
        fateAttributeTable.iterateThroughColumns(2, (o, i) -> ((AdvancedSpinner) o).setValue(attributes.get(i).getAdvValue()));
        fateAttributeTable.iterateThroughColumns(3, (o, i) -> ((JIntegerField) o).setValue(attributes.get(i).getTotalValue()));
        ((JIntegerField) fateAttributeTable.getComponent(12, 1)).setValue(sheet.getMaxHealthPoints());
        fateAttrRemain.setValue(0);
        fateOption1Button.setEnabled(true);
    }
    private void createTable() {
        List<Component> tabOrder = new ArrayList<>();
        attributes = sheet.getAttributes();

        fateAttributeTable.createJLabel(0, 0, attributes.get(11).getName());
        fateAttributeTable.createIntegerField(1, 0, 3, 1, attributes.get(11).getBaseValue(), GridPanel.STANDARD_INTEGER_FIELD, false);

        for (int i = 1; i < attributes.size(); i++) {
            int finalI = i;

            fateAttributeTable.createJLabel(0, i, attributes.get(i).getName());
            fateAttributeTable.createIntegerField(1, i, 1, 1, sheet.getAttributes().get(i).getTotalValue(), GridPanel.STANDARD_INTEGER_FIELD, false);

            AdvancedSpinner advancedSpinner = fateAttributeTable.createAdvancedSpinner(2, i, new SpinnerNumberModel(0, 0, 5, 1), GridPanel.STANDARD_INTEGER_FIELD, false);
            if (sheet.getProfession().hasAttribute(i)) {
                advancedSpinner.setEnabled(true);
                tabOrder.add(advancedSpinner.getTextField());
            }

            JIntegerField sumAttr = fateAttributeTable.createIntegerField(3, i, 1, 1, sheet.getAttributes().get(i).getTotalValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(), Font.ITALIC + Font.BOLD, sumAttr.getFont().getSize() + 2));

            advancedSpinner.addChangeListener(e -> updatePoints(advancedSpinner, finalI, sumAttr));
        }
        fateAttributeTable.createJLabel(0, 12, "HP");
        fateAttributeTable.createIntegerField(1, 12, 3, 1, sheet.getMaxHealthPoints(), GridPanel.STANDARD_INTEGER_FIELD, false);

        fateAttributeTable.setFocusCycleRoot(true);
        fateAttributeTable.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        fateAttributeTable.build(GridPanel.ALIGNMENT_HORIZONTAL);

        int fate = sheet.getRace().getFate();
        int resilience = sheet.getRace().getResilience();
        int extra = sheet.getRace().getExtra();

        fateFate.setValue(fate);
        fateResilience.setValue(resilience);
        fateExtra.setValue(extra);
        fateSpinner.setModel(new SpinnerNumberModel(0, 0, extra, 1));
        resilienceSpinner.setModel(new SpinnerNumberModel(0, 0, extra, 1));
    }
    private void updatePoints(AdvancedSpinner activeSpinner, int finalI, JIntegerField field) {
        int now = (int) (activeSpinner.getValue());
        int adv = sheet.getAttributes().get(finalI).getAdvValue();

        if (adv != now) {
            fateAttrRemain.changeValue(adv - now);
            field.changeValue(now - adv);
            sheet.getAttributes().get(finalI).setAdvValue(now);

            JIntegerField integerField = (JIntegerField) fateAttributeTable.getComponent(12, 1);
            integerField.setValue(sheet.getMaxHealthPoints());

            fateAttributeTable.iterateThroughColumns(2, (o, i) -> {
                AdvancedSpinner spinner = (AdvancedSpinner) o;
                if (activeSpinner != o) {
                    SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                    model.setMaximum((int) model.getValue() + fateAttrRemain.getValue());
                }
            });
        }
        fateOption1Button.setEnabled(fateAttrRemain.getValue() == 0);
    }
    private void createUIComponents() {
        fateAttrRemain = new JIntegerField(5);
    }
}
