package tabs;

import com.intellij.uiDesigner.core.GridConstraints;
import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.GridPanel;
import components.JIntegerField;
import components.SearchableComboBox;
import components.SpinnerTypeListModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import main.CharacterGen;
import main.CharacterSheet;
import main.Connection;
import mappings.Skill;
import mappings.SkillGroup;
import mappings.Talent;
import mappings.TalentGroup;
import org.apache.logging.log4j.LogManager;
import tools.AbstractActionHelper;
import tools.MultiLineTooltip;

public class RaceSkillTab {
    private CharacterSheet sheet;
    private CharacterGen parent;
    private Connection connection;

    private GridPanel skillsPanel;
    private GridPanel talentsPanel;
    private JPanel mainPanel;
    private GridPanel randomTalentsPanel;
    private JPanel raceskillRollPanel;
    private JButton raceskillOption1;
    private JButton raceskillRollButton;
    private JButton raceskillOKButton;
    private JIntegerField raceskillRollResult;

    private final Map<Integer, Integer> raceskillPoints = new ConcurrentHashMap<>();

    private List<SkillGroup> raceSkillGroups;
    private List<TalentGroup> raceTalentGroups;
    private final List<TalentGroup> randomTalentGroups = new ArrayList<>();

    private List<Skill> raceSkills = new ArrayList<>();
    private final List<Talent> raceTalents = new ArrayList<>();
    private final List<Talent> randomTalents = new ArrayList<>();

    public RaceSkillTab() {
        // Needed for GUI Designer
    }
    public RaceSkillTab(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        initialise(parent, sheet, connection);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        this.sheet = sheet;
        this.parent = parent;
        this.connection = connection;

        raceskillPoints.put(0, 0);
        raceskillPoints.put(3, 0);
        raceskillPoints.put(5, 0);
        raceSkillGroups = sheet.getRace().getRaceSkills(sheet.getAttributes());
        raceSkillGroups.forEach(e -> raceSkills.add(e.getFirstSkill()));
        raceTalentGroups = sheet.getRace().getRaceTalents(sheet.getAttributes());
        raceTalentGroups.forEach(e -> raceTalents.add(e.getFirstTalent()));

        createSkillTable(raceSkillGroups, raceSkills);
        updateSkillTable(raceSkills);
        createTalentTable(raceTalentGroups, raceTalents);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
        AbstractActionHelper.createActionMnemonic(mainPanel, stroke, () -> rollAll(raceSkillGroups, raceTalentGroups));
        raceskillRollButton.addActionListener(e -> roll());
        raceskillRollButton.setMnemonic(KeyEvent.VK_R);
        raceskillOption1.addActionListener(e -> {
            List<Skill> tempList = new ArrayList<>();
            for (Skill skill : raceSkills) {
                if (skill.getAdvValue() != 0) {
                    tempList.add(skill);
                }
            }
            raceSkills = tempList;

            sheet.setSkillList(raceSkills);
            sheet.setTalentList(raceTalents);
            sheet.addTalents(randomTalents);

            System.out.println(sheet);

            parent.moveToNextTab();
        });
    }

    private void createSkillTable(List<SkillGroup> raceSkillGroups, List<Skill> raceSkills) {
        List<Component> tabOrder = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;
        int row;
        int column;
        for (int i = 0; i < raceSkillGroups.size(); i++) {
            int finalI = i;
            Color color = Color.black;

            if (raceSkills.get(i).isAdv()) {
                column = 4;
                row = advItr++;
                color = Color.red;
            } else {
                column = 0;
                row = baseItr++;
            }

            Container nameContainer = skillsPanel.createSkillComboIfNeeded(raceSkillGroups.get(i), row, column++);
            nameContainer.setForeground(color);

            JTextField attrField = skillsPanel.createTextField(row, column++, raceSkills.get(i).getAttr().getName(), new Dimension(30, -1), false);
            attrField.setFocusable(false);

            AdvancedSpinner jSpinner = skillsPanel.createAdvancedSpinner(row, column++, 1, 1, new SpinnerTypeListModel<>(new Integer[]{0, 3, 5}), new Dimension(35, -1), true);

            JIntegerField sumField = skillsPanel.createIntegerField(row, column++, raceSkills.get(i).getTotalValue(), new Dimension(30, -1));
            sumField.setEditable(false);
            sumField.setFocusable(false);

            jSpinner.addChangeListener(e -> updateSkillRow(nameContainer, jSpinner, sumField, finalI));
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
    private void updateSkillTable(List<Skill> raceSkills) {
        int baseItr = 1;
        int advItr = 1;
        int column;
        int row;
        for (Skill skill : raceSkills) {
            if (skill.isAdv()) {
                column = 4;
                row = advItr++;
            } else {
                column = 0;
                row = baseItr++;
            }

            if (skillsPanel.getComponent(column, row) instanceof SearchableComboBox) {
                ((SearchableComboBox) skillsPanel.getComponent(column, row)).getModel().setSelectedItem(skill.getName());
            }
            ((AdvancedSpinner) skillsPanel.getComponent(column + 2, row)).setValue(skill.getAdvValue());
        }
    }

    private void createTalentTable(List<TalentGroup> raceTalentGroups, List<Talent> raceTalents) {
        final Dimension[] columns = new Dimension[]{
                new Dimension(200, -1),
                new Dimension(30, -1),
                new Dimension(30, -1),
                new Dimension(200, -1),
                new Dimension(200, -1)
        };

        talentsPanel.createJLabel(0,0,1,-1, "Talents");
        for (int i = 0; i < raceTalentGroups.size(); i++) {
            int finalI = i + 1;
            int column = 0;

            talentsPanel.createTalentComboIfNeeded(raceTalentGroups.get(i), finalI, column++, columns[column-1], e ->
                    updateTalentRow(raceTalentGroups.get(finalI), raceTalents, talentsPanel, finalI, ((SearchableComboBox) e.getSource()).getSelectedIndex()));

            JIntegerField attrField = talentsPanel.createIntegerField(finalI, column++, raceTalents.get(i).getCurrentLvl(), columns[column-1]);
            attrField.setEditable(false);
            attrField.setFocusable(false);

            JTextField maxAttrField = talentsPanel.createTextField(finalI, column++, raceTalents.get(i).getMaxString(), columns[column-1], false);
            maxAttrField.setFocusable(false);

            JTextArea testArea = talentsPanel.createTextArea(finalI, column++, raceTalents.get(i).getBaseTalent().getTest(), columns[column-1], false);
            testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

            String tooltip = raceTalents.get(i).getBaseTalent().getDesc();
            talentsPanel.createJLabel(finalI, column, new ImageIcon("src/resources/images/info.png"), MultiLineTooltip.splitToolTip(tooltip, 75, 10));
        }
        talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

        if (sheet.getSubrace().getRandomTalents() != 0) {
            raceskillRollPanel.setVisible(true);
            for (int i=0;i<sheet.getSubrace().getRandomTalents();i++) {
                int column = 0;

                randomTalentsPanel.createSearchableComboBox(i, column++, columns[column-1], true);

                JTextField attrField = randomTalentsPanel.createTextField(i, column++, "", columns[column-1], false);
                attrField.setFocusable(false);

                JTextField attrField2 = randomTalentsPanel.createTextField(i, column++, "", columns[column-1], false);
                attrField2.setFocusable(false);

                JTextArea testArea = randomTalentsPanel.createTextArea(i, column++, "", columns[column-1], false);
                testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));

                randomTalentsPanel.createJLabel(i, column++, new ImageIcon("src/resources/images/info.png"), "");
            }
            randomTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
        }
    }

    private void updateSkillRow(Container container, AdvancedSpinner spinner, JIntegerField totalField, int index) {
        Skill skill = raceSkills.get(index);
        int now = (int) spinner.getValue();
        int last = now == skill.getAdvValue() ? (int) spinner.getLastValue() : skill.getAdvValue();

        if (last != now) {
            skill.setAdvValue(now);
            totalField.setValue(skill.getTotalValue());

            raceskillPoints.put(last, raceskillPoints.get(last)-1);
            raceskillPoints.put(now, raceskillPoints.get(now)+1);
        }

        if (now == 0 && skill.getBaseSkill().isAdv()) {
            container.setForeground(Color.red);
        } else {
            container.setForeground(Color.black);
        }

        skillsPanel.iterateThroughRows(2, (o, i) -> changeColor(o));
        skillsPanel.iterateThroughRows(6, (o, i) -> changeColor(o));
    }
    private void changeColor(Object object) {
        AdvancedSpinner active = (AdvancedSpinner) object;
        if (raceskillPoints.get((int) active.getValue()) > 3) {
            active.getTextField().setForeground(Color.RED);
        } else {
            active.getTextField().setForeground(Color.BLACK);
        }
    }

    private void updateTalentRow(TalentGroup talentGroup, List<Talent> finalTalents, GridPanel panel, int row, int index) {
        Talent talent = talentGroup.getTalents().get(index);

        if (talentGroup.countTalents() != 1) {
            SearchableComboBox comboBox = (SearchableComboBox) panel.getComponent(0, row);
            comboBox.setSelectedItem(talent.getName());
            JTextArea textArea = (JTextArea) panel.getComponent(3, row);
            textArea.setText(talent.getBaseTalent().getTest());
            JLabel desc = (JLabel) panel.getComponent(4, row);
            desc.setToolTipText(MultiLineTooltip.splitToolTip(talent.getBaseTalent().getDesc()));

            finalTalents.set(row-1, talent);
        }
        JTextField testField = (JTextField) panel.getComponent(1, row);
        testField.setText(String.valueOf(talent.getCurrentLvl()));
        JTextField testField2 = (JTextField) panel.getComponent(2, row);
        testField2.setText(talent.getMaxString());
    }

    private void roll() {
        if (raceskillRollButton.isEnabled()) {
            Object[] result = CharacterGen.getRandomTalent(connection);
            TalentGroup rollTalent = (TalentGroup) result[1];
            int rollResultNumeric = (int) result[0];
            while (randomTalentGroups.contains(rollTalent)) {
                LogManager.getLogger(getClass().getName()).debug(String.format("%d doubled, changing to %d", rollResultNumeric, rollResultNumeric % 100 + 1));
                rollResultNumeric = rollResultNumeric % 100 + 1;
                rollTalent = connection.getRandomTalent(rollResultNumeric);
            }
            randomTalentGroups.add(rollTalent);
            randomTalents.add(rollTalent.getFirstTalent());
            raceskillRollResult.setValue(rollResultNumeric);

            TalentGroup rollTalentGroup = rollTalent;
            int row = randomTalentGroups.size() - 1;

            SearchableComboBox activeField = (SearchableComboBox) randomTalentsPanel.getComponent(0, row);
            for (Talent talent : rollTalentGroup.getTalents()) {
                activeField.addItem(talent.getName());
            }
            activeField.refresh();
            activeField.setLocked(activeField.getItemCount() == 1);
            activeField.setEditable(!rollTalentGroup.isLocked());
            String tooltip = rollTalentGroup.getName();
            if (tooltip != null) {
                activeField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            }
            activeField.addActionListener(e -> updateTalentRow(rollTalentGroup, randomTalents, randomTalentsPanel, row, ((SearchableComboBox) e.getSource()).getSelectedIndex()));

            JTextField activeField2 = (JTextField) randomTalentsPanel.getComponent(1, row);
            activeField2.setText(String.valueOf(rollTalentGroup.getFirstTalent().getCurrentLvl()));

            JTextField activeField3 = (JTextField) randomTalentsPanel.getComponent(2, row);
            activeField3.setText(rollTalentGroup.getFirstTalent().getMaxString());

            JTextArea activeArea = (JTextArea) randomTalentsPanel.getComponent(3, row);
            activeArea.setText(rollTalentGroup.getFirstTalent().getBaseTalent().getTest());

            JLabel activeLabel = (JLabel) randomTalentsPanel.getComponent(4, row);
            activeLabel.setToolTipText(MultiLineTooltip.splitToolTip(rollTalentGroup.getFirstTalent().getBaseTalent().getDesc(), 75, 10));

            if (sheet.getSubrace().getRandomTalents() == randomTalentGroups.size()) {
                raceskillRollButton.setEnabled(false);
                raceskillRollResult.setEditable(false);
                raceskillOKButton.setEnabled(false);
            }
        }
    }
    private void rollAll(List<SkillGroup> skillGroups, List<TalentGroup> talentGroups) {
        raceSkills = CharacterGen.randomizeSkillsWithList(skillGroups, new int[]{3, 3, 3, 5, 5, 5});
        updateSkillTable(raceSkills);
//
//        for (int i = 0; i < talentGroups.size(); i++) {
//            updateTalentRow(talentGroups.get(i), raceTalents, talentsPanel, i + 1, talentGroups.get(i).getRndTalentIndex());
//        }
//        for (int i = 0; i < sheet.getSubrace().getRandomTalents(); i++) {
//            roll();
//        }
    }
}
