package main;

import com.intellij.uiDesigner.core.GridConstraints;
import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.GridPanel;
import components.JIntegerField;
import components.SearchableComboBox;
import components.SpinnerTypeListModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import mappings.Attribute;
import mappings.Race.Size;
import mappings.Race;
import mappings.Skill;
import mappings.SkillGroup;
import mappings.Talent;
import mappings.TalentGroup;
import org.apache.logging.log4j.LogManager;
import tools.AbstractActionHelper;
import tools.ColorPalette;
import tools.Dice;
import tools.MouseClickedAdapter;
import tools.MultiLineTooltip;

public class CharacterGen {
    public JPanel mainPanel;
    public final Main previousScreen;
    public JIntegerField expField;

    private final JFrame frame;
    private final Connection connection;
    private final CharacterSheet sheet;
    private JLabel imageLabel;
    private JTabbedPane tabbedPane;
    private JButton exitButton;

    private List<Attribute> attributes;
    private GridPanel attrAttributesTable;
    private JButton attrRollButton;
    private JButton attrOKButton;
    private JIntegerField attrSumField;
    private JButton attrRollAllButton;
    private JButton attrOption3Button;
    private JButton attrOption1Button;
    private JIntegerField attrMove;
    private JIntegerField attrHP;
    private int attrItr;
    private int attrMaxExp = 50;

    private JPanel fatePanel;
    private GridPanel fateAttributeTable;
    private JIntegerField fateAttrRemain;
    private JIntegerField fateFate;
    private JIntegerField fateResilience;
    private JButton fateFateUP;
    private JButton fateFateDOWN;
    private JIntegerField fateExtra;
    private JButton fateResilienceUP;
    private JButton fateResilienceDOWN;
    private JButton fateOption1Button;

    private JPanel raceskillPanel;
    private GridPanel raceskillSkillsPanel;
    private GridPanel raceskillTalentsPanel;
    private GridPanel raceskillRandomTalentsPanel;
    private JPanel raceskillRollPanel;
    private JLabel raceskillRandomTalentsLabel;
    private JButton raceskillRollButton;
    private JIntegerField raceskillRollResult;
    private JButton raceskillOKButton;
    private JButton raceskillOption1;
    private Map<Integer, Integer> raceskillPoints;

    private GridPanel profskillSkillsPanel;
    private GridPanel profskillTalentsPanel;
    private JButton profskillOption1;
    private RaceTab raceTab;
    private ProfTab profTab;

    private JIntegerField mouseSource;
    private Color mouseColor;
    private boolean attrLocked = true;

    private List<SkillGroup> raceSkillGroups = new ArrayList<>();
    private List<Skill> raceSkills = new ArrayList<>();
    private List<TalentGroup> raceTalentGroups = new ArrayList<>();
    private final List<Talent> raceTalents = new ArrayList<>();
    private final List<TalentGroup> randomTalentGroups = new ArrayList<>();
    private final List<Talent> randomTalents = new ArrayList<>();
    private List<SkillGroup> profSkills = new ArrayList<>();
    private final List<Skill> profSkillGroups = new ArrayList<>();
    private List<TalentGroup> profTalentGroups = new ArrayList<>();
    private final List<Talent> profTalents = new ArrayList<>();

    private final List<JIntegerField> BAttr = new ArrayList<>();
    private final List<JIntegerField> RAttr = new ArrayList<>();
    private final List<JIntegerField> TAttr = new ArrayList<>();

    public CharacterGen(JFrame frame, Main screen, Connection connection) {
        this.frame = frame;
        previousScreen = screen;
        this.connection = connection;
        sheet = new CharacterSheet();

        // Race //
        raceTab.initialise(this, sheet, this.connection);

        /* TODO: Make prof_option4Button works */
        /* FIXME: Optimise SearchableJComboBoxes */
        // prof_option4a.addActionListener(e -> prof_option4b.addItems(connection.getProfsNames(sheet.getRace().getID(), prof_option4a.getValue())));

        // Attributes //
        attrRollButton.addActionListener(e -> {
            Object[] result = getOneRandomAttr(attrItr, sheet.getRace());
            int rollAttr = (int) result[0];
            int sumAttr = (int) result[1];

            RAttr.get(attrItr).setValue(rollAttr);
            RAttr.get(attrItr).setEditable(false);
            TAttr.get(attrItr).setValue(sumAttr);
            attrSumField.changeValue(rollAttr);
            attrItr++;

            if (attrItr ==9) {
                calculateHP();
            }
            if (attrItr ==10) {
                calculateHP();
                attrLocked = false;
                attrRollButton.setEnabled(false);
                attrRollAllButton.setEnabled(false);
                attrOKButton.setEnabled(false);
                attrOption1Button.setEnabled(true);
            }
        });
        attrRollAllButton.addActionListener(e -> {
            Object[] result = getAllRandomAttr(sheet.getRace());
            Integer[] rollAttr = (Integer[]) result[0];
            Integer[] sumAttr = (Integer[]) result[1];
            attrSumField.setValue((int) result[2]);

            for (int i=0;i<10;i++) {
                RAttr.get(i).setValue(rollAttr[i]);
                RAttr.get(i).setEditable(false);
                TAttr.get(i).setValue(sumAttr[i]);
            }
            calculateHP();

            attrLocked = false;
            attrRollButton.setEnabled(false);
            attrRollAllButton.setEnabled(false);
            attrOKButton.setEnabled(false);
            attrOption1Button.setEnabled(true);
        });
        attrRollAllButton.setMnemonic(KeyEvent.VK_R);
        attrOKButton.addActionListener(e -> {
            //TODO Add custom values given by user
        });
        attrOption1Button.addActionListener(e -> {
            for (int i = 0; i < RAttr.size(); i++) {
                attributes.get(i).setRndValue(RAttr.get(i).getValue());
            }
            sheet.setAttributeList(attributes);
            sheet.setMove(attrMove.getValue());
            sheet.setMaxHP(attrHP.getValue());
            sheet.setHP();

            attrOption1Button.setEnabled(false);
            attrLocked = true;
            expField.changeValue(attrMaxExp);
            moveToNextTab();
        });
        attrOption1Button.setMnemonic(KeyEvent.VK_1);
        attrOption3Button.addActionListener(e -> {
            //TODO: Make a3PutOwnValuesButton work and probably change the name
        });

        // Fate & Resolve //
        /* TODO: Maybe change all buttons to JSpinners :thinking: */
        createActionMnemonic(fatePanel, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK), () -> {
            Integer[] slots = {0, 0, 0};
            int remain = 5;
            while (remain > 0) {
                int value = Dice.randomInt(0, remain);
                int active_slot = Dice.randomInt(0, 2);
                remain-=value;
                slots[active_slot]+=value;
            }

            int active_slot = 0;
            Integer[] attributes = sheet.getProfession().getSimpleAttributes();
            for (int i=0; i < attributes.length; i++) {
                if (attributes[i] != 0) {
                    attributes[i] = slots[active_slot];
                    active_slot++;
                }
            }

            fateAttributeTable.iterateThroughColumns(2, (o, i) -> {
                AdvancedSpinner spinner = (AdvancedSpinner) o;
                if (spinner.isEnabled()) {
                    spinner.setValue(attributes[i]);
                }
            });
        });
        fateFateUP.addActionListener(e -> {
            fateExtra.setValue(fateExtra.getValue() - 1);
            fateFate.setValue(fateFate.getValue() + 1);

            fateFateUP.setEnabled(Integer.parseInt(fateExtra.getText())!=0);
            fateFateDOWN.setEnabled(true);
            fateResilienceUP.setEnabled(Integer.parseInt(fateExtra.getText())!=0);
            fateResilienceDOWN.setEnabled(Integer.parseInt(fateResilience.getText())!=sheet.getRace().getResilience());

            fateOption1Button.setEnabled(fateExtra.getValue() == 0 && fateAttrRemain.getValue() == 0);
        });
        fateFateDOWN.addActionListener(e -> {
            fateExtra.setValue(fateExtra.getValue()+1);
            fateFate.setValue(fateFate.getValue()-1);

            fateFateUP.setEnabled(true);
            fateFateDOWN.setEnabled(Integer.parseInt(fateFate.getText())!=sheet.getRace().getFate());
            fateResilienceUP.setEnabled(true);
            fateResilienceDOWN.setEnabled(Integer.parseInt(fateResilience.getText())!=sheet.getRace().getResilience());

            fateOption1Button.setEnabled(fateExtra.getValue() == 0 && fateAttrRemain.getValue() == 0);
        });
        fateResilienceUP.addActionListener(e -> {
            fateExtra.setValue(fateExtra.getValue() - 1);
            fateResilience.setValue(fateResilience.getValue() + 1);

            fateFateUP.setEnabled(Integer.parseInt(fateExtra.getText())!=0);
            fateFateDOWN.setEnabled(Integer.parseInt(fateFate.getText())!=sheet.getRace().getFate());
            fateResilienceUP.setEnabled(Integer.parseInt(fateExtra.getText())!=0);
            fateResilienceDOWN.setEnabled(true);

            fateOption1Button.setEnabled(fateExtra.getValue() == 0 && fateAttrRemain.getValue() == 0);
        });
        fateResilienceDOWN.addActionListener(e -> {
            fateExtra.setValue(fateExtra.getValue() + 1);
            fateResilience.setValue(fateResilience.getValue() - 1);

            fateFateUP.setEnabled(true);
            fateFateDOWN.setEnabled(Integer.parseInt(fateFate.getText())!=sheet.getRace().getFate());
            fateResilienceUP.setEnabled(true);
            fateResilienceDOWN.setEnabled(Integer.parseInt(fateResilience.getText())!=sheet.getRace().getResilience());

            fateOption1Button.setEnabled(fateExtra.getValue() == 0 && fateAttrRemain.getValue() == 0);
        });
        fateOption1Button.addActionListener(e -> { //
            sheet.setAttributeList(attributes);

            moveToNextTab();
        });
        fateOption1Button.setMnemonic(KeyEvent.VK_1);

        // Race skills & Talents //
        createActionMnemonic(raceskillPanel, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK), () -> {
            raceSkills = randomizeSkillsWithList(raceSkillGroups, new int[]{3, 3, 3, 5, 5, 5});
            raceskillUpdateTable(raceSkills);

            for (int i = 0; i < raceTalentGroups.size(); i++) {
                updateTalentRow(raceTalentGroups.get(i), raceTalents, raceskillTalentsPanel, i + 1, raceTalentGroups.get(i).getRndTalentIndex());
            }
            for (int i = 0; i < sheet.getSubrace().getRandomTalents(); i++) {
                racetalentRoll();
            }
        });
        raceskillRollButton.addActionListener(e -> racetalentRoll());
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

            moveToNextTab();
        });

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
    }

    private void attrCreateTable() {
        attributes = sheet.getRace().getAttributes();

        attrMove = new JIntegerField(sheet.getRace().getM());
        attrMove.setHorizontalAlignment(JTextField.CENTER);
        attrMove.setEditable(false);
        attrMove.setFont(new Font(attrMove.getFont().getName(),Font.ITALIC+Font.BOLD, attrMove.getFont().getSize()+2));
        attrAttributesTable
                .add(attrMove, new GridConstraints(1, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null), false);

        for (int i = 0; i < attributes.size(); i++) {
            boolean changeBackground = (sheet.getRace().getSize()== Size.NORMAL && i == 2) || i == 3 || i == 8;
            Color foregroundColor = Color.black;
            if (sheet.getProfession().hasAttribute(i+1)) {
                foregroundColor = ColorPalette.HALF_GREEN;
            }

            JLabel charLabel = new JLabel();
            charLabel.setHorizontalAlignment(JLabel.CENTER);
            charLabel.setHorizontalTextPosition(0);
            charLabel.setText(attributes.get(i).getName());
            attrAttributesTable
                    .add(charLabel, new GridConstraints(0, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);

            JIntegerField baseAttr = new JIntegerField(attributes.get(i).getBaseValue());
            baseAttr.setForeground(foregroundColor);
            if (changeBackground) {
                baseAttr.setBackground(ColorPalette.WHITE_BLUE);
            }
            baseAttr.setHorizontalAlignment(JTextField.CENTER);
            baseAttr.setEditable(false);
            attrAttributesTable
                    .add(baseAttr, new GridConstraints(1, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);
            BAttr.add(baseAttr);

            JIntegerField attr = new JIntegerField();
            attr.setForeground(foregroundColor);
            if (changeBackground) {
                attr.setBackground(ColorPalette.WHITE_BLUE);
            }
            attr.setHorizontalAlignment(JTextField.CENTER);
            attr.addMouseListener((MouseClickedAdapter) this::attrReplaceValues);
            attrAttributesTable
                    .add(attr, new GridConstraints(2, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);
            RAttr.add(attr);

            JIntegerField sumAttr = new JIntegerField(attributes.get(i).getTotalValue());
            sumAttr.setForeground(foregroundColor);
            if (changeBackground) {
                sumAttr.setBackground(ColorPalette.WHITE_BLUE);
            }
            sumAttr.setHorizontalAlignment(JTextField.CENTER);
            sumAttr.setEditable(false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(),Font.ITALIC+Font.BOLD,sumAttr.getFont().getSize()+2));
            attrAttributesTable
                    .add(sumAttr, new GridConstraints(3, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);
            TAttr.add(sumAttr);
        }

        attrHP = new JIntegerField();
        attrHP.setHorizontalAlignment(JTextField.CENTER);
        attrHP.setEditable(false);
        attrHP.setFont(new Font(attrHP.getFont().getName(),Font.ITALIC+Font.BOLD, attrHP.getFont().getSize()+2));
        attrHP.setBackground(ColorPalette.WHITE_BLUE);
        attrAttributesTable.add(attrHP, new GridConstraints(1, attributes.size()+1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null), false);
        attrAttributesTable.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void attrReplaceValues(MouseEvent e) {
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
            calculateTotal();
        }
    }

    private void fateCreateTable() {
        List<JIntegerField> values = List.copyOf(TAttr);
        List<Component> tabOrder = new ArrayList<>();
        BAttr.clear();
        RAttr.clear();
        TAttr.clear();

        String[] columns = {"WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        for (int i = 0; i < columns.length; i++) {
            JLabel charLabel = new JLabel(columns[i], JLabel.CENTER);
            fateAttributeTable
                    .add(charLabel, new GridConstraints(0, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }

        attrHP = new JIntegerField(attrHP.getValue());
        attrHP.setHorizontalAlignment(JTextField.CENTER);
        attrHP.setEditable(false);
        fateAttributeTable.add(
                attrHP, new GridConstraints(1, columns.length-1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null), false);

        for (int i = 0; i < columns.length-1; i++) {
            int finalI = i;

            JIntegerField attr = new JIntegerField(values.get(i).getValue());
            attr.setHorizontalAlignment(JTextField.CENTER);
            attr.setEditable(false);
            BAttr.add(attr);
            fateAttributeTable.add(attr, new GridConstraints(1, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            AdvancedSpinner adv = new AdvancedSpinner(new SpinnerNumberModel(0, 0, 5, 1));
            adv.setEnabled(false);
            if (sheet.getProfession().hasAttribute(i+1)) {
                adv.setEnabled(true);
                tabOrder.add(adv.getTextField());
            }
            fateAttributeTable.add(adv,new GridConstraints(2, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            JIntegerField sumAttr = new JIntegerField(BAttr.get(i).getValue());
            sumAttr.setHorizontalAlignment(JTextField.CENTER);
            sumAttr.setEditable(false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(), Font.ITALIC + Font.BOLD, sumAttr.getFont().getSize() + 2));
            TAttr.add(sumAttr);
            fateAttributeTable.add(sumAttr, new GridConstraints(3, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            adv.addChangeListener(e -> fateUpdatePoints(adv, finalI, sumAttr));
        }
        fateAttributeTable.setFocusCycleRoot(true);
        fateAttributeTable.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        fateAttributeTable.build(GridPanel.ALIGNMENT_HORIZONTAL);

        fateFate.setValue(sheet.getRace().getFate());
        fateResilience.setValue(sheet.getRace().getResilience());
        fateExtra.setValue(sheet.getRace().getExtra());
    }
    private void fateUpdatePoints(AdvancedSpinner activeSpinner, int finalI, JIntegerField field) {
        int now = (int) (activeSpinner.getValue());
        int adv = attributes.get(finalI).getAdvValue();

        if (adv != now) {
            fateAttrRemain.changeValue(adv - now);
            field.changeValue(now - adv);
            attributes.get(finalI).setAdvValue(now);
            calculateHP();

            fateAttributeTable.iterateThroughColumns(2, (o, i) -> {
                AdvancedSpinner spinner = (AdvancedSpinner) o;
                if (activeSpinner != o) {
                    SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                    model.setMaximum((int) model.getValue() + fateAttrRemain.getValue());
                }
            });
        }

        fateOption1Button.setEnabled(fateExtra.getValue() == 0 && fateAttrRemain.getValue() == 0);
    }

    private void raceskillCreateTable() {
        raceSkillGroups = sheet.getRace().getRaceSkills(attributes);
        raceSkillGroups.forEach(e -> raceSkills.add(e.getFirstSkill()));

        profSkills = sheet.getProfession().getProfSkills(attributes, raceSkills);
        profSkills.forEach(e -> profSkillGroups.add(e.getFirstSkill()));

        List<Component> tabOrder = new ArrayList<>();
        int base_itr = 1, adv_itr = 1;
        int column, row;
        for (int i = 0; i < raceSkillGroups.size(); i++) {
            Color color = Color.black;

            if (raceSkills.get(i).isAdv()) {
                column = 4;
                row = adv_itr++;
                color = Color.red;
            } else {
                column = 0;
                row = base_itr++;
            }
            if (profSkillGroups.contains(raceSkills.get(i))) {
                color = ColorPalette.HALF_GREEN;
            }

            Container nameContainer = createSkillComboIfNeeded(raceSkillGroups.get(i), raceskillSkillsPanel, row, column);
            nameContainer.setForeground(color);
            column++;

            JTextField attrField = new JTextField(raceSkills.get(i).getAttr().getName());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            raceskillSkillsPanel
                    .add(attrField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            AdvancedSpinner jSpinner = new AdvancedSpinner(new SpinnerTypeListModel<>(new Integer[]{0, 3, 5}));
            jSpinner.setHorizontalAlignment(JTextField.CENTER);
            raceskillSkillsPanel
                    .add(jSpinner, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            JIntegerField sumField = new JIntegerField(raceSkills.get(i).getTotalValue(), "%d");
            sumField.setHorizontalAlignment(JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            raceskillSkillsPanel
                    .add(sumField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            int finalI = i;
            jSpinner.addChangeListener(e -> raceskillUpdatePoints(nameContainer, jSpinner, sumField, raceSkills.get(finalI), profSkillGroups.contains(raceSkills.get(finalI))));
            tabOrder.add(jSpinner.getTextField());
        }
        if (base_itr != 1) {
            raceskillSkillsPanel
                    .add(new JLabel("Basic skills", JLabel.CENTER), new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }
        if (adv_itr != 1) {
            raceskillSkillsPanel
                    .add(new JLabel("Advanced skills", JLabel.CENTER), new GridConstraints(0, 4, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }

        raceskillSkillsPanel.setFocusCycleRoot(true);
        raceskillSkillsPanel.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        raceskillSkillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

        raceskillUpdateTable(raceSkills);
    }
    private void raceskillUpdateTable(List<Skill> raceSkills) {
        int base_itr = 1, adv_itr = 1;
        int column, row;
        for (Skill skill : raceSkills) {
            Color color = Color.black;
            if (skill.isAdv()) {
                column = 4;
                row = adv_itr++;
                color = Color.red;
            } else {
                column = 0;
                row = base_itr++;
            }

            if (profSkillGroups.contains(skill)) {
                color = ColorPalette.HALF_GREEN;
            }

            if (raceskillSkillsPanel.getComponent(column, row) instanceof SearchableComboBox) {
                ((SearchableComboBox) raceskillSkillsPanel.getComponent(column, row)).getModel().setSelectedItem(skill.getName());
            }
            ((AdvancedSpinner) raceskillSkillsPanel.getComponent(column + 2, row)).setValue(skill.getAdvValue());
        }
    }
    private void raceskillUpdatePoints(Container container, AdvancedSpinner spinner, JIntegerField totalField, Skill skill, boolean paintGreen) {
        int now = (int) spinner.getValue();
        int last = skill.getAdvValue();

        if (skill.getAdvValue() != now) {
            skill.setAdvValue(now);
            totalField.setValue(skill.getTotalValue());

            raceskillPoints.put(last, raceskillPoints.get(last)-1);
            raceskillPoints.put(now, raceskillPoints.get(now)+1);
        } else if (spinner.getValue() != spinner.getLastValue()) {
            totalField.setValue(skill.getTotalValue());

            raceskillPoints.put((Integer) spinner.getLastValue(), raceskillPoints.get(last)-1);
            raceskillPoints.put((Integer) spinner.getValue(), raceskillPoints.get(now)+1);
        }

        if (now == 0 && skill.getBaseSkill().isAdv()) {
            container.setForeground(Color.red);
        } else {
            container.setForeground(Color.black);
        }

        if (paintGreen) {
            container.setForeground(ColorPalette.HALF_GREEN);
        }

//        raceskill_skillsPanel.iterateThroughRows(2, 1, baseSkills.size(), this::raceskill_changeColor);
//        raceskill_skillsPanel.iterateThroughRows(6, 1, advSkills.size(), this::raceskill_changeColor);
    }
    private void raceskillChangeColor(Object object) {
        AdvancedSpinner active = (AdvancedSpinner) object;
        if (raceskillPoints.get((int) active.getValue()) > 3) {
            active.getTextField().setForeground(Color.RED);
        } else {
            active.getTextField().setForeground(Color.BLACK);
        }
    }

    private void racetalentCreateTable() {
        raceTalentGroups = sheet.getRace().getRaceTalents(attributes);
        raceTalentGroups.forEach(e -> raceTalents.add(e.getFirstTalent()));

        profTalentGroups = sheet.getProfession().getProfTalents(attributes);
        profTalentGroups.forEach(e -> profTalents.add(e.getFirstTalent()));

        // Constants
        final Dimension[] columnDimensions = new Dimension[]{
                new Dimension(200, -1),
                new Dimension(30, -1),
                new Dimension(30, -1),
                new Dimension(200, -1)
        };

        // Talents - Header
        raceskillTalentsPanel
                .add(new JLabel("Talents", JLabel.CENTER), new GridConstraints(0, 0, 1, -1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        for (int i = 0; i < raceTalentGroups.size(); i++) {
            int column = 0;

            Container nameContainer = createTalentComboIfNeeded(raceTalentGroups.get(i), raceskillTalentsPanel, i + 1, column, columnDimensions[column]);
            column++;

            JTextField attrField = new JTextField(String.valueOf(raceTalents.get(i).getCurrentLvl()));
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            raceskillTalentsPanel
                    .add(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            JTextField attrField2 = new JTextField(raceTalents.get(i).getMaxString());
            attrField2.setHorizontalAlignment(JTextField.CENTER);
            attrField2.setEditable(false);
            attrField2.setFocusable(false);
            raceskillTalentsPanel.add(attrField2, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            JTextArea testArea = new JTextArea(raceTalents.get(i).getBaseTalent().getTest());
            testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));
            testArea.setLineWrap(true);
            testArea.setWrapStyleWord(true);
            testArea.setEditable(false);
            raceskillTalentsPanel
                    .add(testArea, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            String tooltip = raceTalents.get(i).getBaseTalent().getDesc();
            JLabel desc = new JLabel(new ImageIcon("src/resources/images/info.png"));
            desc.setToolTipText(MultiLineTooltip.splitToolTip(tooltip, 75, 10));
            raceskillTalentsPanel
                    .add(desc, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null), false);
        }

        raceskillTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

        // Talents - Random Talents
        if (sheet.getSubrace().getRandomTalents() != 0) {
            raceskillRandomTalentsLabel.setVisible(true);
            raceskillRollPanel.setVisible(true);
            for (int i=0;i<sheet.getSubrace().getRandomTalents();i++) {
                int column = 0;

                SearchableComboBox nameField = new SearchableComboBox();
                nameField.setLocked(true);
                raceskillRandomTalentsPanel.add(nameField, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JTextField attrField = new JTextField();
                attrField.setHorizontalAlignment(JTextField.CENTER);
                attrField.setEditable(false);
                attrField.setFocusable(false);
                raceskillRandomTalentsPanel.add(attrField, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JTextField attrField2 = new JTextField();
                attrField2.setHorizontalAlignment(JTextField.CENTER);
                attrField2.setEditable(false);
                attrField2.setFocusable(false);
                raceskillRandomTalentsPanel.add(attrField2, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JTextArea testArea = new JTextArea();
                testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));
                testArea.setLineWrap(true);
                testArea.setWrapStyleWord(true);
                testArea.setEditable(false);
                raceskillRandomTalentsPanel.add(testArea, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JLabel desc = new JLabel(new ImageIcon("src/resources/images/info.png"));
                raceskillRandomTalentsPanel
                        .add(desc, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null), false);
            }
        }

        raceskillRandomTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void racetalentRoll() {
        if (raceskillRollButton.isEnabled()) {
            TalentGroup rollTalent;
            do {
                Object[] result = getRandomTalent();
                int rollResultNumeric = (int) result[0];
                rollTalent = (TalentGroup) result[1];

                raceskillRollResult.setValue(rollResultNumeric);
            } while (randomTalentGroups.contains(rollTalent));
            TalentGroup rollTalentGroup = rollTalent;
            randomTalentGroups.add(rollTalentGroup);
            randomTalents.add(rollTalentGroup.getFirstTalent());

            int row = randomTalentGroups.size() - 1;

            SearchableComboBox activeField = (SearchableComboBox) raceskillRandomTalentsPanel.getComponent(0, row);
            for (Talent talent : rollTalentGroup.getTalents()) {
                activeField.addItem(talent.getName());
            }
            activeField.refresh();
            activeField.setLocked(activeField.getItemCount() == 1);
            activeField.setEditable(!rollTalentGroup.isLocked());
            String tooltip = rollTalentGroup.getName();
            if (tooltip != null)
                activeField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            activeField.addActionListener(e -> updateTalentRow(rollTalentGroup, randomTalents, raceskillRandomTalentsPanel, row, ((SearchableComboBox) e.getSource()).getSelectedIndex()));

            JTextField activeField2 = (JTextField) raceskillRandomTalentsPanel.getComponent(1, row);
            activeField2.setText(String.valueOf(rollTalentGroup.getFirstTalent().getCurrentLvl()));

            JTextField activeField3 = (JTextField) raceskillRandomTalentsPanel.getComponent(2, row);
            activeField3.setText(rollTalentGroup.getFirstTalent().getMaxString());

            JTextArea activeArea = (JTextArea) raceskillRandomTalentsPanel.getComponent(3, row);
            activeArea.setText(rollTalentGroup.getFirstTalent().getBaseTalent().getTest());

            JLabel activeLabel = (JLabel) raceskillRandomTalentsPanel.getComponent(4, row);
            activeLabel.setToolTipText(MultiLineTooltip.splitToolTip(rollTalentGroup.getFirstTalent().getBaseTalent().getDesc(), 75, 10));

            if (sheet.getSubrace().getRandomTalents() <= randomTalentGroups.size()) {
                raceskillRollButton.setEnabled(false);
                raceskillRollResult.setEditable(false);
                raceskillOKButton.setEnabled(false);
            }
        }
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
                    .add(attrField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            AdvancedSpinner jSpinner = new AdvancedSpinner(new SpinnerNumberModel(profSkillGroups.get(i).getAdvValue(), profSkillGroups.get(i).getAdvValue(), 10, 1));
            jSpinner.setHorizontalAlignment(JTextField.CENTER);
            profskillSkillsPanel
                    .add(jSpinner, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            JIntegerField sumField = new JIntegerField(profSkillGroups.get(i).getTotalValue(), "%d");
            sumField.setHorizontalAlignment(JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            profskillSkillsPanel
                    .add(sumField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            jSpinner.addChangeListener(e -> profskillUpdatePoints(nameContainer, jSpinner, sumField, profSkillGroups.get(finalI)));
            tabOrder.add(jSpinner.getTextField());
        }

        if (base_itr != 1) {
            profskillSkillsPanel
                    .add(new JLabel("Basic skills", JLabel.CENTER), new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }
        if (adv_itr != 1) {
            profskillSkillsPanel
                    .add(new JLabel("Advanced skills", JLabel.CENTER), new GridConstraints(0, 4, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
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
                .add(new JLabel("Talents", JLabel.CENTER), new GridConstraints(0, 0, 1, -1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        for (int i = 0; i < profTalentGroups.size(); i++) {
            int column = 0;

            Container nameContainer = createTalentComboIfNeeded(profTalentGroups.get(i), profskillTalentsPanel, i + 1, column, columnDimensions[column]);
            column++;

            JTextField attrField = new JTextField(profTalents.get(i).getMaxString());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            profskillTalentsPanel
                    .add(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            JTextArea testArea = new JTextArea(profTalents.get(i).getBaseTalent().getTest());
            testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));
            testArea.setLineWrap(true);
            testArea.setWrapStyleWord(true);
            testArea.setEditable(false);
            profskillTalentsPanel
                    .add(testArea, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            String tooltip = profTalents.get(i).getBaseTalent().getDesc();
            JLabel desc = new JLabel(new ImageIcon("src/resources/images/info.png"));
            desc.setToolTipText(MultiLineTooltip.splitToolTip(tooltip, 75, 10));
            profskillTalentsPanel
                    .add(desc, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null), false);

            JRadioButton radioButton = new JRadioButton();
            buttonGroup.add(radioButton);
            profskillTalentsPanel.add(radioButton, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null), false);
        }
        profskillTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }

    private void calculateHP() {
        int value = (TAttr.get(3).getValue() / 10) * 2 + TAttr.get(8).getValue() / 10;
        if (sheet.getRace().getSize() == Race.Size.NORMAL)
            value += TAttr.get(2).getValue() / 10;
        attrHP.setValue(value);
    }
    private void calculateTotal() {
        for (int i=0;i<10;i++)
            TAttr.get(i).setValue(BAttr.get(i).getValue() + RAttr.get(i).getValue());
        calculateHP();
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
                attrCreateTable();
                break;
            case 3:
                fateCreateTable();
                break;
            case 4:
                raceskillCreateTable();
                racetalentCreateTable();
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
        expField.setRunnable((o, i) -> sheet.setExp(((JIntegerField) o).getValue()));

        fateAttrRemain = new JIntegerField(5);
        raceskillPoints = new HashMap<>();
        raceskillPoints.put(0, 0);
        raceskillPoints.put(3, 0);
        raceskillPoints.put(5, 0);
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

            panel.add(textField, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
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
            panel.add(comboBox, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
            return comboBox;
        }
    }
    private Container createTalentComboIfNeeded(TalentGroup groupTalent, GridPanel panel, int row, int column, Dimension columnDimensions) {
        if (groupTalent.countTalents() == 1) {
            Talent singleTalent = groupTalent.getFirstTalent();

            JTextField textField = new JTextField(singleTalent.getName());
            textField.setFocusable(false);
            textField.setEditable(false);
            panel.add(textField, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions, null), false);
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
            comboBox.addActionListener(e -> updateTalentRow(groupTalent, raceTalents, raceskillTalentsPanel, row, ((SearchableComboBox) e.getSource()).getSelectedIndex()));
            panel.add(comboBox, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions, null), false);
            return comboBox;
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

    private void createActionMnemonic(JPanel panel, KeyStroke keyStroke, Runnable runnable) {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, keyStroke.toString());
        panel.getActionMap().put(keyStroke.toString(), AbstractActionHelper.getAction(runnable));
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
    public Object[] getOneRandomAttr(int index, Race race) {
        Object[] returns = new Object[2];
        int raceAttr = race.getRaceAttribute(index+1).getValue();
        int rollAttr = Dice.randomDice(2, 10);
        int sumAttr = raceAttr + rollAttr;

        returns[0] = rollAttr;
        returns[1] = sumAttr;
        return returns;
    }
    public Object[] getAllRandomAttr(Race race) {
        Integer[] rollAttr = new Integer[10];
        Integer[] sumAttr = new Integer[10];
        Object[] returns = new Object[3];
        int sumRollAttr = 0;
        for (int i=0;i<10;i++) {
            Object[] attr = getOneRandomAttr(i, race);
            rollAttr[i] = (Integer) attr[0];
            sumAttr[i] = (Integer) attr[1];
            sumRollAttr += (int) attr[0];
        }
        returns[0] = rollAttr;
        returns[1] = sumAttr;
        returns[2] = sumRollAttr;
        return returns;
    }

    public List<Skill> randomizeSkillsWithList(List<SkillGroup> skills, int[] values) {
        int range = skills.size();
        List<Integer> usedIndexes = new ArrayList<>();
        List<Skill> finalSkillList = new ArrayList<>();

        // Cleaning existing advancements
        for (SkillGroup skillGroup : skills) {
            skillGroup.cleanSkills();
            finalSkillList.add(skillGroup.getFirstSkill());
        }

        // Adding new random advancements
        for (int value : values) {
            int index = Dice.randomInt(0, range - 1);
            while (usedIndexes.contains(index)) {
                index = (index + 1) % (range - 1);
            }
            Skill chosenSkill = skills.get(index).getRndSkill();
            chosenSkill.setAdvValue(value);
            finalSkillList.set(index, chosenSkill);
            usedIndexes.add(index);
        }
        return finalSkillList;
    }
    public List<Talent> randomizeTalents(List<TalentGroup> talents) {
        List<Talent> returnList = new ArrayList<>();
        for (TalentGroup talent : talents) {
            returnList.add(talent.getRndTalent());
        }
        return returnList;
    }
    public Object[] getRandomTalent() {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getRandomTalent(numeric);
        return returns;
    }
}