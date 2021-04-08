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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import mappings.Attribute;
import mappings.Profession;
import mappings.Race.Size;
import mappings.Race;
import mappings.Skill;
import mappings.SkillGroup;
import mappings.Talent;
import mappings.TalentGroup;
import tools.AbstractActionBuilder;
import tools.ColorPalette;
import tools.Dice;
import tools.MouseClickedAdapter;
import tools.MultiLineTooltip;

public class CharacterGen {
    public JPanel mainPanel;
    public final Main previous_screen;

    private final JFrame frame;
    private final Connection connection;
    private final CharacterSheet sheet;
    private JLabel imageLabel;
    private JIntegerField expField;
    private JTabbedPane tabbedPane;
    private JButton exitButton;
    private JLabel rollLabel;

    private JPanel race_rollPanel;
    private JPanel race_options;
    private JIntegerField race_rollResult;
    private JButton race_option2Button;
    private SearchableComboBox race_option2Combo;
    private JButton race_option1Button;
    private JTextField race_option1;
    private JButton race_rollButton;
    private JButton race_OKButton;

    private JButton prof_OKButton;
    private JIntegerField prof_rollResult;
    private JButton prof_rollButton;
    private JTextField prof_option1a;
    private JTextField prof_option1b;
    private JButton prof_option1Button;
    private JTextField prof_option2a;
    private JTextField prof_option2b;
    private JButton prof_option2Button;
    private JTextField prof_option3a;
    private JTextField prof_option3b;
    private JButton prof_option3Button;
    private SearchableComboBox prof_option4a;
    private SearchableComboBox prof_option4b;
    private JButton prof_option4Button;
    private final JTextField[][] prof_options = {
            {prof_option1a, prof_option1b},
            {prof_option2a, prof_option2b},
            {prof_option3a, prof_option3b}
    };
    private final JButton[] prof_buttons = {
            prof_option1Button, prof_option2Button, prof_option3Button, prof_option4Button
    };
    private int prof_maxExp = 50;

    private List<Attribute> attributes;
    private GridPanel attributesTable;
    private JButton attr_rollButton;
    private JButton attr_OKButton;
    private JIntegerField attr_sumField;
    private JButton attr_rollAllButton;
    private JButton a3PutOwnValuesButton;
    private JButton attr_option1Button;
    private JIntegerField attr_move;
    private JIntegerField attr_hp;
    private int attr_itr=0;
    private int attr_maxExp = 50;

    private JPanel fate_panel;
    private GridPanel fate_attributeTable;
    private JIntegerField fate_attrRemain;
    private JIntegerField fate_fate;
    private JIntegerField fate_resilience;
    private JButton fate_fateUP;
    private JButton fate_fateDOWN;
    private JIntegerField fate_extra;
    private JButton fate_resilienceUP;
    private JButton fate_resilienceDOWN;
    private JButton fate_option1Button;

    private JPanel raceskill_panel;
    private GridPanel raceskill_skillsPanel;
    private GridPanel raceskill_talentsPanel;
    private GridPanel raceskill_randomTalentsPanel;
    private JPanel raceskill_rollPanel;
    private JLabel raceskill_randomTalentsLabel;
    private JButton raceskill_rollButton;
    private JIntegerField raceskill_rollResult;
    private JButton raceskill_OKButton;
    private JButton raceskill_option1;
    private Map<Integer, Integer> raceskill_points;

    private GridPanel profskill_skillsPanel;
    private GridPanel profskill_talentsPanel;
    private JButton profskill_option1;

    private JIntegerField mouse_source = null;
    private Color mouse_color;
    private boolean attr_locked = true;

    private Race rollRace;
    private final List<Profession> profList = new ArrayList<>();
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

    public CharacterGen(JFrame _frame, Main _screen, Connection _connection) {
        frame = _frame;
        previous_screen = _screen;
        connection = _connection;
        sheet = new CharacterSheet();

        // Race //
        race_option2Combo.addItems(connection.getRacesNames(), false);

        race_rollButton.addActionListener(e -> {
            Object[] result = getRandomRace();
            int rollResultNumeric = (int) result[0];
            rollRace = (Race) result[1];

            race_rollResult.setValue(rollResultNumeric);
            race_option1.setText(rollRace.getName());

            race_rollButton.setEnabled(false);
            race_rollResult.setEditable(false);
            race_OKButton.setEnabled(false);

            race_option1.setEnabled(true);
            race_option1Button.setEnabled(true);
            race_option2Combo.setEnabled(true);
            race_option2Button.setEnabled(true);
        });
        race_rollButton.setMnemonic(KeyEvent.VK_R);
        race_OKButton.addActionListener(e -> {
            try {
                if (race_rollResult.getValue() > 0 && race_rollResult.getValue() <= 100) {
                    int rollResultNumeric = race_rollResult.getValue();
                    rollRace = connection.getRaceFromTable(rollResultNumeric);
                    race_option1.setText(rollRace.getName());

                    race_rollButton.setEnabled(false);
                    race_rollResult.setEditable(false);
                    race_OKButton.setEnabled(false);

                    race_option1.setEnabled(true);
                    race_option1Button.setEnabled(true);
                    race_option2Combo.setEnabled(true);
                    race_option2Button.setEnabled(true);
                }
            } catch (Exception ex) {
                race_rollResult.setText("");
            }
        });
        race_OKButton.setMnemonic(KeyEvent.VK_O);

        race_option1Button.addActionListener(e -> {
            race_option2Combo.setSelectedItem(race_option1.getText());
            sheet.setRace(rollRace);
            expField.changeValue(20);

            race_option1Button.setEnabled(false);
            race_option1.setEditable(false);
            race_option2Button.setEnabled(false);
            race_option2Combo.setLocked(true);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        race_option1Button.setMnemonic(KeyEvent.VK_1);
        race_option2Button.addActionListener(e -> {
            try {
                race_option1.setText((String) race_option2Combo.getSelectedItem());
                sheet.setRace(connection.getRace((String) race_option2Combo.getSelectedItem()));

                race_option1Button.setEnabled(false);
                race_option1.setEditable(false);
                race_option2Button.setEnabled(false);
                race_option2Combo.setLocked(true);

                moveToNextTab(tabbedPane.getSelectedIndex());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        race_option2Button.setMnemonic(KeyEvent.VK_2);

        // Profession //
        prof_rollButton.addActionListener(e -> {
            Profession rollProf;
            do {
                Object[] result = getRandomProf(sheet.getRace());
                int rollResultNumeric = (int) result[0];
                rollProf = (Profession) result[1];

                prof_rollResult.setValue(rollResultNumeric);
            } while (profList.contains(rollProf));
            profList.add(rollProf);
            prof_options[profList.size() - 1][0].setText(rollProf.getCareer().getName());
            prof_options[profList.size() - 1][1].setText(rollProf.getName());
            prof_buttons[profList.size() - 1].setEnabled(true);
            if (profList.size() > 1)
                prof_maxExp = 25;
            if (profList.size() >= 3) {
                prof_rollButton.setEnabled(false);
                prof_rollResult.setEditable(false);
                prof_OKButton.setEnabled(false);

                prof_option4Button.setEnabled(true);
                prof_option4a.setEnabled(true);
                prof_option4b.setEnabled(true);
            }
        });
        prof_rollButton.setMnemonic(KeyEvent.VK_R);
        prof_OKButton.addActionListener(e -> {
            try {
                if (Integer.parseInt(prof_rollResult.getText()) > 0 && Integer.parseInt(prof_rollResult.getText()) <= 100) {
                    int rollResultNumeric = Integer.parseInt(prof_rollResult.getText());
                    Profession prof = connection.getProfFromTable(sheet.getRace().getID(), rollResultNumeric);
                    if (profList.contains(prof)) {
                        rollLabel.setVisible(true);
                        throw new Exception();
                    } else {
                        rollLabel.setVisible(false);
                        profList.add(prof);
                        prof_options[profList.size() - 1][0].setText(prof.getCareer().getName());
                        prof_options[profList.size() - 1][1].setText(prof.getName());
                        prof_buttons[profList.size() - 1].setEnabled(true);
                    }
                    if (profList.size() >= 3) {
                        prof_rollButton.setEnabled(false);
                        prof_rollResult.setEditable(false);
                        prof_OKButton.setEnabled(false);
                    }
                }
            } catch (Exception ex) {
                prof_rollResult.setText("");
            }
        });
        prof_option1Button.addActionListener(e -> {
            sheet.setProfession(profList.get(0));
            expField.changeValue(prof_maxExp);

            prof_rollButton.setEnabled(false);
            prof_rollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_buttons)
                button.setEnabled(false);
            prof_option4a.setLocked(true);
            prof_option4b.setLocked(true);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_option1Button.setMnemonic(KeyEvent.VK_1);
        prof_option2Button.addActionListener(e -> {
            sheet.setProfession(profList.get(1));
            expField.changeValue(prof_maxExp);

            prof_rollButton.setEnabled(false);
            prof_rollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_buttons)
                button.setEnabled(false);
            prof_option4a.setLocked(true);
            prof_option4b.setLocked(true);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_option2Button.setMnemonic(KeyEvent.VK_2);
        prof_option3Button.addActionListener(e -> {
            sheet.setProfession(profList.get(2));
            expField.changeValue(prof_maxExp);

            prof_rollButton.setEnabled(false);
            prof_rollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_buttons)
                button.setEnabled(false);
            prof_option4a.setLocked(true);
            prof_option4b.setLocked(true);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_option3Button.setMnemonic(KeyEvent.VK_3);

        /* TODO: Make prof_option4Button works */
        /* FIXME: Optimise SearchableJComboBoxes */
        // prof_option4a.addActionListener(e -> prof_option4b.addItems(connection.getProfsNames(sheet.getRace().getID(), prof_option4a.getValue())));

        // Attributes //
        attr_rollButton.addActionListener(e -> {
            Object[] result = getOneRandomAttr(attr_itr, sheet.getRace());
            int rollAttr = (int) result[0];
            int sumAttr = (int) result[1];

            RAttr.get(attr_itr).setValue(rollAttr);
            RAttr.get(attr_itr).setEditable(false);
            TAttr.get(attr_itr).setValue(sumAttr);
            attr_sumField.changeValue(rollAttr);
            attr_itr++;

            if (attr_itr==9) {
                calculateHP();
            }
            if (attr_itr==10) {
                calculateHP();
                attr_locked = false;
                attr_rollButton.setEnabled(false);
                attr_rollAllButton.setEnabled(false);
                attr_OKButton.setEnabled(false);
                attr_option1Button.setEnabled(true);
            }
        });
        attr_rollAllButton.addActionListener(e -> {
            Object[] result = getAllRandomAttr(sheet.getRace());
            Integer[] rollAttr = (Integer[]) result[0];
            Integer[] sumAttr = (Integer[]) result[1];
            attr_sumField.setValue((int) result[2]);

            for (int i=0;i<10;i++) {
                RAttr.get(i).setValue(rollAttr[i]);
                RAttr.get(i).setEditable(false);
                TAttr.get(i).setValue(sumAttr[i]);
            }
            calculateHP();

            attr_locked = false;
            attr_rollButton.setEnabled(false);
            attr_rollAllButton.setEnabled(false);
            attr_OKButton.setEnabled(false);
            attr_option1Button.setEnabled(true);
        });
        attr_rollAllButton.setMnemonic(KeyEvent.VK_R);
        attr_OKButton.addActionListener(e -> {
            //TODO Add custom values given by user
        });
        attr_option1Button.addActionListener(e -> {
            for (int i = 0; i < RAttr.size(); i++) {
                attributes.get(i).setRndValue(RAttr.get(i).getValue());
            }
            sheet.setAttributeList(attributes);
            sheet.setMove(attr_move.getValue());
            sheet.setMaxHP(attr_hp.getValue());
            sheet.setHP();

            attr_option1Button.setEnabled(false);
            attr_locked = true;
            expField.changeValue(attr_maxExp);
            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        attr_option1Button.setMnemonic(KeyEvent.VK_1);
        a3PutOwnValuesButton.addActionListener(e -> {
            //TODO: Make a3PutOwnValuesButton work and probably change the name
        });

        // Fate & Resolve //
        /* TODO: Maybe change all buttons to JSpinners :thinking: */
        createActionMnemonic(fate_panel, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK), () -> {
            Integer[] slots = new Integer[] {0, 0, 0};
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

            fate_attributeTable.iterateThroughColumns(2, (o, i) -> {
                AdvancedSpinner spinner = (AdvancedSpinner) o;
                if (spinner.isEnabled()) {
                    spinner.setValue(attributes[i]);
                }
            });
        });
        fate_fateUP.addActionListener(e -> {
            fate_extra.setValue(fate_extra.getValue() - 1);
            fate_fate.setValue(fate_fate.getValue() + 1);

            fate_fateUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_fateDOWN.setEnabled(true);
            fate_resilienceUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());

            fate_option1Button.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
        });
        fate_fateDOWN.addActionListener(e -> {
            fate_extra.setValue(fate_extra.getValue()+1);
            fate_fate.setValue(fate_fate.getValue()-1);

            fate_fateUP.setEnabled(true);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(true);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());

            fate_option1Button.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
        });
        fate_resilienceUP.addActionListener(e -> {
            fate_extra.setValue(fate_extra.getValue() - 1);
            fate_resilience.setValue(fate_resilience.getValue() + 1);

            fate_fateUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_resilienceDOWN.setEnabled(true);

            fate_option1Button.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
        });
        fate_resilienceDOWN.addActionListener(e -> {
            fate_extra.setValue(fate_extra.getValue() + 1);
            fate_resilience.setValue(fate_resilience.getValue() - 1);

            fate_fateUP.setEnabled(true);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(true);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());

            fate_option1Button.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
        });
        fate_option1Button.addActionListener(e -> { //
            sheet.setAttributeList(attributes);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        fate_option1Button.setMnemonic(KeyEvent.VK_1);

        // Race skills & Talents //
        createActionMnemonic(raceskill_panel, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK), () -> {
            raceSkills = randomizeSkillsWithList(raceSkillGroups, new int[]{3, 3, 3, 5, 5, 5});
            raceskill_updateTable(raceSkills);

            for (int i = 0; i < raceTalentGroups.size(); i++) {
                updateTalentRow(raceTalentGroups.get(i), raceTalents, raceskill_talentsPanel, i + 1, raceTalentGroups.get(i).getRndTalentIndex());
            }
            for (int i = 0; i < sheet.getRace().getRandomTalents(); i++) {
                racetalent_roll();
            }
        });
        raceskill_rollButton.addActionListener(e -> racetalent_roll());
        raceskill_rollButton.setMnemonic(KeyEvent.VK_R);

        raceskill_option1.addActionListener(e -> {
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

            moveToNextTab(tabbedPane.getSelectedIndex());
        });

        // Pane controls //
        tabbedPane.addChangeListener(e -> {
            int tab = tabbedPane.getSelectedIndex();
            String iconPath = String.format("src/resources/images/round%d.png", tab + 1);
            ImageIcon icon = new ImageIcon(iconPath);
            imageLabel.setIcon(icon);
        });
        exitButton.addActionListener(e -> {
            frame.setContentPane(previous_screen.mainPanel);
            frame.validate();
        });
    }


    private void attr_createTable() {
        attributes = sheet.getRace().getAttributes();

        attr_move = new JIntegerField(sheet.getRace().getM());
        attr_move.setHorizontalAlignment(JTextField.CENTER);
        attr_move.setEditable(false);
        attr_move.setFont(new Font(attr_move.getFont().getName(),Font.ITALIC+Font.BOLD,attr_move.getFont().getSize()+2));
        attributesTable.add(attr_move, new GridConstraints(1, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null), false);

        for (int i = 0; i < attributes.size(); i++) {
            boolean changeBackground = (sheet.getRace().getSize()== Size.NORMAL && i == 2) || i == 3 || i == 8;
            Color foregroundColor = Color.black;
            if (sheet.getProfession().hasAttribute(i+1)) {
                foregroundColor = ColorPalette.CustomGreen;
            }

            JLabel charLabel = new JLabel();
            charLabel.setHorizontalAlignment(JLabel.CENTER);
            charLabel.setHorizontalTextPosition(0);
            charLabel.setText(attributes.get(i).getName());
            attributesTable.add(charLabel, new GridConstraints(0, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);

            JIntegerField baseAttr = new JIntegerField(attributes.get(i).getBaseValue());
            baseAttr.setForeground(foregroundColor);
            if (changeBackground) {
                baseAttr.setBackground(ColorPalette.CustomWhiteBlue);
            }
            baseAttr.setHorizontalAlignment(JTextField.CENTER);
            baseAttr.setEditable(false);
            attributesTable.add(baseAttr, new GridConstraints(1, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);
            BAttr.add(baseAttr);

            JIntegerField attr = new JIntegerField();
            attr.setForeground(foregroundColor);
            if (changeBackground) {
                attr.setBackground(ColorPalette.CustomWhiteBlue);
            }
            attr.setHorizontalAlignment(JTextField.CENTER);
            attr.addMouseListener((MouseClickedAdapter) this::attr_replaceValues);
            attributesTable.add(attr, new GridConstraints(2, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);
            RAttr.add(attr);

            JIntegerField sumAttr = new JIntegerField(attributes.get(i).getTotalValue());
            sumAttr.setForeground(foregroundColor);
            if (changeBackground) {
                sumAttr.setBackground(ColorPalette.CustomWhiteBlue);
            }
            sumAttr.setHorizontalAlignment(JTextField.CENTER);
            sumAttr.setEditable(false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(),Font.ITALIC+Font.BOLD,sumAttr.getFont().getSize()+2));
            attributesTable.add(sumAttr, new GridConstraints(3, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);
            TAttr.add(sumAttr);
        }

        attr_hp = new JIntegerField();
        attr_hp.setHorizontalAlignment(JTextField.CENTER);
        attr_hp.setEditable(false);
        attr_hp.setFont(new Font(attr_hp.getFont().getName(),Font.ITALIC+Font.BOLD,attr_hp.getFont().getSize()+2));
        attr_hp.setBackground(ColorPalette.CustomWhiteBlue);
        attributesTable.add(attr_hp, new GridConstraints(1, attributes.size()+1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null), false);
        attributesTable.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void attr_replaceValues(MouseEvent e) {
        if (!attr_locked) {
            if (mouse_source == null) {
                mouse_source = (JIntegerField) e.getSource();
                mouse_color = mouse_source.getForeground();
                mouse_source.setForeground(Color.red);
                mouse_source.setFont(new Font(mouse_source.getFont().getName(),Font.BOLD, mouse_source.getFont().getSize()));
            }
            else {
                JIntegerField target = (JIntegerField) e.getSource();
                int temp = target.getValue();
                target.setValue(mouse_source.getValue());
                mouse_source.setValue(temp);
                mouse_source.setForeground(mouse_color);
                mouse_source.setFont(new Font(mouse_source.getFont().getName(),Font.PLAIN, mouse_source.getFont().getSize()));
                mouse_source = null;
            }
            attr_maxExp = 25;
            calculateTotal();
        }
    }

    private void fate_createTable() {
        List<JIntegerField> values = List.copyOf(TAttr);
        List<Component> tabOrder = new ArrayList<>();
        BAttr.clear();
        RAttr.clear();
        TAttr.clear();

        String[] columns = {"WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        for (int i = 0; i < columns.length; i++) {
            JLabel charLabel = new JLabel(columns[i], JLabel.CENTER);
            fate_attributeTable.add(charLabel, new GridConstraints(0, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }

        attr_hp = new JIntegerField(attr_hp.getValue());
        attr_hp.setHorizontalAlignment(JTextField.CENTER);
        attr_hp.setEditable(false);
        fate_attributeTable.add(attr_hp, new GridConstraints(1, columns.length-1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null), false);

        for (int i = 0; i < columns.length-1; i++) {
            int finalI = i;

            JIntegerField attr = new JIntegerField(values.get(i).getValue());
            attr.setHorizontalAlignment(JTextField.CENTER);
            attr.setEditable(false);
            BAttr.add(attr);
            fate_attributeTable.add(attr, new GridConstraints(1, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            AdvancedSpinner adv = new AdvancedSpinner(new SpinnerNumberModel(0, 0, 5, 1));
            adv.setEnabled(false);
            if (sheet.getProfession().hasAttribute(i+1)) {
                adv.setEnabled(true);
                tabOrder.add(adv.getTextField());
            }
            fate_attributeTable.add(adv,new GridConstraints(2, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            JIntegerField sumAttr = new JIntegerField(BAttr.get(i).getValue());
            sumAttr.setHorizontalAlignment(JTextField.CENTER);
            sumAttr.setEditable(false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(), Font.ITALIC + Font.BOLD, sumAttr.getFont().getSize() + 2));
            TAttr.add(sumAttr);
            fate_attributeTable.add(sumAttr, new GridConstraints(3, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            adv.addChangeListener(e -> fate_updatePoints(adv, finalI, sumAttr));
        }
        fate_attributeTable.setFocusCycleRoot(true);
        fate_attributeTable.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        fate_attributeTable.build(GridPanel.ALIGNMENT_HORIZONTAL);

        fate_fate.setValue(sheet.getRace().getFate());
        fate_resilience.setValue(sheet.getRace().getResilience());
        fate_extra.setValue(sheet.getRace().getExtra());
    }
    private void fate_updatePoints(AdvancedSpinner activeSpinner, int finalI, JIntegerField field) {
        int now = (int) (activeSpinner.getValue());
        int adv = attributes.get(finalI).getAdvValue();

        if (adv != now) {
            fate_attrRemain.changeValue(adv - now);
            field.changeValue(now - adv);
            attributes.get(finalI).setAdvValue(now);
            calculateHP();

            fate_attributeTable.iterateThroughColumns(2, (o, i) -> {
                AdvancedSpinner spinner = (AdvancedSpinner) o;
                if (activeSpinner != o) {
                    SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                    model.setMaximum((int) model.getValue() + fate_attrRemain.getValue());
                }
            });
        }

        fate_option1Button.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
    }

    private void raceskill_createTable() {
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
                color = ColorPalette.CustomGreen;
            }

            Container nameContainer = createSkillComboIfNeeded(raceSkillGroups.get(i), raceskill_skillsPanel, row, column);
            nameContainer.setForeground(color);
            column++;

            JTextField attrField = new JTextField(raceSkills.get(i).getAttr().getName());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            raceskill_skillsPanel.add(attrField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            AdvancedSpinner jSpinner = new AdvancedSpinner(new SpinnerTypeListModel<>(new Integer[]{0, 3, 5}));
            jSpinner.setHorizontalAlignment(JTextField.CENTER);
            raceskill_skillsPanel.add(jSpinner, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            JIntegerField sumField = new JIntegerField(raceSkills.get(i).getTotalValue(), "%d", JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            raceskill_skillsPanel.add(sumField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            int finalI = i;
            jSpinner.addChangeListener(e -> raceskill_updatePoints(nameContainer, jSpinner, sumField, raceSkills.get(finalI), profSkillGroups.contains(raceSkills.get(finalI))));
            tabOrder.add(jSpinner.getTextField());
        }
        if (base_itr != 1) {
            raceskill_skillsPanel.add(new JLabel("Basic skills", JLabel.CENTER), new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }
        if (adv_itr != 1) {
            raceskill_skillsPanel.add(new JLabel("Advanced skills", JLabel.CENTER), new GridConstraints(0, 4, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }

        raceskill_skillsPanel.setFocusCycleRoot(true);
        raceskill_skillsPanel.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        raceskill_skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

        raceskill_updateTable(raceSkills);
    }
    private void raceskill_updateTable(List<Skill> raceSkills) {
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
                color = ColorPalette.CustomGreen;
            }

            if (raceskill_skillsPanel.getComponent(column, row) instanceof SearchableComboBox) {
                ((SearchableComboBox) raceskill_skillsPanel.getComponent(column, row)).getModel().setSelectedItem(skill.getName());
            }
            ((AdvancedSpinner) raceskill_skillsPanel.getComponent(column + 2, row)).setValue(skill.getAdvValue());
        }
    }
    private void raceskill_updatePoints(Container container, AdvancedSpinner spinner, JIntegerField totalField, Skill skill, boolean paintGreen) {
        int now = (int) spinner.getValue();
        int last = skill.getAdvValue();

        if (skill.getAdvValue() != now) {
            skill.setAdvValue(now);
            totalField.setValue(skill.getTotalValue());

            raceskill_points.put(last, raceskill_points.get(last)-1);
            raceskill_points.put(now, raceskill_points.get(now)+1);
        } else if (spinner.getValue() != spinner.getLastValue()) {
            totalField.setValue(skill.getTotalValue());

            raceskill_points.put((Integer) spinner.getLastValue(), raceskill_points.get(last)-1);
            raceskill_points.put((Integer) spinner.getValue(), raceskill_points.get(now)+1);
        }

        if (now == 0 && skill.getBaseSkill().isAdv()) {
            container.setForeground(Color.red);
        } else {
            container.setForeground(Color.black);
        }

        if (paintGreen) {
            container.setForeground(ColorPalette.CustomGreen);
        }

//        raceskill_skillsPanel.iterateThroughRows(2, 1, baseSkills.size(), this::raceskill_changeColor);
//        raceskill_skillsPanel.iterateThroughRows(6, 1, advSkills.size(), this::raceskill_changeColor);
    }
    private void raceskill_changeColor(Object object) {
        AdvancedSpinner active = (AdvancedSpinner) object;
        if (raceskill_points.get((int) active.getValue()) > 3) {
            active.getTextField().setForeground(Color.RED);
        } else {
            active.getTextField().setForeground(Color.BLACK);
        }
    }

    private void racetalent_createTable() {
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
        raceskill_talentsPanel.add(new JLabel("Talents", JLabel.CENTER), new GridConstraints(0, 0, 1, -1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        for (int i = 0; i < raceTalentGroups.size(); i++) {
            int column = 0;

            Container nameContainer = createTalentComboIfNeeded(raceTalentGroups.get(i), raceskill_talentsPanel, i + 1, column, columnDimensions[column]);
            column++;

            JTextField attrField = new JTextField(String.valueOf(raceTalents.get(i).getCurrentLvl()));
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            raceskill_talentsPanel.add(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            JTextField attrField2 = new JTextField(raceTalents.get(i).getMaxString());
            attrField2.setHorizontalAlignment(JTextField.CENTER);
            attrField2.setEditable(false);
            attrField2.setFocusable(false);
            raceskill_talentsPanel.add(attrField2, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            JTextArea testArea = new JTextArea(raceTalents.get(i).getBaseTalent().getTest());
            testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));
            testArea.setLineWrap(true);
            testArea.setWrapStyleWord(true);
            testArea.setEditable(false);
            raceskill_talentsPanel.add(testArea, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            String tooltip = raceTalents.get(i).getBaseTalent().getDesc();
            JLabel desc = new JLabel(new ImageIcon("src/resources/images/info.png"));
            desc.setToolTipText(MultiLineTooltip.splitToolTip(tooltip, 75, 10));
            raceskill_talentsPanel.add(desc, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null), false);
        }

        raceskill_talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

        // Talents - Random Talents
        if (sheet.getRace().getRandomTalents() != 0) {
            raceskill_randomTalentsLabel.setVisible(true);
            raceskill_rollPanel.setVisible(true);
            for (int i=0;i<sheet.getRace().getRandomTalents();i++) {
                int column = 0;

                SearchableComboBox nameField = new SearchableComboBox();
                nameField.setLocked(true);
                raceskill_randomTalentsPanel.add(nameField, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JTextField attrField = new JTextField();
                attrField.setHorizontalAlignment(JTextField.CENTER);
                attrField.setEditable(false);
                attrField.setFocusable(false);
                raceskill_randomTalentsPanel.add(attrField, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JTextField attrField2 = new JTextField();
                attrField2.setHorizontalAlignment(JTextField.CENTER);
                attrField2.setEditable(false);
                attrField2.setFocusable(false);
                raceskill_randomTalentsPanel.add(attrField2, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JTextArea testArea = new JTextArea();
                testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));
                testArea.setLineWrap(true);
                testArea.setWrapStyleWord(true);
                testArea.setEditable(false);
                raceskill_randomTalentsPanel.add(testArea, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JLabel desc = new JLabel(new ImageIcon("src/resources/images/info.png"));
                raceskill_randomTalentsPanel.add(desc, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null), false);
            }
        }

        raceskill_randomTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void racetalent_roll() {
        if (raceskill_rollButton.isEnabled()) {
            TalentGroup rollTalent;
            do {
                Object[] result = getRandomTalent();
                int rollResultNumeric = (int) result[0];
                rollTalent = (TalentGroup) result[1];

                raceskill_rollResult.setValue(rollResultNumeric);
            } while (randomTalentGroups.contains(rollTalent));
            TalentGroup rollTalentGroup = rollTalent;
            randomTalentGroups.add(rollTalentGroup);
            randomTalents.add(rollTalentGroup.getFirstTalent());

            int row = randomTalentGroups.size() - 1;

            SearchableComboBox activeField = (SearchableComboBox) raceskill_randomTalentsPanel.getComponent(0, row);
            for (Talent talent : rollTalentGroup.getTalents()) {
                activeField.addItem(talent.getName());
            }
            activeField.refresh();
            activeField.setLocked(activeField.getItemCount() == 1);
            activeField.setEditable(!rollTalentGroup.isLocked());
            String tooltip = rollTalentGroup.getName();
            if (tooltip != null)
                activeField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            activeField.addActionListener(e -> updateTalentRow(rollTalentGroup, randomTalents, raceskill_randomTalentsPanel, row, ((SearchableComboBox) e.getSource()).getSelectedIndex()));

            JTextField activeField2 = (JTextField) raceskill_randomTalentsPanel.getComponent(1, row);
            activeField2.setText(String.valueOf(rollTalentGroup.getFirstTalent().getCurrentLvl()));

            JTextField activeField3 = (JTextField) raceskill_randomTalentsPanel.getComponent(2, row);
            activeField3.setText(rollTalentGroup.getFirstTalent().getMaxString());

            JTextArea activeArea = (JTextArea) raceskill_randomTalentsPanel.getComponent(3, row);
            activeArea.setText(rollTalentGroup.getFirstTalent().getBaseTalent().getTest());

            JLabel activeLabel = (JLabel) raceskill_randomTalentsPanel.getComponent(4, row);
            activeLabel.setToolTipText(MultiLineTooltip.splitToolTip(rollTalentGroup.getFirstTalent().getBaseTalent().getDesc(), 75, 10));

            if (sheet.getRace().getRandomTalents() <= randomTalentGroups.size()) {
                raceskill_rollButton.setEnabled(false);
                raceskill_rollResult.setEditable(false);
                raceskill_OKButton.setEnabled(false);
            }
        }
    }

    private void profskill_createTable() {
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

            Container nameContainer = createSkillComboIfNeeded(profSkills.get(i), profskill_skillsPanel, row, column);
            nameContainer.setForeground(color);
            column++;

            JTextField attrField = new JTextField(profSkillGroups.get(i).getAttr().getName());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            profskill_skillsPanel.add(attrField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            AdvancedSpinner jSpinner = new AdvancedSpinner(new SpinnerNumberModel(profSkillGroups.get(i).getAdvValue(), profSkillGroups.get(i).getAdvValue(), 10, 1));
            jSpinner.setHorizontalAlignment(JTextField.CENTER);
            profskill_skillsPanel.add(jSpinner, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            JIntegerField sumField = new JIntegerField(profSkillGroups.get(i).getTotalValue(), "%d", JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            profskill_skillsPanel.add(sumField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            jSpinner.addChangeListener(e -> profskill_updatePoints(nameContainer, jSpinner, sumField, profSkillGroups.get(finalI)));
            tabOrder.add(jSpinner.getTextField());
        }

        if (base_itr != 1) {
            profskill_skillsPanel.add(new JLabel("Basic skills", JLabel.CENTER), new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }
        if (adv_itr != 1) {
            profskill_skillsPanel.add(new JLabel("Advanced skills", JLabel.CENTER), new GridConstraints(0, 4, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }
        profskill_skillsPanel.setFocusCycleRoot(true);
        profskill_skillsPanel.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        profskill_skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void profskill_updatePoints(Container container, AdvancedSpinner spinner, JIntegerField totalField, Skill skill) {
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

    private void proftalent_createTable() {
        ButtonGroup buttonGroup = new ButtonGroup();

        // Constants
        final Dimension[] columnDimensions = new Dimension[]{
                new Dimension(200, -1),
                new Dimension(30, -1),
                new Dimension(200, -1)
        };

        // Talents - Header
        profskill_talentsPanel.add(new JLabel("Talents", JLabel.CENTER), new GridConstraints(0, 0, 1, -1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        for (int i = 0; i < profTalentGroups.size(); i++) {
            int column = 0;

            Container nameContainer = createTalentComboIfNeeded(profTalentGroups.get(i), profskill_talentsPanel, i + 1, column, columnDimensions[column]);
            column++;

            JTextField attrField = new JTextField(profTalents.get(i).getMaxString());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            profskill_talentsPanel.add(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            JTextArea testArea = new JTextArea(profTalents.get(i).getBaseTalent().getTest());
            testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));
            testArea.setLineWrap(true);
            testArea.setWrapStyleWord(true);
            testArea.setEditable(false);
            profskill_talentsPanel.add(testArea, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            String tooltip = profTalents.get(i).getBaseTalent().getDesc();
            JLabel desc = new JLabel(new ImageIcon("src/resources/images/info.png"));
            desc.setToolTipText(MultiLineTooltip.splitToolTip(tooltip, 75, 10));
            profskill_talentsPanel.add(desc, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null), false);

            JRadioButton radioButton = new JRadioButton();
            buttonGroup.add(radioButton);
            profskill_talentsPanel.add(radioButton, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null), false);
        }
        profskill_talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }

    private void calculateHP() {
        int value = (TAttr.get(3).getValue() / 10) * 2 + TAttr.get(8).getValue() / 10;
        if (sheet.getRace().getSize() == Race.Size.NORMAL)
            value += TAttr.get(2).getValue() / 10;
        attr_hp.setValue(value);
    }
    private void calculateTotal() {
        for (int i=0;i<10;i++)
            TAttr.get(i).setValue(BAttr.get(i).getValue() + RAttr.get(i).getValue());
        calculateHP();
    }

    private void moveToNextTab(int tab) {
        tabbedPane.setEnabledAt(tab + 1, true);
        tabbedPane.setSelectedIndex(tab + 1);
        Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Loaded tab %d", tab + 1));
        switch (tab + 1) {
            case 1:
//                prof_option4a.addItems(connection.getProfsClasses(sheet.getRace().getID()));
//                prof_option4b.addItems(connection.getProfsNames(sheet.getRace().getID(), prof_option4a.getValue()));
                break;
            case 2:
                attr_createTable();
                break;
            case 3:
                fate_createTable();
                break;
            case 4:
                raceskill_createTable();
                racetalent_createTable();
                break;
            case 5:
                profskill_createTable();
                proftalent_createTable();
                break;
            default:
                break;
        }
    }
    private void createUIComponents() {
        expField = new JIntegerField(0);
        expField.setRunnable((o, i) -> sheet.setExp(((JIntegerField) o).getValue()));

        fate_attrRemain = new JIntegerField(5);
        raceskill_points = new HashMap<>();
        raceskill_points.put(0, 0);
        raceskill_points.put(3, 0);
        raceskill_points.put(5, 0);
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
            comboBox.refresh(false);
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
            comboBox.refresh(false);
            comboBox.setEditable(false);
            comboBox.addActionListener(e -> updateTalentRow(groupTalent, raceTalents, raceskill_talentsPanel, row, ((SearchableComboBox) e.getSource()).getSelectedIndex()));
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
        panel.getActionMap().put(keyStroke.toString(), AbstractActionBuilder.getAction(runnable));
    }

    // Base functions to use with GUI and text //
    private Object[] getRandomRace() {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getRaceFromTable(numeric);
        return returns;
    }
    private Object[] getRandomProf(Race race) {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getProfFromTable(race.getID(), numeric);
        return returns;
    }
    private Object[] getOneRandomAttr(int index, Race race) {
        Object[] returns = new Object[2];
        int raceAttr = race.getRaceAttribute(index+1).getValue();
        int rollAttr = Dice.randomDice(2, 10);
        int sumAttr = raceAttr + rollAttr;

        returns[0] = rollAttr;
        returns[1] = sumAttr;
        return returns;
    }
    private Object[] getAllRandomAttr(Race race) {
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

    private List<Skill> randomizeSkillsWithList(List<SkillGroup> skills, int[] values) {
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
    private List<Talent> randomizeTalents(List<TalentGroup> talents) {
        List<Talent> returnList = new ArrayList<>();
        for (TalentGroup talent : talents) {
            returnList.add(talent.getRndTalent());
        }
        return returnList;
    }
    private Object[] getRandomTalent() {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getRandomTalent(numeric);
        return returns;
    }
}