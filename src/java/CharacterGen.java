import com.intellij.uiDesigner.core.GridConstraints;
import components.*;
import mappings.*;
import mappings.Race.Size;
import tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CharacterGen {
    public JPanel mainPanel;
    public Main previous_screen;

    private final JFrame frame;
    private final Connection connection;
    private final CharacterSheet sheet;
    private JLabel imageLabel;
    private JIntegerField expField;
    private JTabbedPane tabbedPane;
    private JButton exitButton;
    private JLabel rollLabel;

    private JIntegerField race_rollResult;
    private JButton race_option2Button;
    private SearchableJComboBox race_option2Combo;
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
    private SearchableJComboBox prof_option4a;
    private SearchableJComboBox prof_option4b;
    private JButton prof_option4Button;
    private final JTextField[][] prof_options = {
            {prof_option1a, prof_option1b},
            {prof_option2a, prof_option2b},
            {prof_option3a, prof_option3b}
    };
    private final JButton[] prof_buttons = {
            prof_option1Button, prof_option2Button, prof_option3Button, prof_option4Button
    };
    private List<GroupSkill> professionSkills;
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

    private GridPanel fate_attributeTable;
    private JIntegerField fate_attrRemain;
    private JIntegerField fate_fate;
    private JIntegerField fate_resilience;
    private JButton fate_fateUP;
    private JButton fate_fateDOWN;
    private JIntegerField fate_extra;
    private JButton fate_resilienceUP;
    private JButton fate_resilienceDOWN;
    private JButton fate_OKButton;

    private final List<GroupTalent> raceskill_randomTalents = new ArrayList<>();
    private final List<GroupSkill> baseSkills = new ArrayList<>();
    private final List<GroupSkill> advSkills = new ArrayList<>();
    private GridPanel raceskill_skillsPanel;
    private GridPanel raceskill_talentsPanel;
    private GridPanel raceskill_randomTalentsPanel;
    private JPanel raceskill_rollPanel;
    private JLabel raceskill_randomTalentsLabel;
    private JButton raceskill_rollButton;
    private JIntegerField raceskill_rollResult;
    private JButton raceskill_OKButton;
    private JButton raceskill_option1;
    private JLabel raceskill_noteLabel;
    private Map<Integer, Integer> raceskill_points;

    private JIntegerField mouse_source = null;
    private Color mouse_color;
    private boolean attr_locked = true;

    private Race rollRace;
    private final List<Profession> profList = new ArrayList<>();
    private final List<GroupTalent> talentsList = new ArrayList<>();

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
            prof_options[profList.size() - 1][1].setText(rollProf.getProfession());
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
                        prof_options[profList.size() - 1][1].setText(prof.getProfession());
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
            sheet.setProf(profList.get(0));
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
        prof_option2Button.addActionListener(e -> {
            sheet.setProf(profList.get(1));
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
        prof_option3Button.addActionListener(e -> {
            sheet.setProf(profList.get(2));
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
        //TODO: Make prof_option4Button works
        //FIXME: Optimise SearchableJComboBoxes
//        prof_option4a.addActionListener(e -> prof_option4b.addItems(connection.getProfsNames(sheet.getRace().getID(), prof_option4a.getValue())));

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
        attr_OKButton.addActionListener(e->{
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
        //TODO: Make a3PutOwnValuesButton work and probably change the name

        // Fate & Resolve //
        //TODO: Maybe change all buttons to JSpinners :thinking:
        fate_fateUP.addActionListener(e -> {
            fate_extra.setValue(fate_extra.getValue() - 1);
            fate_fate.setValue(fate_fate.getValue() + 1);

            fate_fateUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_fateDOWN.setEnabled(true);
            fate_resilienceUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());

            fate_OKButton.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
        });
        fate_fateDOWN.addActionListener(e -> {
            fate_extra.setValue(fate_extra.getValue()+1);
            fate_fate.setValue(fate_fate.getValue()-1);

            fate_fateUP.setEnabled(true);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(true);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());

            fate_OKButton.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
        });
        fate_resilienceUP.addActionListener(e -> {
            fate_extra.setValue(fate_extra.getValue() - 1);
            fate_resilience.setValue(fate_resilience.getValue() + 1);

            fate_fateUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_resilienceDOWN.setEnabled(true);

            fate_OKButton.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
        });
        fate_resilienceDOWN.addActionListener(e -> {
            fate_extra.setValue(fate_extra.getValue() + 1);
            fate_resilience.setValue(fate_resilience.getValue() - 1);

            fate_fateUP.setEnabled(true);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(true);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());

            fate_OKButton.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
        });
        fate_OKButton.addActionListener(e -> { //
            sheet.setAttributeList(attributes);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });

        // Race skills & Talents //
        raceskill_rollButton.addActionListener(e -> {
            GroupTalent rollTalent;
            do {
                Object[] result = getRandomTalent();
                int rollResultNumeric = (int) result[0];
                rollTalent = (GroupTalent) result[1];

                raceskill_rollResult.setValue(rollResultNumeric);
            } while (raceskill_randomTalents.contains(rollTalent));
            raceskill_randomTalents.add(rollTalent);
            JTextField activeField = (JTextField) raceskill_randomTalentsPanel.getComponent(0, raceskill_randomTalents.size()-1);
            activeField.setText(rollTalent.getName());

            activeField = (JTextField) raceskill_randomTalentsPanel.getComponent(1, raceskill_randomTalents.size() - 1);
//            racetalent_updateMax(rollTalent, activeField);

            JTextArea activeArea = (JTextArea) raceskill_randomTalentsPanel.getComponent(2, raceskill_randomTalents.size() - 1);
            activeArea.setText(rollTalent.getBaseTalent().getTest());

            JLabel activeLabel = (JLabel) raceskill_randomTalentsPanel.getComponent(3, raceskill_randomTalents.size() - 1);
            activeLabel.setToolTipText(MultiLineTooltip.splitToolTip(rollTalent.getBaseTalent().getDesc(), 75, 10));

            if (sheet.getRace().getRandomTalents() <= raceskill_randomTalents.size()) {
                raceskill_rollButton.setEnabled(false);
                raceskill_rollResult.setEditable(false);
                raceskill_OKButton.setEnabled(false);
            }
        });
        raceskill_option1.addActionListener(e -> {
            sheet.setTalentList(talentsList);
            System.out.println(sheet);

//            moveToNextTab(tabbedPane.getSelectedIndex());
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

    void attr_createTable() {
        attributes = connection.getRaceAttributes(sheet.getRace());

        attr_move = new JIntegerField(sheet.getRace().getM());
        attr_move.setHorizontalAlignment(JTextField.CENTER);
        attr_move.setEditable(false);
        attr_move.setFont(new Font(attr_move.getFont().getName(),Font.ITALIC+Font.BOLD,attr_move.getFont().getSize()+2));
        attributesTable.add(attr_move, new GridConstraints(1, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null), false);

        for (int i = 0; i < attributes.size(); i++) {
            boolean changeBackground = (sheet.getRace().getSize()== Size.NORMAL && i == 2) || i == 3 || i == 8;
            Color foregroundColor = Color.black;
            if (sheet.getProf().hasAttribute(i+1)) {
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
    void attr_replaceValues(MouseEvent e) {
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

    void fate_createTable() {
        List<JIntegerField> values = List.copyOf(TAttr);
        BAttr.clear();
        RAttr.clear();
        TAttr.clear();

        String[] columns = {"WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        for (int i = 0; i < columns.length; i++) {
            JLabel charLabel = new JLabel(columns[i], JLabel.CENTER);
            fate_attributeTable.add(charLabel, new GridConstraints(1, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }

        attr_hp = new JIntegerField(attr_hp.getValue());
        attr_hp.setHorizontalAlignment(JTextField.CENTER);
        attr_hp.setEditable(false);
        fate_attributeTable.add(attr_hp, new GridConstraints(2, columns.length-1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null), false);

        for (int i = 0; i < columns.length-1; i++) {
            int finalI = i;

            JIntegerField attr = new JIntegerField(values.get(i).getValue());
            attr.setHorizontalAlignment(JTextField.CENTER);
            attr.setEditable(false);
            BAttr.add(attr);
            fate_attributeTable.add(attr, new GridConstraints(2, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            JSpinner adv = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
            adv.setEnabled(false);
            if (sheet.getProf().hasAttribute(i+1)) {
                adv.setEnabled(true);
            }
            fate_attributeTable.add(adv,new GridConstraints(3, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            JIntegerField sumAttr = new JIntegerField(BAttr.get(i).getValue());
            sumAttr.setHorizontalAlignment(JTextField.CENTER);
            sumAttr.setEditable(false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(), Font.ITALIC + Font.BOLD, sumAttr.getFont().getSize() + 2));
            TAttr.add(sumAttr);
            fate_attributeTable.add(sumAttr, new GridConstraints(4, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            adv.addChangeListener(e -> fate_updatePoints(adv, finalI, sumAttr));
        }
        fate_attributeTable.build(GridPanel.ALIGNMENT_HORIZONTAL);

        fate_fate.setValue(sheet.getRace().getFate());
        fate_resilience.setValue(sheet.getRace().getResilience());
        fate_extra.setValue(sheet.getRace().getExtra());
    }
    void fate_updatePoints(JSpinner activeSpinner, int finalI, JIntegerField field) {
        int now = (int) (activeSpinner.getValue());
        int adv = attributes.get(finalI).getAdvValue();

        if (adv != now) {
            fate_attrRemain.changeValue(adv - now);
            field.changeValue(now - adv);
            attributes.get(finalI).setAdvValue(now);
            calculateHP();

            fate_attributeTable.iterateThroughColumns(3, o -> {
                JSpinner spinner = (JSpinner) o;
                if (activeSpinner != o) {
                    SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                    model.setMaximum((int) model.getValue() + fate_attrRemain.getValue());
                }
            });
        }

        fate_OKButton.setEnabled(fate_extra.getValue() == 0 && fate_attrRemain.getValue() == 0);
    }

    void raceskill_createTable() {
        List<GroupSkill> skills = connection.getSkillsByRace(sheet.getRace());
        List<Component> tabOrder = new ArrayList<>();
        professionSkills = connection.getProfessionSkills(sheet.getProf());

        // Skills - Headers
        raceskill_skillsPanel.add(new JLabel("Basic skills", JLabel.CENTER), new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        raceskill_skillsPanel.add(new JLabel("Advanced skills", JLabel.CENTER), new GridConstraints(0, 4, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);

        int base_itr = 1;
        int adv_itr = 1;
        int column, row;
        for (GroupSkill skill : skills) {
            Color color = Color.black;

            if (skill.isAdv()) {
                column = 4;
                row = adv_itr++;
                advSkills.add(skill);
            } else {
                column = 0;
                row = base_itr++;
                baseSkills.add(skill);
            }
            if (professionSkills.contains(skill)) {
                color = ColorPalette.CustomGreen;
            }

            for (Attribute attribute : attributes) {
                if (attribute.equals(skill.getAttr())) {
                    skill.setAttr(attribute);
                    break;
                }
            }

            Container nameContainer = raceskill_createComboIfNeeded(skill, row, column);
            nameContainer.setForeground(color);
            column++;

            JTextField attrField = new JTextField(skill.getAttr().getName());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            raceskill_skillsPanel.add(attrField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            ListSpinner<Integer> jSpinner = new ListSpinner<>(new Integer[]{0, 3, 5});
            jSpinner.setHorizontalAlignment(JTextField.CENTER);
            jSpinner.setEditable(false);
            raceskill_skillsPanel.add(jSpinner, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            JIntegerField sumField = new JIntegerField(skill.getTotalValue(), "%d", JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            raceskill_skillsPanel.add(sumField, new GridConstraints(row, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            jSpinner.addChangeListener(e -> raceskill_updatePoints(nameContainer, jSpinner, sumField, skill));
            tabOrder.add(((JSpinner.DefaultEditor) jSpinner.getEditor()).getTextField());
        }
        raceskill_skillsPanel.setFocusCycleRoot(true);
        raceskill_skillsPanel.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        raceskill_skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    Container raceskill_createComboIfNeeded(GroupSkill skill, int row, int column) {
        if (!skill.isGroup()) {
            JTextField textField = new JTextField(skill.getName());
            String tooltip = skill.getBase().getDescr();
            if (tooltip != null)
                textField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            textField.setFocusable(false);
            textField.setEditable(false);
            raceskill_skillsPanel.add(textField, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
            return textField;
        } else {
            SearchableJComboBox comboBox = new SearchableJComboBox();
            String tooltip = skill.getBase().getDescr();
            if (tooltip != null)
                comboBox.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            for (GroupSkill alternateSkill : connection.getAlternateSkillsForGroup(skill.getBase().getID()))
                comboBox.addItem(alternateSkill.getName());
            comboBox.setPreferredSize(new Dimension(comboBox.getSize().width, -1));
            comboBox.refresh(false);
            comboBox.setLocked(true);
            raceskill_skillsPanel.add(comboBox, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
            return comboBox;
        }
    }
    void raceskill_updatePoints(Container container, ListSpinner<Integer> spinner, JIntegerField totalField, GroupSkill skill) {
        int now = (int) spinner.getValue();
        int last = (int) spinner.getLastValue();

        if (skill.getAdvValue() != now) {
            skill.setAdvValue(now);
            totalField.setValue(skill.getTotalValue());

            raceskill_points.put(last, raceskill_points.get(last)-1);
            raceskill_points.put(now, raceskill_points.get(now)+1);
        }

        if (now == 0 && skill.getBase().isAdv()) {
            container.setForeground(Color.red);
        } else {
            container.setForeground(Color.black);
        }

        if (professionSkills.contains(skill)) {
            container.setForeground(ColorPalette.CustomGreen);
        }

        raceskill_skillsPanel.iterateThroughRows(2, 1, baseSkills.size() + 1, this::raceskill_changeColor);
        raceskill_skillsPanel.iterateThroughRows(6, 1, advSkills.size() + 1, this::raceskill_changeColor);
    }
    void raceskill_changeColor(Object object) {
        JSpinner active = (JSpinner) object;
        if (raceskill_points.get((int) active.getValue()) > 3) {
            ((JSpinner.DefaultEditor) active.getEditor()).getTextField().setForeground(Color.RED);
        } else {
            ((JSpinner.DefaultEditor) active.getEditor()).getTextField().setForeground(Color.BLACK);
        }
    }

    void racetalent_createTable() {
        List<RaceTalent> raceTalentsList = connection.getTalentsByRace(sheet.getRace().getID());
        raceTalentsList.forEach(e -> talentsList.add(e.getTalent()));

        // Constants
        final Dimension[] columnDimensions = new Dimension[]{
                new Dimension(200, -1),
                new Dimension(30, -1),
                new Dimension(200, -1)
        };

        // Talents - Header
        raceskill_talentsPanel.add(new JLabel("Talents", JLabel.CENTER), new GridConstraints(0, 0, 1, -1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        for (int i = 0; i < raceTalentsList.size(); i++) {
            int column = 0;

            racetalent_createComboIfNeeded(raceTalentsList.get(i), i + 1, column, columnDimensions[column]);
            column++;

            JTextField attrField = new JTextField();
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
//            racetalent_updateMax(talents.get(i).getTalent(), attrField);
            raceskill_talentsPanel.add(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            JTextArea testArea = new JTextArea(raceTalentsList.get(i).getTalent().getBaseTalent().getTest());
            testArea.setFont(new Font(testArea.getFont().getName(), testArea.getFont().getStyle(), 10));
            testArea.setLineWrap(true);
            testArea.setWrapStyleWord(true);
            testArea.setEditable(false);
            raceskill_talentsPanel.add(testArea, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

            String tooltip = raceTalentsList.get(i).getTalent().getBaseTalent().getDesc();
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

                JTextField nameField = new JTextField();
                nameField.setEditable(false);
                raceskill_randomTalentsPanel.add(nameField, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

                JTextField attrField = new JTextField();
                attrField.setHorizontalAlignment(JTextField.CENTER);
                attrField.setEditable(false);
                attrField.setFocusable(false);
                raceskill_randomTalentsPanel.add(attrField, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[column-1], null), false);

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
    void racetalent_createComboIfNeeded(RaceTalent talent, int row, int column, Dimension columnDimensions) {
        if (talent.getTalentExcl() == null) {
            JTextField nameField = new JTextField(talent.getTalent().getName());
            nameField.setEditable(false);
            raceskill_talentsPanel.add(nameField, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions, null), false);
        } else {
            JComboBox<String> comboField = new JComboBox<>();
            comboField.addItem(talent.getTalent().getName());
            comboField.addItem(talent.getTalentExcl().getName());
            comboField.addActionListener(e -> racetalent_updateTalent(talent, row, comboField.getSelectedIndex()));
            raceskill_talentsPanel.add(comboField, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions, null), false);
        }
    }
//    void racetalent_updateMax(GroupTalent talent, JTextField testField) {
//        int max;
//        try {
//            Race.Attributes attr = Race.Attributes.find(talent.getBaseTalent().getMaxLvl());
//            max = sheet.getSumAttribute(attr) / 10;
//        } catch (Exception ex) {
//            max = Integer.parseInt(talent.getBaseTalent().getMaxLvl());
//        }
//        testField.setText(String.format("%d/%d", talent.getCurrentLvl(), max));
//    }
    void racetalent_updateTalent(RaceTalent talent, int row, int index) {
        JTextField testField = (JTextField) raceskill_talentsPanel.getComponent(1, row);
//        racetalent_updateMax(talent.getAllTalents()[index], testField);
        JTextArea textArea = (JTextArea) raceskill_talentsPanel.getComponent(2, row);
        textArea.setText(talent.getAllTalents()[index].getBaseTalent().getTest());
        JLabel desc = (JLabel) raceskill_talentsPanel.getComponent(3, row);
        desc.setToolTipText(MultiLineTooltip.splitToolTip(talent.getAllTalents()[index].getBaseTalent().getDesc()));

        talentsList.set(row-1, talent.getAllTalents()[index]);
    }

    void calculateHP() {
        int value = (TAttr.get(3).getValue() / 10) * 2 + TAttr.get(8).getValue() / 10;
        if (sheet.getRace().getSize() == Race.Size.NORMAL)
            value += TAttr.get(2).getValue() / 10;
        attr_hp.setValue(value);
    }
    void calculateTotal() {
        for (int i=0;i<10;i++)
            TAttr.get(i).setValue(BAttr.get(i).getValue() + RAttr.get(i).getValue());
        calculateHP();
    }

    void moveToNextTab(int tab) {
        tabbedPane.setEnabledAt(tab + 1, true);
        tabbedPane.setSelectedIndex(tab + 1);
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
        }
    }
    void createUIComponents() {
        expField = new JIntegerField(0);
        expField.setRunnable(o -> sheet.setExp(((JIntegerField) o).getValue()));

        fate_attrRemain = new JIntegerField(5);
        raceskill_points = new HashMap<>();
        raceskill_points.put(0, 0);
        raceskill_points.put(3, 0);
        raceskill_points.put(5, 0);
    }

    // Base functions to use with GUI and text //
    Object[] getRandomRace() {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getRaceFromTable(numeric);
        return returns;
    }
    Object[] getRandomProf(Race race) {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getProfFromTable(race.getID(), numeric);
        return returns;
    }
    Object[] getOneRandomAttr(int index, Race race) {
        Object[] returns = new Object[2];
        int raceAttr = race.getAttribute(index+1).getValue();
        int rollAttr = Dice.randomDice(2, 10);
        int sumAttr = raceAttr + rollAttr;

        returns[0] = rollAttr;
        returns[1] = sumAttr;
        return returns;
    }
    Object[] getAllRandomAttr(Race race) {
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
    Object[] getRandomTalent() {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        returns[0] = numeric;
        returns[1] = connection.getRandomTalent(numeric);
        return returns;
    }
    void getRaceSkills(Race race) {
        List<GroupSkill> baseSkills = connection.getBaseSkillsByRace(race.getID());
        List<GroupSkill> advSkills = connection.getAdvSkillsByRace(race.getID());
    }
}