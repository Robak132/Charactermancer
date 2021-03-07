import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import components.CustomJSpinnerModel;
import components.GridPanel;
import components.JIntegerField;
import components.SearchableJComboBox;
import mappings.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CharacterGen {
    JFrame frame;
    JPanel mainPanel;
    Main previous_screen;
    Connection connection;
    CharacterSheet sheet;

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
    List<Profession> profList = new ArrayList<>();
    JTextField[][] prof_options = {
            {prof_option1a, prof_option1b},
            {prof_option2a, prof_option2b},
            {prof_option3a, prof_option3b}
    };
    JButton[] prof_buttons = {
            prof_option1Button, prof_option2Button, prof_option3Button, prof_option4Button
    };
    int prof_maxExp = 50;
    int attr_maxExp = 50;

    private JPanel attributesTable;
    private JButton attr_rollButton;
    private JButton attr_okButton;
    private JIntegerField attr_sumField;
    private JButton attr_rollAllButton;
    private JButton a3PutOwnValuesButton;
    private JButton attr_option1Button;
    private JIntegerField attr_move;
    private JIntegerField attr_hp;
    private int attr_itr=0;

    private JPanel fate_attributeTable;
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
    private JIntegerField raceskill_number3;
    private JIntegerField raceskill_number5;

    private JIntegerField mouse_source = null;
    private Color mouse_color;
    private boolean attr_locked = true;
    private int rollResultNumeric;
    private Race rollRace;

    List<JIntegerField> BAttr = new ArrayList<>();
    List<JIntegerField> RAttr = new ArrayList<>();
    List<JIntegerField> TAttr = new ArrayList<>();

    List<JButton> fate_ButtonsUP = new ArrayList<>();
    List<JButton> fate_ButtonsDOWN = new ArrayList<>();

    public CharacterGen(JFrame _frame, Main _screen, Connection _connection) {
        frame = _frame;
        previous_screen = _screen;
        connection = _connection;
        sheet = new CharacterSheet();

        // Race //
        race_option2Combo.addItems(connection.getRacesNames(), false);

        race_rollButton.addActionListener(e -> {
            Object[] result = getRandomRace();
            rollResultNumeric = (int) result[0];
            rollRace = (Race) result[1];

            race_rollResult.setText("" + rollResultNumeric);
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
                if (Integer.parseInt(race_rollResult.getText()) > 0 && Integer.parseInt(race_rollResult.getText()) <= 100) {
                    rollResultNumeric = Integer.parseInt(race_rollResult.getText());
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
                rollResultNumeric = (int) result[0];
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
                    rollResultNumeric = Integer.parseInt(prof_rollResult.getText());
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
        prof_option4a.addActionListener(e -> prof_option4b.addItems(connection.getProfsNames(sheet.getRace().getID(), prof_option4a.getValue())));

        // Attributes //
        createAttrTable();

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
                attr_okButton.setEnabled(false);
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
            attr_okButton.setEnabled(false);
            attr_option1Button.setEnabled(true);
        });
        attr_okButton.addActionListener(e->{
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

        // Fate & Resolve //
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
            buttonsSetEnable(fate_ButtonsDOWN, false);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });

        // Race skills & Talents //


        // Pane controls //
        tabbedPane.addChangeListener(e -> {
            int tab = tabbedPane.getSelectedIndex();
            String iconpath = String.format("src/main/resources/images/round%d.png", tab + 1);
            ImageIcon icon = new ImageIcon(iconpath);
            imageLabel.setIcon(icon);
        });
        exitButton.addActionListener(e -> {
            frame.setContentPane(previous_screen.mainPanel);
            frame.validate();
        });
    }

    void createAttrTable() {
        String[] columns = {"M", "WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        attributesTable.setLayout(new GridLayoutManager(5, columns.length + 2, new Insets(0, 0, 0, 0), -1, -1));
        attributesTable.add(new Spacer(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
        attributesTable.add(new Spacer(), new GridConstraints(0, columns.length + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));

        for (int i = 0; i < columns.length; i++) {
            JLabel charlabel = new JLabel();
            charlabel.setHorizontalAlignment(JLabel.CENTER);
            charlabel.setHorizontalTextPosition(0);
            charlabel.setText(columns[i]);
            attributesTable.add(charlabel, new GridConstraints(1, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        }

        attr_move = new JIntegerField();
        attr_move.setHorizontalAlignment(JTextField.CENTER);
        attr_move.setEditable(false);
        attr_move.setFont(new Font(attr_move.getFont().getName(),Font.ITALIC+Font.BOLD,attr_move.getFont().getSize()+2));
        attributesTable.add(attr_move, new GridConstraints(2, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null));

        for (int i = 1; i < columns.length-1; i++) {
            JIntegerField baseattr = new JIntegerField();
            baseattr.setHorizontalAlignment(JTextField.CENTER);
            baseattr.setEditable(false);
            BAttr.add(baseattr);

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

            JIntegerField sumattr = new JIntegerField();
            sumattr.setHorizontalAlignment(JTextField.CENTER);
            sumattr.setEditable(false);
            sumattr.setFont(new Font(sumattr.getFont().getName(),Font.ITALIC+Font.BOLD,sumattr.getFont().getSize()+2));
            TAttr.add(sumattr);

            attributesTable.add(baseattr, new GridConstraints(2, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));
            attributesTable.add(attr, new GridConstraints(3, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));
            attributesTable.add(sumattr, new GridConstraints(4, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));
        }

        attr_hp = new JIntegerField();
        attr_hp.setHorizontalAlignment(JTextField.CENTER);
        attr_hp.setEditable(false);
        attr_hp.setFont(new Font(attr_hp.getFont().getName(),Font.ITALIC+Font.BOLD,attr_hp.getFont().getSize()+2));
        attr_hp.setBackground(new Color(176, 224, 230));
        attributesTable.add(attr_hp, new GridConstraints(2, columns.length, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null));
    }
    void createFateTable() {
        List<JIntegerField> values = List.copyOf(TAttr);
        BAttr.clear();
        RAttr.clear();
        TAttr.clear();

        String[] columns = {"M", "WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        fate_attributeTable.setLayout(new GridLayoutManager(6, columns.length + 2, new Insets(0, 0, 0, 0), -1, -1));
        fate_attributeTable.add(new Spacer(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
        fate_attributeTable.add(new Spacer(), new GridConstraints(0, columns.length + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));

        for (int i = 1; i < columns.length; i++) {
            JLabel charlabel = new JLabel();
            charlabel.setHorizontalAlignment(JLabel.CENTER);
            charlabel.setHorizontalTextPosition(0);
            charlabel.setText(columns[i]);
            fate_attributeTable.add(charlabel, new GridConstraints(1, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        }

        attr_hp = new JIntegerField(attr_hp.getValue());
        attr_hp.setHorizontalAlignment(JTextField.CENTER);
        attr_hp.setEditable(false);
        fate_attributeTable.add(attr_hp, new GridConstraints(2, 12, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null));

        for (int i = 1; i < columns.length-1; i++) {
            int finalI = i-1;
            JButton buttonup = new JButton("+");
            buttonup.setEnabled(false);
            buttonup.addActionListener(e -> {
                RAttr.get(finalI).setValue(RAttr.get(finalI).getValue()+1);
                TAttr.get(finalI).setValue(TAttr.get(finalI).getValue()+1);

                fate_attrRemain.decrement();
                fate_ButtonsDOWN.get(finalI).setEnabled(true);
                if ((sheet.getRace().getSize() == Race.SIZE_TINY && finalI == 2) || finalI == 3 || finalI == 8)
                    calculateHP();

                if (fate_attrRemain.getValue() == 0)
                    buttonsSetEnable(fate_ButtonsUP, false);
            });
            fate_ButtonsUP.add(buttonup);
            fate_attributeTable.add(buttonup, new GridConstraints(0, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));

            JIntegerField attr = new JIntegerField(values.get(finalI).getValue());
            attr.setHorizontalAlignment(JTextField.CENTER);
            attr.setEditable(false);
            BAttr.add(attr);
            fate_attributeTable.add(attr, new GridConstraints(2, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));

            JIntegerField adv = new JIntegerField(0);
            adv.setHorizontalAlignment(JTextField.CENTER);
            adv.setEditable(false);
            RAttr.add(adv);
            fate_attributeTable.add(adv, new GridConstraints(3, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));

            JIntegerField sumattr = new JIntegerField();
            sumattr.setHorizontalAlignment(JTextField.CENTER);
            sumattr.setEditable(false);
            sumattr.setFont(new Font(sumattr.getFont().getName(),Font.ITALIC+Font.BOLD,sumattr.getFont().getSize()+2));
            TAttr.add(sumattr);
            fate_attributeTable.add(sumattr, new GridConstraints(4, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null));

            JButton buttondown = new JButton("-");
            buttondown.setEnabled(false);
            buttondown.addActionListener(e -> {
                RAttr.get(finalI).setValue(RAttr.get(finalI).getValue()-1);
                TAttr.get(finalI).setValue(TAttr.get(finalI).getValue()-1);

                fate_attrRemain.increment();
                buttonsSetEnable(fate_ButtonsUP, true);
                if (RAttr.get(finalI).getValue() == 0)
                    fate_ButtonsDOWN.get(finalI).setEnabled(false);

                if ((sheet.getRace().getSize() == Race.SIZE_NORMAL && finalI == 2) || finalI == 3 || finalI == 8)
                    calculateHP();
                if (fate_attrRemain.getValue() == 5)
                    buttonsSetEnable(fate_ButtonsDOWN, false);
            });
            fate_ButtonsDOWN.add(buttondown);
            fate_attributeTable.add(buttondown, new GridConstraints(5, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        }
    }
    void createRaceSkillTable() {
        List<GroupSkill> baseSkills = connection.getBaseSkillsByRace(sheet.getRace().getID());
        List<GroupSkill> advSkills = connection.getAdvSkillsByRace(sheet.getRace().getID());
        List<RaceTalent> talents = connection.getTalentsByRace(sheet.getRace().getID());

        // Skills - Headers
        raceskill_skillsPanel.addAuto(new JLabel("Basic skills", JLabel.CENTER), new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        raceskill_skillsPanel.addAuto(new JLabel("Advanced skills", JLabel.CENTER), new GridConstraints(0, 5, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));

        // Skills - Left side
        for (int i=0;i<baseSkills.size();i++) {
            int column = 0;

            if (baseSkills.get(i).getCustom()==0) {
                JTextField textField = new JTextField(baseSkills.get(i).getName());
                String tooltip = baseSkills.get(i).getBase().getDescr();
                if (tooltip != null)
                    textField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
                textField.setFocusable(false);
                textField.setEditable(false);
                raceskill_skillsPanel.addAuto(textField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
            } else {
                SearchableJComboBox comboBox = new SearchableJComboBox();
                String tooltip = baseSkills.get(i).getBase().getDescr();
                if (tooltip != null)
                    comboBox.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
                for (GroupSkill skill : connection.getGroupSkillsForCustom(baseSkills.get(i).getBase().getID()))
                    comboBox.addItem(skill.getName());
                comboBox.setPreferredSize(new Dimension(comboBox.getSize().width, -1));
                comboBox.refresh(false);
                comboBox.setEditable(true);
                raceskill_skillsPanel.addAuto(comboBox, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
            }

            String attr = baseSkills.get(i).getBase().getAttr();
            JTextField attrField = new JTextField(attr);
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            raceskill_skillsPanel.addAuto(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35,-1), null));

            JSpinner jSpinner = new JSpinner(new CustomJSpinnerModel<>(new Integer[]{0, 3, 5}));
            ((JSpinner.DefaultEditor) jSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
            ((JSpinner.DefaultEditor) jSpinner.getEditor()).getTextField().setEditable(false);
            raceskill_skillsPanel.addAuto(jSpinner, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35,-1), null));

            JIntegerField sumField = new JIntegerField(sheet.getSumAttribute(Race.Attributes.valueOf(attr)));
            sumField.setHorizontalAlignment(JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            raceskill_skillsPanel.addAuto(sumField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null));
        }
        // Skills - Right side
        for (int i=0;i<advSkills.size();i++) {
            int column = 5;

            if (advSkills.get(i).getCustom()==0) {
                JTextField textField = new JTextField(advSkills.get(i).getName());
                String tooltip = advSkills.get(i).getBase().getDescr();
                if (tooltip != null)
                    textField.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
                textField.setForeground(Color.RED);
                textField.setFocusable(false);
                textField.setEditable(false);
                raceskill_skillsPanel.addAuto(textField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(225, -1), null));
            } else {
                SearchableJComboBox comboBox = new SearchableJComboBox();
                String tooltip = advSkills.get(i).getBase().getDescr();
                if (tooltip != null)
                    comboBox.setToolTipText(MultiLineTooltip.splitToolTip(tooltip));
                for (GroupSkill skill : connection.getGroupSkillsForCustom(advSkills.get(i).getBase().getID()))
                    comboBox.addItem(skill.getName());
                comboBox.setPreferredSize(new Dimension(comboBox.getSize().width, -1));
                comboBox.refresh(false);
                comboBox.setEditable(true);
                raceskill_skillsPanel.addAuto(comboBox, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(225, -1), null));
            }

            JTextField attrField = new JTextField(advSkills.get(i).getBase().getAttr());
            attrField.setHorizontalAlignment(JTextField.CENTER);
            attrField.setEditable(false);
            attrField.setFocusable(false);
            raceskill_skillsPanel.addAuto(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35,-1), null));

            JSpinner jSpinner = new JSpinner(new CustomJSpinnerModel<>(new Integer[]{0, 3, 5}));
            ((JSpinner.DefaultEditor) jSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
            ((JSpinner.DefaultEditor) jSpinner.getEditor()).getTextField().setEditable(false);
            raceskill_skillsPanel.addAuto(jSpinner, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35,-1), null));

            JIntegerField sumField = new JIntegerField(0);
            sumField.setHorizontalAlignment(JTextField.CENTER);
            sumField.setEditable(false);
            sumField.setFocusable(false);
            raceskill_skillsPanel.addAuto(sumField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(35, -1), null));
        }

        raceskill_skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

        // Talents - Header
        raceskill_talentsPanel.addAuto(new JLabel("Talents", JLabel.CENTER), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        for (int i=0;i<talents.size();i++) {
            int column = 0;
            int finalI = i;
            Object[] talentArray = new Object[2];

            if (talents.get(i).getTalentExcl() == null) {
                JTextField nameField = new JTextField(talents.get(i).getTalent().getName());
                nameField.setEditable(false);
                raceskill_talentsPanel.addAuto(nameField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null));
            } else {
                JComboBox<String> comboField = new JComboBox<>();
                comboField.addItem(talents.get(i).getTalent().getName());
                comboField.addItem(talents.get(i).getTalentExcl().getName());
                comboField.addActionListener(e -> {
                    JTextField testField = (JTextField) raceskill_talentsPanel.getComponent(1, finalI+1);
                    testField.setText("" + talents.get(finalI).getAllTalents()[comboField.getSelectedIndex()].getBase());
                });
                raceskill_talentsPanel.addAuto(comboField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null));
            }

            JTextField attrField = new JTextField("" + talents.get(i).getTalent().getBase());
            attrField.setEditable(false);
            attrField.setFocusable(false);
            talentArray[1] = attrField;
            raceskill_talentsPanel.addAuto(attrField, new GridConstraints(i+1, column++, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null));
        }

        // Talents - Random Talents
        if (sheet.getRace().getRandomTalents() != 0) {
            raceskill_talentsPanel.addAuto(new JLabel("Random Talents", JLabel.CENTER), new GridConstraints(talents.size()+1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        }

        raceskill_talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }

    void calculateHP() {
        int value = (TAttr.get(3).getValue() / 10) * 2 + TAttr.get(8).getValue() / 10;
        if (sheet.getRace().getSize() == Race.SIZE_NORMAL)
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
    void setBaseValues() {
        attr_move.setValue(sheet.getRace().getM());

        for (int i=0; i<BAttr.size();i++) {
            Integer number = sheet.getRace().getAttr(i);
            BAttr.get(i).setValue(number);

            if ((sheet.getRace().getSize()==Race.SIZE_NORMAL && i == 2) || i == 3 || i == 8) {
                BAttr.get(i).setBackground(new Color(176, 224, 230));
                RAttr.get(i).setBackground(new Color(176, 224, 230));
                TAttr.get(i).setBackground(new Color(176, 224, 230));
            }
        }
        for (int i=0;i<RAttr.size();i++) {
            if (sheet.getProf().getAttr(i) == 1) {
                BAttr.get(i).setForeground(new Color(0,128,0));
                RAttr.get(i).setForeground(new Color(0,128,0));
                TAttr.get(i).setForeground(new Color(0,128,0));
            }
        }
    }
    void setTotalValues() {
        for (int i=0;i<10;i++) {
            TAttr.get(i).setValue(BAttr.get(i).getValue());
            if (sheet.getProf().getAttr(i)==1)
                fate_ButtonsUP.get(i).setEnabled(true);
        }
        fate_fate.setValue(sheet.getRace().getFate());
        fate_resilience.setValue(sheet.getRace().getResilience());
        fate_extra.setValue(sheet.getRace().getExtra());
    }
    void buttonsSetEnable(List<JButton> list, boolean bool) {
        for (int i=0;i<list.size();i++) {
            if (sheet.getProf().getAttr(i)==1)
                list.get(i).setEnabled(bool);
        }
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
                setBaseValues();
                break;
            case 3:
                createFateTable();
                setTotalValues();
                break;
            case 4:
                createRaceSkillTable();
                break;
        }
    }

    void createUIComponents() {
        fate_attrRemain = new JIntegerField(5);
        raceskill_number3 = new JIntegerField(3, "%d/3");
        raceskill_number5 = new JIntegerField(3, "%d/3");
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
    void getRaceSkills(Race race) {
        List<GroupSkill> baseSkills = connection.getBaseSkillsByRace(race.getID());
        List<GroupSkill> advSkills = connection.getAdvSkillsByRace(race.getID());
    }
}