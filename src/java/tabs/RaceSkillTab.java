package tabs;

import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.GridPanel;
import components.JIntegerField;
import components.SearchableComboBox;
import components.SpinnerTypeListModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import main.CharacterGen;
import main.CharacterSheet;
import main.Connection;
import mappings.*;
import tools.AbstractActionHelper;
import tools.Dice;
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

    private List<Skill> raceSkills = new ArrayList<>();
    private List<Talent> raceTalents = new ArrayList<>();
    private final List<Talent> randomTalents = new ArrayList<>();
    private final List<SkillSingle> visibleRaceSkills = new ArrayList<>();
    private final List<TalentSingle> visibleRaceTalents = new ArrayList<>();
    private final List<TalentSingle> visibleRandomTalents = new ArrayList<>();

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
        raceSkills = sheet.getRace().getRaceSkills(sheet.getAttributes());
        raceTalents = sheet.getRace().getRaceTalents(sheet.getAttributes());

        createSkillTable(raceSkills);
        createTalentTable(raceTalents);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
        AbstractActionHelper.createActionMnemonic(mainPanel, stroke, this::rollSkills);
//        raceskillRollButton.addActionListener(e -> roll());
//        raceskillRollButton.setMnemonic(KeyEvent.VK_R);
//        raceskillOption1.addActionListener(e -> {
//            List<Skill> tempList = new ArrayList<>();
//            for (Skill skill : raceSkills) {
//                if (skill.getAdvValue() != 0) {
//                    tempList.add(skill);
//                }
//            }
//            raceSkills = tempList;
//
//            sheet.setSkillList(raceSkills);
//            sheet.setTalentList(raceTalents);
//            sheet.addTalents(randomTalents);
//
//            System.out.println(sheet);
//
//            parent.export();
//        });
    }

    private void createSkillTable(List<Skill> raceSkills) {
        List<Component> tabOrder = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;
        int row;
        int column;
        for (int i = 0; i < raceSkills.size(); i++) {
            Skill raceSkill = raceSkills.get(i);
            Color color = Color.black;

            if (raceSkill.isAdv()) {
                column = 4;
                row = advItr++;
                color = Color.red;
            } else {
                column = 0;
                row = baseItr++;
            }
            int finalI = i;
            int finalRow = row;
            int finalColumn = column;
            SkillSingle activeSkill = createComboIfNeeded(raceSkill, i, row, column++, color);

            JTextField attrField = skillsPanel.createTextField(row, column++, activeSkill.getAttrName(), new Dimension(30, -1), false);

            AdvancedSpinner jSpinner = skillsPanel.createAdvancedSpinner(row, column++, 1, 1, new SpinnerTypeListModel<>(new Integer[]{0, 3, 5}), new Dimension(35, -1), true);
            jSpinner.addChangeListener(e -> skillSpinnerChange(finalI, finalRow, finalColumn, jSpinner));

            JIntegerField sumField = skillsPanel.createIntegerField(row, column++, activeSkill.getBaseSkill().getLinkedAttribute().getTotalValue(), new Dimension(30, -1));
            sumField.setEditable(false);

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
    private void skillSpinnerChange(int idx, int row, int column, AdvancedSpinner jSpinner) {
        int lastManual = visibleRaceSkills.get(idx).getAdvValue();
        int lastAuto = (int) jSpinner.getLastValue();
        int now = (int) jSpinner.getValue();

        if (lastManual != now) {
            // Manual change of Spinner
            raceskillPoints.put(lastManual, raceskillPoints.get(lastManual) + 1);
            raceskillPoints.put(now, raceskillPoints.get(now) - 1);
            visibleRaceSkills.get(idx).setAdvValue(now);
            updateSkillRow(idx, row, column);
        } else if (lastAuto != now) {
            // Automatic change via updateAll()
            raceskillPoints.put(lastAuto, raceskillPoints.get(lastAuto) + 1);
            raceskillPoints.put(now, raceskillPoints.get(now) - 1);
        }
    }
    private SkillSingle createComboIfNeeded(Skill raceSkill, int idx, int row, int column, Color color) {
        if (raceSkill instanceof SkillSingle) {
            SkillSingle activeSkill = (SkillSingle) raceSkill;
            JTextField talentName = skillsPanel.createTextField(row, column, activeSkill.getName(), null, false);
            talentName.setForeground(color);

            visibleRaceSkills.add(activeSkill);
            return activeSkill;
        } else {
            SkillGroup skillGroup = (SkillGroup) raceSkill;
            SkillSingle activeSkill = (SkillSingle) skillGroup.getFirstSkill();
            SearchableComboBox skillNameCombo = skillsPanel.createSearchableComboBox(row, column, null, false);
            skillNameCombo.setToolTipText(MultiLineTooltip.splitToolTip(skillGroup.getName()));
            for (Skill alternateSkill : skillGroup.getSkills()) {
                skillNameCombo.addItem(alternateSkill.getName());
            }
            skillNameCombo.setPreferredSize(new Dimension(skillNameCombo.getSize().width, -1));
            skillNameCombo.refresh();
            skillNameCombo.setForeground(color);
            skillNameCombo.addActionListener(e -> updateSkillRow(idx, row, column, (SkillSingle) skillGroup.getSkills().get(skillNameCombo.getSelectedIndex())));

            visibleRaceSkills.add(activeSkill);
            return activeSkill;
        }
    }
    private void updateSkillRow(int idx, int row, int column, SkillSingle newSkill) {
        boolean colorChange = newSkill.isAdv() && newSkill.getAdvValue()==0;
        if (skillsPanel.getComponent(column, row) instanceof JTextField) {
            ((JTextField) skillsPanel.getComponent(column, row)).setText(newSkill.getName());
        } else {
            ((SearchableComboBox) skillsPanel.getComponent(column, row)).setSelectedItem(newSkill.getName());
        }
        skillsPanel.getComponent(column, row).setForeground(colorChange ? Color.RED : Color.BLACK);

        ((JTextField) skillsPanel.getComponent(column + 1, row)).setText(newSkill.getAttrName());
        ((AdvancedSpinner) skillsPanel.getComponent(column + 2, row)).setValue(newSkill.getAdvValue());
        ((JIntegerField) skillsPanel.getComponent(column + 3, row)).setValue(newSkill.getTotalValue());

        visibleRaceSkills.set(idx, newSkill);
    }
    private void updateSkillRow(int idx, int row, int column) {
        updateSkillRow(idx, row, column, visibleRaceSkills.get(idx));
    }

    private void createTalentTable(List<Talent> raceTalents) {
        final Dimension[] columns = new Dimension[]{
                new Dimension(200, -1),
                new Dimension(30, -1),
                new Dimension(30, -1),
                new Dimension(200, -1)
        };

        talentsPanel.createJLabel(0,0,1,-1, "Talents");
        for (int i = 0; i < raceTalents.size(); i++) {
            int finalI = i + 1;
            int column = 0;

            TalentSingle activeTalent;
            if (raceTalents.get(i) instanceof TalentSingle) {
                activeTalent = (TalentSingle) raceTalents.get(i);
                JTextField talentName = talentsPanel.createTextField(finalI, column++, activeTalent.getName(), columns[column-1], false);
            } else {
                TalentGroup talentGroup = (TalentGroup) raceTalents.get(i);
                activeTalent = (TalentSingle) talentGroup.getFirstTalent();
                SearchableComboBox talentNameCombo = talentsPanel.createSearchableComboBox(finalI, column++, columns[column-1], false);
                talentNameCombo.setToolTipText(MultiLineTooltip.splitToolTip(talentGroup.getName()));
                for (Talent alternateTalent : talentGroup.getChildTalents()) {
                    talentNameCombo.addItem(alternateTalent.getName());
                }
                talentNameCombo.setPreferredSize(new Dimension(talentNameCombo.getSize().width, -1));
                talentNameCombo.refresh();
                talentNameCombo.addActionListener(e -> {
                    TalentSingle newTalent = (TalentSingle) talentGroup.getChildTalents().get(talentNameCombo.getSelectedIndex());

                    ((JIntegerField) talentsPanel.getComponent(1, finalI)).setValue(newTalent.getCurrentLvl());
                    ((JTextField) talentsPanel.getComponent(2, finalI)).setText(newTalent.getMaxString());
                    ((JTextArea) talentsPanel.getComponent(3, finalI)).setText(newTalent.getBaseTalent().getTest());
                    ((JLabel) talentsPanel.getComponent(4, finalI)).setToolTipText(MultiLineTooltip.splitToolTip(newTalent.getBaseTalent().getDesc()));

                    visibleRaceTalents.set(finalI, newTalent);
                });
            }

            JIntegerField attrField = talentsPanel.createIntegerField(finalI, column++, activeTalent.getCurrentLvl(), columns[column-1]);
            attrField.setEditable(false);

            JTextField maxAttrField = talentsPanel.createTextField(finalI, column++, activeTalent.getMaxString(), columns[column-1], false);

            JTextArea testArea = talentsPanel.createTextArea(finalI, column++, activeTalent.getBaseTalent().getTest(), columns[column-1], false);
            testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

            String tooltip = activeTalent.getBaseTalent().getDesc();
            talentsPanel.createJLabel(finalI, column, new ImageIcon("src/resources/images/info.png"), MultiLineTooltip.splitToolTip(tooltip, 75, 10));

            visibleRaceTalents.add(activeTalent);
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
    private void updateTalentRow(TalentGroup talentGroup, List<Talent> finalTalents, GridPanel panel, int row, int index) {
        TalentSingle talent = (TalentSingle) talentGroup.getChildTalents().get(index);

        if (talentGroup.countChildTalents() != 1) {
            SearchableComboBox comboBox = (SearchableComboBox) panel.getComponent(0, row);
            comboBox.setSelectedItem(talent.getName());
            JTextArea textArea = (JTextArea) panel.getComponent(3, row);
            textArea.setText(talent.getBaseTalent().getTest());
            JLabel desc = (JLabel) panel.getComponent(4, row);
            desc.setToolTipText(MultiLineTooltip.splitToolTip(talent.getBaseTalent().getDesc()));

            finalTalents.set(row-1, talent);
        }
        JTextField testField = (JTextField) panel.getComponent(1, row);
//        testField.setText(String.valueOf(talent.getCurrentLvl()));
        JTextField testField2 = (JTextField) panel.getComponent(2, row);
//        testField2.setText(talent.getMaxString());
    }

    private void rollSkills() {
        List<SkillSingle> updatedSkills = CharacterGen.randomizeSkillsWithList(raceSkills, new int[] {3, 3, 3, 5, 5, 5});
        int baseItr = 1;
        int advItr = 1;
        int row;
        int column;
        for (int i=0;i<updatedSkills.size();i++) {
            if (updatedSkills.get(i).isAdv()) {
                column = 4;
                row = advItr++;
            } else {
                column = 0;
                row = baseItr++;
            }
            updateSkillRow(i, row, column, updatedSkills.get(i));
        }
    }
//    private void roll() {
//        if (raceskillRollButton.isEnabled()) {
//            Object[] result = CharacterGen.getRandomTalent(connection);
//            TalentGroup rollTalent = (TalentGroup) result[1];
//            int rollResultNumeric = (int) result[0];
//            while (randomTalentGroups.contains(rollTalent)) {
//                LogManager.getLogger(getClass().getName()).debug(String.format("%d doubled, changing to %d", rollResultNumeric, rollResultNumeric % 100 + 1));
//                rollResultNumeric = rollResultNumeric % 100 + 1;
//                rollTalent = connection.getRandomTalent(rollResultNumeric);
//            }
//            randomTalentGroups.add(rollTalent);
//            randomTalents.add(rollTalent.getFirstTalent());
//            raceskillRollResult.setValue(rollResultNumeric);
//
//            TalentGroup rollTalentGroup = rollTalent;
//            int row = randomTalentGroups.size() - 1;
//
//            SearchableComboBox activeField = (SearchableComboBox) randomTalentsPanel.getComponent(0, row);
//            for (Talent talent : rollTalentGroup.getTalents()) {
//                activeField.addItem(talent.getName());
//            }
//            activeField.refresh();
//            activeField.setLocked(activeField.getItemCount() == 1);
//            activeField.setEditable(!rollTalentGroup.isLocked());
//            String tooltip = rollTalentGroup.getName();
//            if (tooltip != null) {
//                activeField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
//            }
//            activeField.addActionListener(e -> updateTalentRow(rollTalentGroup, randomTalents, randomTalentsPanel, row, ((SearchableComboBox) e.getSource()).getSelectedIndex()));
//
//            JTextField activeField2 = (JTextField) randomTalentsPanel.getComponent(1, row);
//            activeField2.setText(String.valueOf(rollTalentGroup.getFirstTalent().getCurrentLvl()));
//
//            JTextField activeField3 = (JTextField) randomTalentsPanel.getComponent(2, row);
//            activeField3.setText(rollTalentGroup.getFirstTalent().getMaxString());
//
//            JTextArea activeArea = (JTextArea) randomTalentsPanel.getComponent(3, row);
//            activeArea.setText(rollTalentGroup.getFirstTalent().getBaseTalent().getTest());
//
//            JLabel activeLabel = (JLabel) randomTalentsPanel.getComponent(4, row);
//            activeLabel.setToolTipText(MultiLineTooltip.splitToolTip(rollTalentGroup.getFirstTalent().getBaseTalent().getDesc(), 75, 10));
//
//            if (sheet.getSubrace().getRandomTalents() == randomTalentGroups.size()) {
//                raceskillRollButton.setEnabled(false);
//                raceskillRollResult.setEditable(false);
//                raceskillOKButton.setEnabled(false);
//            }
//        }
//    }
    private void rollAll(List<Skill> skillGroups, List<TalentGroup> talentGroups) {
//        raceSkills = CharacterGen.randomizeSkillsWithList(raceSkills, new int[]{3, 3, 3, 5, 5, 5});
//        updateSkillTable(raceSkills);
//
//        for (int i = 0; i < talentGroups.size(); i++) {
//            updateTalentRow(talentGroups.get(i), raceTalents, talentsPanel, i + 1, talentGroups.get(i).getRndTalentIndex());
//        }
//        for (int i = 0; i < sheet.getSubrace().getRandomTalents(); i++) {
//            roll();
//        }
    }
}
