import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import components.*;
import mappings.*;
import tools.ComponentList;
import tools.Dice;
import tools.MultiLineTooltip;

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
    private final List<Profession> profList = new ArrayList<>();
    private final JTextField[][] prof_options = {
            {prof_option1a, prof_option1b},
            {prof_option2a, prof_option2b},
            {prof_option3a, prof_option3b}
    };
    private final JButton[] prof_buttons = {
            prof_option1Button, prof_option2Button, prof_option3Button, prof_option4Button
    };
    private int prof_maxExp = 50;

    private JPanel attributesTable;
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

    private GridPanel raceskill_skillsPanel;
    private GridPanel raceskill_talentsPanel;
    private GridPanel raceskill_randomTalentsPanel;
    private JPanel raceskill_rollPanel;
    private JLabel raceskill_randomTalentsLabel;
    private JButton raceskill_rollButton;
    private JIntegerField raceskill_rollResult;
    private JButton raceskill_OKButton;
    private JButton raceskill_option1;
    private final List<GroupTalent> raceskill_randomTalents = new ArrayList<>();
    private final List<GroupSkill> baseSkills = new ArrayList<>();
    private final List<GroupSkill> advSkills = new ArrayList<>();
    private Map<Integer, Integer> raceskill_points;

    private JIntegerField mouse_source = null;
    private Color mouse_color;
    private boolean attr_locked = true;
    private Race rollRace;

    private final List<JIntegerField> BAttr = new ArrayList<>();
    private final List<JIntegerField> RAttr = new ArrayList<>();
    private final List<JIntegerField> TAttr = new ArrayList<>();

    private final ComponentList<JButton> fate_ButtonsUP = new ComponentList<>();
    private final ComponentList<JButton> fate_ButtonsDOWN = new ComponentList<>();

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
            updateExp(20);

            race_option1Button.setEnabled(false);
            race_option1.setEditable(false);
            race_option2Button.setEnabled(false);
            race_option2Combo.setNotEditable();

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
                race_option2Combo.setNotEditable();

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
            prof_options[profList.size() - 1][0].setText(rollProf.getCareer());
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
                        prof_options[profList.size() - 1][0].setText(prof.getCareer());
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
            updateExp(prof_maxExp);

            prof_rollButton.setEnabled(false);
            prof_rollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_buttons)
                button.setEnabled(false);
            prof_option4a.setNotEditable();
            prof_option4b.setNotEditable();

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_option2Button.addActionListener(e -> {
            sheet.setProf(profList.get(1));
            updateExp(prof_maxExp);

            prof_rollButton.setEnabled(false);
            prof_rollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_buttons)
                button.setEnabled(false);
            prof_option4a.setNotEditable();
            prof_option4b.setNotEditable();

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_option3Button.addActionListener(e -> {
            sheet.setProf(profList.get(2));
            updateExp(prof_maxExp);

            prof_rollButton.setEnabled(false);
            prof_rollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_buttons)
                button.setEnabled(false);
            prof_option4a.setNotEditable();
            prof_option4b.setNotEditable();

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        //TODO: Make prof_option4Button works
        //FIXME: Optimise SearchableJComboBoxes
        prof_option4a.addActionListener(e -> prof_option4b.addItems(connection.getProfsNames(sheet.getRace().getID(), prof_option4a.getValue())));

        // Attributes //
        attr_rollButton.addActionListener(e -> {
            Object[] result = getOneRandomAttr(attr_itr, sheet.getRace());
            int rollAttr = (int) result[0];
            int sumAttr = (int) result[1];

            RAttr.get(attr_itr).setValue(rollAttr);
            RAttr.get(attr_itr).setEditable(false);
            TAttr.get(attr_itr).setValue(sumAttr);
            attr_sumField.increment(rollAttr);
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
            for (int i=0;i<TAttr.size();i++)
                sheet.setBaseAttributes(i, TAttr.get(i).getValue());
            sheet.setMove(attr_move.getValue());
            sheet.setMaxHP(attr_hp.getValue());
            sheet.setHP();

            attr_option1Button.setEnabled(false);
            attr_locked = true;
            updateExp(attr_maxExp);
            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        //TODO: Make a3PutOwnValuesButton work and probably change the name

        // Fate & Resolve //
        //TODO: Maybe change all buttons to JSpinners :thinking:
        fate_fateUP.addActionListener(e -> {
            fate_extra.setText(String.valueOf(Integer.parseInt(fate_extra.getText()) - 1));
            fate_fate.setText(String.valueOf(Integer.parseInt(fate_fate.getText()) + 1));

            fate_fateUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_fateDOWN.setEnabled(true);
            fate_resilienceUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());
        });
        fate_fateDOWN.addActionListener(e -> {
            fate_extra.setText(String.valueOf(Integer.parseInt(fate_extra.getText())+1));
            fate_fate.setText(String.valueOf(Integer.parseInt(fate_fate.getText())-1));

            fate_fateUP.setEnabled(true);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(true);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());
        });
        fate_resilienceUP.addActionListener(e -> {
            fate_extra.setText(String.valueOf(Integer.parseInt(fate_extra.getText()) - 1));
            fate_resilience.setText(String.valueOf(Integer.parseInt(fate_resilience.getText()) + 1));

            fate_fateUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(Integer.parseInt(fate_extra.getText())!=0);
            fate_resilienceDOWN.setEnabled(true);
        });
        fate_resilienceDOWN.addActionListener(e -> {
            fate_extra.setText(String.valueOf(Integer.parseInt(fate_extra.getText())+1));
            fate_resilience.setText(String.valueOf(Integer.parseInt(fate_resilience.getText())-1));

            fate_fateUP.setEnabled(true);
            fate_fateDOWN.setEnabled(Integer.parseInt(fate_fate.getText())!=sheet.getRace().getFate());
            fate_resilienceUP.setEnabled(true);
            fate_resilienceDOWN.setEnabled(Integer.parseInt(fate_resilience.getText())!=sheet.getRace().getResilience());
        });
        fate_OKButton.addActionListener(e -> { //
            for (int i=0;i<TAttr.size();i++) {
                sheet.setAdvAttributes(i, RAttr.get(i).getValue());
                sheet.setSumAttributes(i, TAttr.get(i).getValue());
            }
            fate_ButtonsDOWN.setEnabled(sheet.getProf().getAllAttr(), false);

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
            racetalent_updateMax(rollTalent, activeField);
            if (sheet.getRace().getRandomTalents() <= raceskill_randomTalents.size()) {
                raceskill_rollButton.setEnabled(false);
                raceskill_rollResult.setEditable(false);
                raceskill_OKButton.setEnabled(false);
            }
        });

        // Pane controls //
        tabbedPane.addChangeListener(e -> {
            int tab = tabbedPane.getSelectedIndex();
            String iconPath = String.format("src/main/resources/images/round%d.png", tab + 1);
            ImageIcon icon = new ImageIcon(iconPath);
            imageLabel.setIcon(icon);
        });
        exitButton.addActionListener(e -> {
            frame.setContentPane(previous_screen.mainPanel);
            frame.validate();
        });
    }

    void attr_createTable() {
        String[] columns = {"M", "WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        attributesTable.setLayout(new GridLayoutManager(5, columns.length + 2, new Insets(0, 0, 0, 0), -1, -1));
        attributesTable.add(new Spacer(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
        attributesTable.add(new Spacer(), new GridConstraints(0, columns.length + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));

        for (int i = 0; i < columns.length; i++) {
            JLabel charLabel = new JLabel();
            charLabel.setHorizontalAlignment(JLabel.CENTER);
            charLabel.setHorizontalTextPosition(0);
            charLabel.setText(columns[i]);
            attributesTable.add(charLabel, new GridConstraints(1, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        }

        attr_move = new JIntegerField();
        attr_move.setHorizontalAlignment(JTextField.CENTER);
        attr_move.setEditable(false);
        attr_move.setFont(new Font(attr_move.getFont().getName(),Font.ITALIC+Font.BOLD,attr_move.getFont().getSize()+2));
        attributesTable.add(attr_move, new GridConstraints(2, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null));

        for (int i = 1; i < columns.length-1; i++) {
            JIntegerField baseAttr = new JIntegerField();
            baseAttr.setHorizontalAlignment(JTextField.CENTER);
            baseAttr.setEditable(false);
            BAttr.add(baseAttr);

            JIntegerField attr = new JIntegerField();
            attr.setHorizontalAlignment(JTextField.CENTER);
            RAttr.add(attr);
            attr.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
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
            });

            JIntegerField sumAttr = new JIntegerField();
            sumAttr.setHorizontalAlignment(JTextField.CENTER);
            sumAttr.setEditable(false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(),Font.ITALIC+Font.BOLD,sumAttr.getFont().getSize()+2));
            TAttr.add(sumAttr);

            attributesTable.add(baseAttr, new GridConstraints(2, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));
            attributesTable.add(attr, new GridConstraints(3, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));
            attributesTable.add(sumAttr, new GridConstraints(4, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));
        }

        attr_hp = new JIntegerField();
        attr_hp.setHorizontalAlignment(JTextField.CENTER);
        attr_hp.setEditable(false);
        attr_hp.setFont(new Font(attr_hp.getFont().getName(),Font.ITALIC+Font.BOLD,attr_hp.getFont().getSize()+2));
        attr_hp.setBackground(new Color(176, 224, 230));
        attributesTable.add(attr_hp, new GridConstraints(2, columns.length, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null));
    }
    void attr_fillTable() {
        attr_move.setValue(sheet.getRace().getM());

        for (int i=0; i<BAttr.size();i++) {
            Integer number = sheet.getRace().getAttr(i);
            BAttr.get(i).setValue(number);

            if ((sheet.getRace().getSize()==Race.Size.NORMAL && i == 2) || i == 3 || i == 8) {
                BAttr.get(i).setBackground(new Color(176, 224, 230));
                RAttr.get(i).setBackground(new Color(176, 224, 230));
                TAttr.get(i).setBackground(new Color(176, 224, 230));
            }
        }
        for (int i=0;i<RAttr.size();i++) {
            if (sheet.getProf().getAttr(i)) {
                BAttr.get(i).setForeground(new Color(0,128,0));
                RAttr.get(i).setForeground(new Color(0,128,0));
                TAttr.get(i).setForeground(new Color(0,128,0));
            }
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
            JButton buttonUp = new JButton("+");
            buttonUp.setEnabled(false);
            buttonUp.addActionListener(e -> fate_clickUP(finalI));
            fate_ButtonsUP.add(buttonUp);
            fate_attributeTable.add(buttonUp, new GridConstraints(0, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);

            JIntegerField attr = new JIntegerField(values.get(i).getValue());
            attr.setHorizontalAlignment(JTextField.CENTER);
            attr.setEditable(false);
            BAttr.add(attr);
            fate_attributeTable.add(attr, new GridConstraints(2, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            JIntegerField adv = new JIntegerField(0);
            adv.setHorizontalAlignment(JTextField.CENTER);
            adv.setEditable(false);
            RAttr.add(adv);
            fate_attributeTable.add(adv, new GridConstraints(3, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            JIntegerField sumAttr = new JIntegerField();
            sumAttr.setHorizontalAlignment(JTextField.CENTER);
            sumAttr.setEditable(false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(),Font.ITALIC+Font.BOLD,sumAttr.getFont().getSize()+2));
            TAttr.add(sumAttr);
            fate_attributeTable.add(sumAttr, new GridConstraints(4, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null), false);

            JButton buttonDown = new JButton("-");
            buttonDown.setEnabled(false);
            buttonDown.addActionListener(e -> fate_clickDOWN(finalI));
            fate_ButtonsDOWN.add(buttonDown);
            fate_attributeTable.add(buttonDown, new GridConstraints(5, i, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }

        fate_attributeTable.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    void fate_fillTable() {
        for (int i=0;i<10;i++) {
            ((JIntegerField) fate_attributeTable.getComponent(i,4)).setValue(BAttr.get(i).getValue());
            if (sheet.getProf().getAttr(i)) {
                fate_attributeTable.getComponent(i,0).setEnabled(true);
            }
        }
        fate_fate.setValue(sheet.getRace().getFate());
        fate_resilience.setValue(sheet.getRace().getResilience());
        fate_extra.setValue(sheet.getRace().getExtra());
    }
    void fate_clickUP(int i) {
        ((JIntegerField) fate_attributeTable.getComponent(i,3)).increment();
        ((JIntegerField) fate_attributeTable.getComponent(i,4)).increment();

        fate_attrRemain.decrement();
        fate_ButtonsDOWN.get(i).setEnabled(true);
        if ((sheet.getRace().getSize() == Race.Size.NORMAL && i == 2) || i == 3 || i == 8)
            calculateHP();

        if (fate_attrRemain.getValue() == 0)
            fate_ButtonsUP.setEnabled(sheet.getProf().getAllAttr(), false);
    }
    void fate_clickDOWN(int i) {
        ((JIntegerField) fate_attributeTable.getComponent(i,3)).decrement();
        ((JIntegerField) fate_attributeTable.getComponent(i,4)).decrement();

        fate_attrRemain.increment();
        fate_ButtonsUP.setEnabled(sheet.getProf().getAllAttr(), true);
        if (RAttr.get(i).getValue() == 0)
            fate_ButtonsDOWN.get(i).setEnabled(false);

        if ((sheet.getRace().getSize() == Race.Size.NORMAL && i == 2) || i == 3 || i == 8)
            calculateHP();
        if (fate_attrRemain.getValue() == 5)
            fate_ButtonsDOWN.setEnabled(sheet.getProf().getAllAttr(), false);
    }

    void raceskill_createTable() {
        List<GroupSkill> skills = connection.getSkillsByRace(sheet.getRace());

        // Skills - Headers
        raceskill_skillsPanel.add(new JLabel("Basic skills", JLabel.CENTER), new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        raceskill_skillsPanel.add(new JLabel("Advanced skills", JLabel.CENTER), new GridConstraints(0, 4, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);

        int base_itr = 0;
        int adv_itr = 0;
        int column, i;
        for (GroupSkill skill : skills) {
            if (skill.isAdv()) {
                column = 4;
                i = adv_itr++;
                advSkills.add(skill);
            } else {
                column = 0;
                i = base_itr++;
                baseSkills.add(skill);
            }

            raceskill_createComboIfNeeded(skill, i + 1, column);
            column++;

            String attr = skill.getAttr();
            JTextField attrField = new JTextField(attr);
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            raceskill_skillsPanel.add(attrField, new GridConstraints(i + 1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            ListSpinner<Integer> jSpinner = new ListSpinner<>(new Integer[]{0, 3, 5});
            jSpinner.setHorizontalAlignment(JTextField.CENTER);
            jSpinner.setEditable(false);
            raceskill_skillsPanel.add(jSpinner, new GridConstraints(i + 1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            int value = sheet.getSumAttribute(Race.Attributes.find(attr));
            JIntegerField sumField = new JIntegerField(value, "%d", JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            raceskill_skillsPanel.add(sumField, new GridConstraints(i + 1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null), false);

            jSpinner.addChangeListener(e -> raceskill_updatePoints(jSpinner, skill));
            skill.setStartValue(value);
        }
        raceskill_skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    void raceskill_createComboIfNeeded(GroupSkill skill, int row, int column) {
        if (!skill.isGroup()) {
            JTextField textField = new JTextField(skill.getName());
            String tooltip = skill.getBase().getDescr();
            if (tooltip != null)
                textField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            textField.setFocusable(false);
            textField.setEditable(false);
            raceskill_skillsPanel.add(textField, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        } else {
            SearchableJComboBox comboBox = new SearchableJComboBox();
            String tooltip = skill.getBase().getDescr();
            if (tooltip != null)
                comboBox.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
            for (GroupSkill alternateSkill : connection.getAlternateSkillsForGroup(skill.getBase().getID()))
                comboBox.addItem(alternateSkill.getName());
            comboBox.setPreferredSize(new Dimension(comboBox.getSize().width, -1));
            comboBox.refresh(false);
            comboBox.setEditable(true);
            raceskill_skillsPanel.add(comboBox, new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        }
    }
    void raceskill_updatePoints(ListSpinner<Integer> spinner, GroupSkill skill) {
        int now = (int) spinner.getValue();
        int last = (int) spinner.getLastValue();

        if (skill.getAdvValue() != now) {
            skill.setAdvValue(now);

            raceskill_points.put(last, raceskill_points.get(last)-1);
            raceskill_points.put(now, raceskill_points.get(now)+1);
        }
        raceskill_changeColorInColumn(2, 1, baseSkills.size()+1);
        raceskill_changeColorInColumn(6, 1, advSkills.size()+1);
    }
    void raceskill_changeColorInColumn(int col, int rowStart, int rowEnd) {
        for (int i = rowStart; i < rowEnd; i++) {
            JSpinner active = (JSpinner) raceskill_skillsPanel.getComponent(col, i);
            if (raceskill_points.get((int) active.getValue()) > 3) {
                ((JSpinner.DefaultEditor) active.getEditor()).getTextField().setForeground(Color.RED);
            } else {
                ((JSpinner.DefaultEditor) active.getEditor()).getTextField().setForeground(Color.BLACK);
            }
        }
    }

    void racetalent_createTable() {
        List<RaceTalent> talents = connection.getTalentsByRace(sheet.getRace().getID());

        // Constants
        Dimension[] columnDimensions = new Dimension[]{
                new Dimension(200, -1),
                new Dimension(30, -1),
                new Dimension(100, -1)
        };

        // Talents - Header
        raceskill_talentsPanel.add(new JLabel("Talents", JLabel.CENTER), new GridConstraints(0, 0, 1, -1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null), false);
        for (int i=0;i<talents.size();i++) {
            int column = 0;

            racetalent_createComboIfNeeded(talents.get(i), i, column, columnDimensions);
            column++;

            JTextField attrField = new JTextField();
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            racetalent_updateMax(talents.get(i).getTalent(), attrField);

            raceskill_talentsPanel.add(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[1], null), false);
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
                raceskill_randomTalentsPanel.add(nameField, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[0], null), false);

                JTextField attrField = new JTextField();
                attrField.setHorizontalAlignment(JTextField.CENTER);
                attrField.setEditable(false);
                attrField.setFocusable(false);
                raceskill_randomTalentsPanel.add(attrField, new GridConstraints(i, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[1], null), false);
            }
        }

        raceskill_randomTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    void racetalent_createComboIfNeeded(RaceTalent talent, int i, int column, Dimension[] columnDimensions) {
        if (talent.getTalentExcl() == null) {
            JTextField nameField = new JTextField(talent.getTalent().getName());
            nameField.setEditable(false);
            raceskill_talentsPanel.add(nameField, new GridConstraints(i+1, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[0], null), false);
        } else {
            JComboBox<String> comboField = new JComboBox<>();
            comboField.addItem(talent.getTalent().getName());
            comboField.addItem(talent.getTalentExcl().getName());
            comboField.addActionListener(e -> {
                JTextField testField = (JTextField) raceskill_talentsPanel.getComponent(1, i + 1);
                racetalent_updateMax(talent.getAllTalents()[comboField.getSelectedIndex()], testField);
            });
            raceskill_talentsPanel.add(comboField, new GridConstraints(i+1, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, columnDimensions[0], null), false);
        }
    }
    void racetalent_updateMax(GroupTalent talent, JTextField testField) {
        int max;
        try {
            Race.Attributes attr = Race.Attributes.find(talent.getBaseTalent().getMaxLvl());
            max = sheet.getSumAttribute(attr) / 10;
        } catch (Exception ex) {
            max = Integer.parseInt(talent.getBaseTalent().getMaxLvl());
        }
        testField.setText(String.format("%d/%d", talent.getCurrentLvl(), max));
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
    void updateExp(int value) {
        sheet.addExp(value);
        expField.setValue(sheet.getExp());
    }
    void moveToNextTab(int tab) {
        tabbedPane.setEnabledAt(tab + 1, true);
        tabbedPane.setSelectedIndex(tab + 1);
        switch (tab + 1) {
            case 1:
                prof_option4a.addItems(connection.getProfsClasses(sheet.getRace().getID()));
                prof_option4b.addItems(connection.getProfsNames(sheet.getRace().getID(), prof_option4a.getValue()));
                break;
            case 2:
                attr_createTable();
                attr_fillTable();
                break;
            case 3:
                fate_createTable();
                fate_fillTable();
                break;
            case 4:
                raceskill_createTable();
                racetalent_createTable();
                break;
        }
    }

    void createUIComponents() {
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
        int raceAttr = race.getAttr(index);
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