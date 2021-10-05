package tabs;

import components.AdvancedSpinner;
import components.FilteredComboBox;
import components.GridPanel;
import components.JIntegerField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.function.Function;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import mappings.Talent;
import mappings.TalentSingle;
import tools.MultiLineTooltip;

public interface TalentTab {
    default void createTalentTable(GridPanel talentsPanel, List<Talent> talents, List<TalentSingle> visibleTalents) {
        createTalentTable(talentsPanel, talents, visibleTalents, t->Color.BLACK);
    }
    default void createTalentTable(GridPanel talentsPanel, List<Talent> talents, List<TalentSingle> visibleTalents, Function<TalentSingle, Color> colorFunction) {
        talentsPanel.createJLabel(0,0,1,-1, "Talents");
        for (int i = 0; i < talents.size(); i++) {
            Talent talent = talents.get(i);
            int finalI = i;
            int row = i + 1;
            int column = 0;

            TalentSingle activeTalent = talentsPanel.createComboIfNeeded(talent, row, column++, colorFunction,
                    newTalent -> updateTalentRow(talentsPanel, finalI, row, visibleTalents, colorFunction, newTalent));
            visibleTalents.add(activeTalent);

            SpinnerModel model = getTalentSpinnerModel(activeTalent);
            AdvancedSpinner spinner = talentsPanel.createAdvancedSpinner(row, column++, model, GridPanel.STANDARD_INTEGER_FIELD, (int)model.getValue()==0);
            spinner.setForeground(colorFunction.apply(activeTalent));

            JIntegerField maxLvl = talentsPanel.createIntegerField(row, column++, activeTalent.getMax(), GridPanel.STANDARD_INTEGER_FIELD, false);
            maxLvl.setForeground(colorFunction.apply(activeTalent));

            JTextArea testArea = talentsPanel.createTextArea(row, column++, activeTalent.getBaseTalent().getTest(), GridPanel.STANDARD_TEXT_FIELD, false);
            testArea.setForeground(colorFunction.apply(activeTalent));
            testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

            String tooltip = activeTalent.getBaseTalent().getDesc();
            talentsPanel.createJLabel(row, column, new ImageIcon("src/resources/images/info.png"), MultiLineTooltip.splitToolTip(tooltip, 75, 10));
        }
        talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }

    default void updateTalentRow(GridPanel panel, int idx, int row, List<TalentSingle> talentList, Function<TalentSingle, Color> colorFunction, TalentSingle newTalent) {
        Component nameField = panel.getComponent(0, row);
        if (nameField instanceof FilteredComboBox) {
            ((FilteredComboBox<?>) nameField).setSelectedItem(newTalent);
        }
        nameField.setForeground(colorFunction.apply(newTalent));

        ((AdvancedSpinner) panel.getComponent(1, row)).setValue(newTalent.getCurrentLvl());
        panel.getComponent(1, row).setForeground(colorFunction.apply(newTalent));

        ((JIntegerField) panel.getComponent(2, row)).setValue(newTalent.getMax());
        panel.getComponent(2, row).setForeground(colorFunction.apply(newTalent));

        ((JTextArea) panel.getComponent(3, row)).setText(newTalent.getBaseTalent().getTest());
        ((JLabel) panel.getComponent(4, row)).setToolTipText(MultiLineTooltip.splitToolTip(newTalent.getBaseTalent().getDesc()));
        talentList.set(idx, newTalent);
    }
    default void updateTalentRow(GridPanel panel, int idx, int row, List<TalentSingle> talentList, Function<TalentSingle, Color> colorFunction) {
        updateTalentRow(panel, idx, row, talentList, colorFunction, talentList.get(idx));
    }

    SpinnerModel getTalentSpinnerModel(TalentSingle talent);
    default void talentSpinnerChange(int idx, int row, int column, AdvancedSpinner spinner) {}
}
