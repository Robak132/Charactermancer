package tabs;

import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.FilteredComboBox;
import components.GridPanel;
import components.JIntegerField;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import mappings.Skill;
import mappings.SkillSingle;
import mappings.TalentSingle;
import tools.MultiLineTooltip;

public class SkillTab {
    protected void createSkillTable(GridPanel skillsPanel, List<Skill> raceSkills, List<SkillSingle> visibleRaceSkills, AdvancedSpinner jspinner) {
        List<Component> tabOrder = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;
        for (int i = 0; i < raceSkills.size(); i++) {
            Skill raceSkill = raceSkills.get(i);
            int finalRow = raceSkill.isAdv() ? advItr++ : baseItr++;
            int column = raceSkill.isAdv() ? 4 : 0;
            int finalI = i;
            int finalColumn = column;

            SkillSingle activeSkill = skillsPanel.createComboIfNeeded(raceSkill, finalRow, column++, SkillSingle::getColor,
                    newSkill -> updateSkillRow(skillsPanel, visibleRaceSkills, finalI, finalRow, finalColumn, newSkill));
            visibleRaceSkills.add(activeSkill);

            skillsPanel.createTextField(finalRow, column++, activeSkill.getAttrName(), new Dimension(30, -1), false);

            SpinnerModel model = new SpinnerNumberModel(activeSkill.getAdvValue(), activeSkill.getAdvValue(), 10, 1);
            AdvancedSpinner jSpinner = skillsPanel.createAdvancedSpinner(finalRow, column++, model, new Dimension(35, -1), true);

            skillsPanel.createIntegerField(finalRow, column++, activeSkill.getBaseSkill().getLinkedAttribute().getTotalValue(), new Dimension(30, -1), false);
            tabOrder.add(jSpinner.getTextField());
        }
        if (baseItr != 1) {
            skillsPanel.createJLabel(0, 0, 1, 4, "Basic skills");
        }
        if (advItr != 1) {
            skillsPanel.createJLabel(0, 4, 1, 4, "Advanced skills");
        }
        skillsPanel.setFocusCycleRoot(true);
        skillsPanel.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    protected void updateSkillRow(GridPanel panel, List<SkillSingle> visibleSkills, int idx, int row, int column, SkillSingle newSkill) {
        if (panel.getComponent(column, row) instanceof FilteredComboBox<?>) {
            ((FilteredComboBox<?>) panel.getComponent(column, row)).setSelectedItem(newSkill);
        }
        panel.getComponent(column, row).setForeground(newSkill.getColor());

        ((JTextField) panel.getComponent(column + 1, row)).setText(newSkill.getAttrName());
        ((AdvancedSpinner) panel.getComponent(column + 2, row)).setValue(newSkill.getAdvValue());
        ((JIntegerField) panel.getComponent(column + 3, row)).setValue(newSkill.getTotalValue());

        visibleSkills.set(idx, newSkill);
    }
    protected void updateSkillRow(GridPanel panel, List<SkillSingle> visibleSkills, int idx, int row, int column) {
        updateSkillRow(panel,visibleSkills, idx, row, column, visibleSkills.get(idx));
    }

    protected void updateTalentRow(GridPanel panel, int idx, int row, List<TalentSingle> talentList, TalentSingle newTalent) {
        Component nameField = panel.getComponent(0, row);
        if (nameField instanceof FilteredComboBox) {
            ((FilteredComboBox<?>) nameField).setSelectedItem(newTalent);
        }
        nameField.setForeground(newTalent.getColor());

        ((JIntegerField) panel.getComponent(1, row)).setValue(newTalent.getCurrentLvl());
        panel.getComponent(1, row).setForeground(newTalent.getColor());

        ((JIntegerField) panel.getComponent(2, row)).setValue(newTalent.getMax());
        panel.getComponent(2, row).setForeground(newTalent.getColor());

        ((JTextArea) panel.getComponent(3, row)).setText(newTalent.getBaseTalent().getTest());
        ((JLabel) panel.getComponent(4, row)).setToolTipText(MultiLineTooltip.splitToolTip(newTalent.getBaseTalent().getDesc()));
        talentList.set(idx, newTalent);
    }
    protected void updateTalentRow(GridPanel panel, int idx, int row, List<TalentSingle> talentList) {
        updateTalentRow(panel, idx, row, talentList, talentList.get(idx));
    }

}
