package main;

import com.intellij.uiDesigner.core.GridConstraints;
import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.GridPanel;
import components.JIntegerField;
import components.SearchableComboBox;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import mappings.Attribute;
import mappings.Profession;
import mappings.Race;
import mappings.Skill;
import mappings.SkillGroup;
import mappings.Talent;
import mappings.TalentGroup;
import org.apache.logging.log4j.LogManager;
import tabs.AttributesTab;
import tabs.FateTab;
import tabs.ProfTab;
import tabs.RaceSkillTab;
import tabs.RaceTab;
import tools.Dice;
import tools.MultiLineTooltip;

public class CharacterGen {
    public JPanel mainPanel;
    public final Main previousScreen;

    private final JFrame frame;
    private final Connection connection;
    private final CharacterSheet sheet;
    private JLabel imageLabel;
    private JTabbedPane tabbedPane;
    private JIntegerField expField;
    private JButton exitButton;

    private GridPanel profskillSkillsPanel;
    private GridPanel profskillTalentsPanel;
    private JButton profskillOption1;
    private RaceTab raceTab;
    private ProfTab profTab;
    private AttributesTab attributesTab;
    private FateTab fateTab;
    private RaceSkillTab raceSkillTab;

    private final List<Talent> raceTalents = new ArrayList<>();
    private final List<SkillGroup> profSkills = new ArrayList<>();
    private final List<Skill> profSkillGroups = new ArrayList<>();
    private final List<TalentGroup> profTalentGroups = new ArrayList<>();
    private final List<Talent> profTalents = new ArrayList<>();

    public CharacterGen(JFrame frame, Main screen, Connection connection) {
        this.frame = frame;
        previousScreen = screen;
        this.connection = connection;
        sheet = new CharacterSheet();
        sheet.addObserver("exp", expField);

        // Race //
        raceTab.initialise(this, sheet, this.connection);

        // Pane controls //
        tabbedPane.addChangeListener(e -> {
            int tab = tabbedPane.getSelectedIndex();
            String iconPath = String.format("src/resources/images/round%d.png", tab + 1);
            ImageIcon icon = new ImageIcon(iconPath);
            imageLabel.setIcon(icon);
        });
        exitButton.addActionListener(e -> {
            this.frame.setContentPane(previousScreen.mainPanel);
            this.frame.validate();
        });
        exitButton.setMnemonic(KeyEvent.VK_E);
    }

    private void profskillCreateTable() {
        List<Component> tabOrder = new ArrayList<>();

        int base_itr = 1;
        int adv_itr = 1;
        int column, row;
        for (int i = 0; i < profSkills.size(); i++) {
            Color color = Color.black;
            int finalI = i;

            if (profSkillGroups.get(i).isAdv()) {
                column = 4;
                row = adv_itr++;
                color = Color.red;
            } else {
                column = 0;
                row = base_itr++;
            }

            Container nameContainer = createSkillComboIfNeeded(profSkills.get(i), profskillSkillsPanel, row, column);
            nameContainer.setForeground(color);
            column++;

            JTextField attrField = new JTextField(profSkillGroups.get(i).getAttr().getName());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            profskillSkillsPanel
                    .add(attrField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null));

            AdvancedSpinner jSpinner = new AdvancedSpinner(new SpinnerNumberModel(profSkillGroups.get(i).getAdvValue(), profSkillGroups.get(i).getAdvValue(), 10, 1));
            jSpinner.setHorizontalAlignment(JTextField.CENTER);
            profskillSkillsPanel
                    .add(jSpinner, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null));

            JIntegerField sumField = new JIntegerField(profSkillGroups.get(i).getTotalValue(), "%d");
            sumField.setHorizontalAlignment(JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            profskillSkillsPanel
                    .add(sumField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null));

            jSpinner.addChangeListener(e -> profskillUpdatePoints(nameContainer, jSpinner, sumField, profSkillGroups.get(finalI)));
            tabOrder.add(jSpinner.getTextField());
        }

        if (base_itr != 1) {
            profskillSkillsPanel
                    .add(new JLabel("Basic skills", JLabel.CENTER), new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        }
        if (adv_itr != 1) {
            profskillSkillsPanel
                    .add(new JLabel("Advanced skills", JLabel.CENTER), new GridConstraints(0, 4, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        }
        profskillSkillsPanel.setFocusCycleRoot(true);
        profskillSkillsPanel.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        profskillSkillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void profskillUpdatePoints(Container container, AdvancedSpinner spinner, JIntegerField totalField, Skill skill) {
        int now = (int) spinner.getValue();

        if (skill.getAdvValue() != now) {
            skill.setAdvValue(now);
            totalField.setValue(skill.getTotalValue());
        }

        if (now == 0 && skill.getBaseSkill().isAdv()) {
            container.setForeground(Color.red);
        } else {
            container.setForeground(Color.black);
        }
    }

    private void proftalentCreateTable() {
        ButtonGroup buttonGroup = new ButtonGroup();

        // Constants
        final Dimension[] columnDimensions = new Dimension[]{
                new Dimension(200, -1),
                new Dimension(30, -1),
                new Dimension(200, -1)
        };

        // Talents - Header
        profskillTalentsPanel
                .add(new JLabel("Talents", JLabel.CENTER), new GridConstraints(0, 0, 1, -1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        for (int i = 0; i < profTalentGroups.size(); i++) {
            int column = 0;

            Container nameContainer = createTalentComboIfNeeded(profTalentGroups.get(i), profskillTalentsPanel, i + 1, column, columnDimensions[column]);
            column++;

            JTextField attrField = new JTextField(profTalents.get(i).getMaxString());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            profskillTalentsPanel
                    .add(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null));

            JTextArea testArea = new JTextArea(profTalents.get(i).getBaseTalent().getTest());
            testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));
            testArea.setLineWrap(true);
            testArea.setWrapStyleWord(true);
            testArea.setEditable(false);
            profskillTalentsPanel
                    .add(testArea, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null));

            String tooltip = profTalents.get(i).getBaseTalent().getDesc();
            JLabel desc = new JLabel(new ImageIcon("src/resources/images/info.png"));
            desc.setToolTipText(MultiLineTooltip.splitToolTip(tooltip, 75, 10));
            profskillTalentsPanel
                    .add(desc, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));

            JRadioButton radioButton = new JRadioButton();
            buttonGroup.add(radioButton);
            profskillTalentsPanel.add(radioButton, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
        }
        profskillTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }

    public void moveToNextTab() {
        int tab = tabbedPane.getSelectedIndex();
        tabbedPane.setEnabledAt(tab + 1, true);
        tabbedPane.setSelectedIndex(tab + 1);
        LogManager.getLogger(getClass().getName()).info(String.format("Loaded tab %d", tab + 1));
        switch (tab + 1) {
            case 1:
                profTab.initialise(this, sheet, connection);
                break;
            case 2:
                attributesTab.initialise(this, sheet, connection);
                break;
            case 3:
                fateTab.initialise(this, sheet, connection);
                break;
            case 4:
                raceSkillTab.initialise(this, sheet, connection);
                break;
            case 5:
                profskillCreateTable();
                proftalentCreateTable();
                break;
            default:
                break;
        }
    }
    private void createUIComponents() {
        expField = new JIntegerField(0);
    }

    private Container createSkillComboIfNeeded(SkillGroup groupSkill, GridPanel panel, int row, int column) {
        if (groupSkill.countSkills() == 1) {
            Skill singleSkill = groupSkill.getFirstSkill();

            JTextField textField = new JTextField(singleSkill.getName());
            String tooltip = singleSkill.getBaseSkill().getDesc();
            if (tooltip != null)
                textField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            if (groupSkill.getFirstSkill().isEarning())
                textField.setFont(textField.getFont().deriveFont(Font.BOLD));
            textField.setFocusable(false);
            textField.setEditable(false);

            panel.add(textField, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
            return textField;
        } else {
            SearchableComboBox comboBox = new SearchableComboBox();
            String tooltip = groupSkill.getFirstSkill().getBaseSkill().getDesc();
            if (tooltip != null)
                comboBox.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            for (Skill alternateSkill : groupSkill.getSkills())
                comboBox.addItem(alternateSkill.getName());
            comboBox.setPreferredSize(new Dimension(comboBox.getSize().width, -1));
            comboBox.refresh();
            comboBox.setEditable(!groupSkill.isLocked());
            panel.add(comboBox, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
            return comboBox;
        }
    }
    private Container createTalentComboIfNeeded(TalentGroup groupTalent, GridPanel panel, int row, int column, Dimension columnDimensions) {
        if (groupTalent.countTalents() == 1) {
            Talent singleTalent = groupTalent.getFirstTalent();

            JTextField textField = new JTextField(singleTalent.getName());
            textField.setFocusable(false);
            textField.setEditable(false);
            panel.add(textField, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions, null));
            return textField;
        } else {
            SearchableComboBox comboBox = new SearchableComboBox();
            String tooltip = groupTalent.getName();
            if (tooltip != null)
                comboBox.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            for (Talent alternateTalent : groupTalent.getTalents())
                comboBox.addItem(alternateTalent.getName());
            comboBox.setPreferredSize(new Dimension(comboBox.getSize().width, -1));
            comboBox.refresh();
            comboBox.setEditable(false);
            comboBox.addActionListener(e -> updateTalentRow(groupTalent, raceTalents, profskillTalentsPanel, row, e));
            panel.add(comboBox, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions, null));
            return comboBox;
        }
    }
    private void updateTalentRow(TalentGroup talentGroup, List<Talent> finalTalents, GridPanel panel, int row, ActionEvent e) {
        int index = ((SearchableComboBox) e.getSource()).getSelectedIndex();
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

    // Base functions to use with GUI and text //
    public static Object[] getRandomRace(Connection connection) {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getRaceFromTable(numeric);
        return returns;
    }
    public static Object[] getRandomProf(Connection connection, Race race) {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getProfFromTable(race, numeric);
        return returns;
    }
    public static Attribute getOneRandomAttr(int index, Race race) {
        int numeric = Dice.randomDice(2, 10);
        Attribute attribute = race.getAttribute(index);
        attribute.setRndValue(numeric);
        return attribute;
    }
    public static Map<Integer, Attribute> getAllRandomAttr(Race race) {
        Map<Integer, Attribute> attributeMap = race.getAttributes();
        for (int i = 0; i < attributeMap.size(); i++) {
            int numeric = Dice.randomDice(2, 10);
            attributeMap.get(i).setRndValue(numeric);
        }
        return attributeMap;
    }
    public static Map<Integer, Attribute> randomAttributeAdvances(Profession profession, Map<Integer, Attribute> startAttributes, int maxPoints) {
        Map<Integer, Attribute> attributes = new ConcurrentHashMap<>(startAttributes);
        List<Attribute> profAttributes = profession.getAttributesList(attributes);

        for (Attribute attribute : attributes.values()) {
            attribute.setAdvValue(0);
        }

        for (int i = 0; i < maxPoints; i++) {
            int activeSlot = Dice.randomInt(0, profAttributes.size() - 1);
            Attribute active = profAttributes.get(activeSlot);
            if (active.incAdvValue() == maxPoints) {
                profAttributes.remove(activeSlot);
            }
        }
        return attributes;
    }
    public static List<Skill> randomizeSkillsWithList(List<SkillGroup> startSkills, int[] values) {
        List<Skill> skills = new ArrayList<>();
        List<Skill> lookupList = new ArrayList<>();

        for (SkillGroup skillGroup : startSkills) {
            Skill skill = new Skill(skillGroup.getRndSkill());
            skills.add(skill);
            lookupList.add(skill);
        }

        for (int value : values) {
            int index = Dice.randomInt(0, lookupList.size()-1);
            Skill chosenSkill = lookupList.get(index);
            chosenSkill.setAdvValue(value);
            lookupList.remove(chosenSkill);
        }

        return skills;
    }
    public List<Talent> randomizeTalents(List<TalentGroup> talents) {
        List<Talent> returnList = new ArrayList<>();
        for (TalentGroup talent : talents) {
            returnList.add(talent.getRndTalent());
        }
        return returnList;
    }
    public static Object[] getRandomTalent(Connection connection) {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        TalentGroup talentGroup = connection.getRandomTalent(numeric);
        for (Talent talent : talentGroup.getTalents()) {
            talent.setCurrentLvl(1);
        }

        returns[0] = numeric;
        returns[1] = talentGroup;
        return returns;
    }
}