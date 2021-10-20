package tools;

import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.FilteredComboBox;
import components.GridPanel;
import components.JIntegerField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import mappings.Skill;
import mappings.SkillSingle;

public interface SkillTab {
    default void createSkillTable(GridPanel skillsPanel, List<Skill> skills, List<SkillSingle> visibleSkills) {
        createSkillTable(skillsPanel, skills, visibleSkills, s->Color.BLACK);
    }
    default void createSkillTable(GridPanel skillsPanel, List<Skill> skills, List<SkillSingle> visibleSkills, Function<SkillSingle, Color> colorFunction) {
        List<Component> tabOrder = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;

        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            int finalRow = skill.isAdv() ? advItr++ : baseItr++;
            int column = skill.isAdv() ? 4 : 0;
            int finalI = i;
            int finalColumn = column;

            SkillSingle activeSkill = skillsPanel.createComboIfNeeded(skill, finalRow, column++, colorFunction,
                    newSkill -> updateSkillRow(skillsPanel, visibleSkills, finalI, finalRow, finalColumn, newSkill));
            visibleSkills.add(activeSkill);

            skillsPanel.createTextField(finalRow, column++, activeSkill.getAttrName(), GridPanel.STANDARD_INTEGER_FIELD, false);

            SpinnerModel model = getSkillSpinnerModel(activeSkill);
            AdvancedSpinner jSpinner = skillsPanel.createAdvancedSpinner(finalRow, column++, model, new Dimension(35, -1), true);
            jSpinner.addChangeListener(e -> {
                if (!jSpinner.isLocked()) {
                    skillSpinnerChange(finalI, finalRow, finalColumn, jSpinner);
                }
            });

            skillsPanel.createIntegerField(finalRow, column++, activeSkill.getBaseSkill().getLinkedAttribute().getTotalValue(),
                    GridPanel.STANDARD_INTEGER_FIELD, false);
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

    default void updateSkillRow(GridPanel panel, List<SkillSingle> visibleSkills, int idx, int row, int column, SkillSingle newSkill) {
        if (panel.getComponent(column, row) instanceof FilteredComboBox<?>) {
            ((FilteredComboBox<?>) panel.getComponent(column, row)).setSelectedItem(newSkill);
        }
        panel.getComponent(column, row).setForeground(newSkill.getColor());

        ((JTextField) panel.getComponent(column + 1, row)).setText(newSkill.getAttrName());
        ((AdvancedSpinner) panel.getComponent(column + 2, row)).setValue(newSkill.getAdvValue());
        ((JIntegerField) panel.getComponent(column + 3, row)).setValue(newSkill.getTotalValue());

        visibleSkills.set(idx, newSkill);
    }
    default void updateSkillRow(GridPanel panel, List<SkillSingle> visibleSkills, int idx, int row, int column) {
        updateSkillRow(panel, visibleSkills, idx, row, column, visibleSkills.get(idx));
    }

    SpinnerModel getSkillSpinnerModel(SkillSingle skill);
    default void skillSpinnerChange(int idx, int row, int column, AdvancedSpinner spinner) {}
}