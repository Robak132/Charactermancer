import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import mappings.Profession;
import mappings.Race;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CharacterGen {
    JFrame frame;
    JPanel mainPanel;
    Main previous_screen;
    LanguagePack languagePack;
    Connection connection;
    CharacterSheet sheet;

    private JLabel imageLabel;
    private JTextField expField;
    private JTabbedPane tabbedPane;
    private JButton exitButton;
    private JLabel rollLabel;

    private JTextField race_RollResult;
    private JButton race_Option2Button;
    private SearchableJComboBox race_Option2Combo;
    private JButton race_Option1Button;
    private JTextField race_Option1;
    private JButton race_RollButton;
    private JButton race_OKButton;

    private JButton prof_Option1Button;
    private JButton prof_OKButton;
    private JButton prof_RollButton;
    private JTextField prof_RollResult;
    private JTextField prof_Option1a;
    private JTextField prof_Option1b;
    private JTextField prof_Option2a;
    private JTextField prof_Option2b;
    private JButton prof_Option2Button;
    private JTextField prof_Option3a;
    private JTextField prof_Option3b;
    private JButton prof_Option3Button;
    private SearchableJComboBox prof_Option4a;
    private SearchableJComboBox prof_Option4b;
    private JButton prof_Option4Button;
    List<Profession> profList = new ArrayList<>();
    JTextField[][] prof_Options = {
            {prof_Option1a, prof_Option1b},
            {prof_Option2a, prof_Option2b},
            {prof_Option3a, prof_Option3b}
    };
    JButton[] prof_Buttons = {
            prof_Option1Button, prof_Option2Button, prof_Option3Button, prof_Option4Button
    };
    int prof_maxexp = 50;

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
    private JButton OKButton;

    private JIntegerField mouse_source = null;
    Color mouse_color;
    boolean attr_locked = false;

    int rollResultNumeric, rollResultNumeric1;
    Race rollRace;

    List<JIntegerField> BAttr = new ArrayList<>();
    List<JIntegerField> RAttr = new ArrayList<>();
    List<JIntegerField> TAttr = new ArrayList<>();

    List<JButton> fate_ButtonsUP = new ArrayList<>();
    List<JButton> fate_ButtonsDOWN = new ArrayList<>();

    public CharacterGen(JFrame _frame, Main _screen, LanguagePack _languagepack, Connection _connection) {
        frame = _frame;
        previous_screen = _screen;
        languagePack = _languagepack;
        connection = _connection;
        sheet = new CharacterSheet();

        // Race //
        prepareRaces();

        race_RollButton.addActionListener(e -> {
            rollResultNumeric = Dice.randomDice(1, 100);
            race_RollResult.setText("" + rollResultNumeric);
            rollRace = connection.getRaceFromTable(rollResultNumeric);
            race_Option1.setText(rollRace.getName());

            race_RollButton.setEnabled(false);
            race_RollResult.setEditable(false);
            race_OKButton.setEnabled(false);

            race_Option1.setEnabled(true);
            race_Option1Button.setEnabled(true);
            race_Option2Combo.setEnabled(true);
            race_Option2Button.setEnabled(true);
        });
        race_OKButton.addActionListener(e -> {
            try {
                if (Integer.parseInt(race_RollResult.getText()) > 0 && Integer.parseInt(race_RollResult.getText()) <= 100) {
                    rollResultNumeric = Integer.parseInt(race_RollResult.getText());
                    rollRace = connection.getRaceFromTable(rollResultNumeric);
                    race_Option1.setText(rollRace.getName());

                    race_RollButton.setEnabled(false);
                    race_RollResult.setEditable(false);
                    race_OKButton.setEnabled(false);

                    race_Option1.setEnabled(true);
                    race_Option1Button.setEnabled(true);
                    race_Option2Combo.setEnabled(true);
                    race_Option2Button.setEnabled(true);
                }
            } catch (Exception ex) {
                race_RollResult.setText("");
            }
        });

        race_Option1Button.addActionListener(e -> {
            race_Option2Combo.setSelectedItem(race_Option1.getText());
            sheet.setRace(rollRace);
            sheet.addExp(20);
            updateExp();

            race_Option1Button.setEnabled(false);
            race_Option1.setEditable(false);
            race_Option2Button.setEnabled(false);
            race_Option2Combo.setNotEditable();

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        race_Option2Button.addActionListener(e -> {
            try {
                race_Option1.setText((String) race_Option2Combo.getSelectedItem());
                sheet.setRace(connection.getRace((String) race_Option2Combo.getSelectedItem()));
                updateExp();

                race_Option1Button.setEnabled(false);
                race_Option1.setEditable(false);
                race_Option2Button.setEnabled(false);
                race_Option2Combo.setNotEditable();

                moveToNextTab(tabbedPane.getSelectedIndex());
            } catch (Exception ignored) {
            }
        });

        // Profession //
        prof_RollButton.addActionListener(e -> {
            Profession prof;
            do {
                rollResultNumeric1 = Dice.randomDice(1, 100);
                prof_RollResult.setText("" + rollResultNumeric1);
                prof = connection.getProfFromTable(sheet.getRace().getId(), rollResultNumeric1);
            } while (profList.contains(prof));
            profList.add(prof);
            prof_Options[profList.size() - 1][0].setText(prof.getCareer());
            prof_Options[profList.size() - 1][1].setText(prof.getProfession());
            prof_Buttons[profList.size() - 1].setEnabled(true);
            if (profList.size() > 1)
                prof_maxexp = 25;
            if (profList.size() >= 3) {
                prof_RollButton.setEnabled(false);
                prof_RollResult.setEditable(false);
                prof_OKButton.setEnabled(false);

                prof_Option4Button.setEnabled(true);
                prof_Option4a.setEnabled(true);
                prof_Option4b.setEnabled(true);
            }
        });
        prof_OKButton.addActionListener(e -> {
            try {
                if (Integer.parseInt(prof_RollResult.getText()) > 0 && Integer.parseInt(prof_RollResult.getText()) <= 100) {
                    rollResultNumeric1 = Integer.parseInt(prof_RollResult.getText());
                    Profession prof = connection.getProfFromTable(sheet.getRace().getId(), rollResultNumeric1);
                    if (profList.contains(prof)) {
                        rollLabel.setVisible(true);
                        throw new Exception();
                    } else {
                        rollLabel.setVisible(false);
                        profList.add(prof);
                        prof_Options[profList.size() - 1][0].setText(prof.getCareer());
                        prof_Options[profList.size() - 1][1].setText(prof.getProfession());
                        prof_Buttons[profList.size() - 1].setEnabled(true);
                    }
                    if (profList.size() >= 3) {
                        prof_RollButton.setEnabled(false);
                        prof_RollResult.setEditable(false);
                        prof_OKButton.setEnabled(false);
                    }
                }
            } catch (Exception ex) {
                prof_RollResult.setText("");
            }
        });
        prof_Option1Button.addActionListener(e -> {
            sheet.setProf(profList.get(0));
            updateExp(prof_maxexp);

            prof_RollButton.setEnabled(false);
            prof_RollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_Buttons)
                button.setEnabled(false);
            prof_Option4a.setNotEditable();
            prof_Option4b.setNotEditable();

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_Option2Button.addActionListener(e -> {
            sheet.setProf(profList.get(1));
            updateExp(prof_maxexp);

            prof_RollButton.setEnabled(false);
            prof_RollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_Buttons)
                button.setEnabled(false);
            prof_Option4a.setNotEditable();
            prof_Option4b.setNotEditable();

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_Option3Button.addActionListener(e -> {
            sheet.setProf(profList.get(2));
            updateExp(prof_maxexp);

            prof_RollButton.setEnabled(false);
            prof_RollResult.setEditable(false);
            prof_OKButton.setEnabled(false);
            for (JButton button : prof_Buttons)
                button.setEnabled(false);
            prof_Option4a.setNotEditable();
            prof_Option4b.setNotEditable();

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_Option4a.addActionListener(e -> prof_Option4b.bindItems(connection.getProfsNames(sheet.getRace().getId(), prof_Option4a.getValue())));

        // Attributes
        createAttrTable();

        attr_rollButton.addActionListener(e -> {
            int number = Dice.randomDice(2, 10);
            RAttr.get(attr_itr).setValue(number);
            RAttr.get(attr_itr).setEditable(false);

            attr_sumField.increment(number);
            calculateTotal(attr_itr);
            attr_itr++;

            if (attr_itr==10) {
                attr_rollButton.setEnabled(false);
                attr_rollAllButton.setEnabled(false);
                attr_okButton.setEnabled(false);
            }
        });
        attr_rollAllButton.addActionListener(e -> {
            while (attr_itr < 10) {
                int number = Dice.randomDice(2, 10);
                RAttr.get(attr_itr).setValue(number);
                RAttr.get(attr_itr).setEditable(false);

                attr_sumField.setValue(attr_sumField.getValue()+number);
                calculateTotal(attr_itr);
                attr_itr++;
            }
            attr_rollButton.setEnabled(false);
            attr_rollAllButton.setEnabled(false);
            attr_okButton.setEnabled(false);
        });
        attr_option1Button.addActionListener(e -> {
//            sheet.setBAttributes(TAttr_num);
//            sheet.setMove(attr_move_num);
//            sheet.setMaxHP(attr_hp_num);
//            sheet.setHP();

            attr_option1Button.setEnabled(false);
            attr_locked = true;
            updateExp(50);
            moveToNextTab(tabbedPane.getSelectedIndex());
        });

        // Fate & Resolve
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

    void calculateTotal(int index) {
        TAttr.get(index).setValue(BAttr.get(index).getValue() + RAttr.get(index).getValue());
        if (attr_itr>=9) {
            int value = (TAttr.get(3).getValue() / 10) * 2 + TAttr.get(8).getValue() / 10;
            if (sheet.getRace().getSize() == Race.SIZE_NORMAL)
                value += TAttr.get(2).getValue() / 10;

            attr_hp.setValue(value);
        }
    }
    void calculateTotal() {
        for (int i=0;i<10;i++)
            calculateTotal(i);
    }
    void prepareRaces() {
        for (Object race: connection.getRaces())
            race_Option2Combo.addItem(((Race) race).getName());
    }
    void createAttrTable() {
        String[] columns = {"M", "WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        attributesTable.setLayout(new GridLayoutManager(5, columns.length + 2, new Insets(0, 0, 0, 0), -1, -1));
        attributesTable.add(new Spacer(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        attributesTable.add(new Spacer(), new GridConstraints(0, columns.length + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

        for (int i = 0; i < columns.length; i++) {
            JLabel charlabel = new JLabel();
            charlabel.setHorizontalAlignment(0);
            charlabel.setHorizontalTextPosition(0);
            charlabel.setText(columns[i]);
            attributesTable.add(charlabel, new GridConstraints(1, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        }

        attr_move = new JIntegerField();
        attr_move.setHorizontalAlignment(0);
        attr_move.setEditable(false);
        attr_move.setFont(new Font(attr_move.getFont().getName(),Font.ITALIC+Font.BOLD,attr_move.getFont().getSize()+2));
        attributesTable.add(attr_move, new GridConstraints(2, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null, 0, false));

        for (int i = 1; i < columns.length-1; i++) {
            JIntegerField baseattr = new JIntegerField();
            baseattr.setHorizontalAlignment(0);
            baseattr.setEditable(false);
            BAttr.add(baseattr);

            JIntegerField attr = new JIntegerField();
            attr.setHorizontalAlignment(0);
            attr.setFocusable(false);
            RAttr.add(attr);
            attr.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (attr_itr == 10 && !attr_locked) {
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
                        calculateTotal();
                    }
                }
            });

            JIntegerField sumattr = new JIntegerField();
            sumattr.setHorizontalAlignment(0);
            sumattr.setEditable(false);
            sumattr.setFont(new Font(sumattr.getFont().getName(),Font.ITALIC+Font.BOLD,sumattr.getFont().getSize()+2));
            TAttr.add(sumattr);

            attributesTable.add(baseattr, new GridConstraints(2, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
            attributesTable.add(attr, new GridConstraints(3, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
            attributesTable.add(sumattr, new GridConstraints(4, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
        }

        attr_hp = new JIntegerField();
        attr_hp.setHorizontalAlignment(0);
        attr_hp.setEditable(false);
        attr_hp.setFont(new Font(attr_hp.getFont().getName(),Font.ITALIC+Font.BOLD,attr_hp.getFont().getSize()+2));
        attr_hp.setBackground(new Color(176, 224, 230));
        attributesTable.add(attr_hp, new GridConstraints(2, columns.length, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null, 0, false));
    }
    void createFateTable() {
        List<JIntegerField> values = List.copyOf(TAttr);
        BAttr.clear();
        RAttr.clear();
        TAttr.clear();

        String[] columns = {"M", "WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        fate_attributeTable.setLayout(new GridLayoutManager(6, columns.length + 2, new Insets(0, 0, 0, 0), -1, -1));
        fate_attributeTable.add(new Spacer(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        fate_attributeTable.add(new Spacer(), new GridConstraints(0, columns.length + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

        for (int i = 1; i < columns.length; i++) {
            JLabel charlabel = new JLabel();
            charlabel.setHorizontalAlignment(0);
            charlabel.setHorizontalTextPosition(0);
            charlabel.setText(columns[i]);
            fate_attributeTable.add(charlabel, new GridConstraints(1, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        }

        attr_hp = new JIntegerField(attr_hp.getValue());
        attr_hp.setHorizontalAlignment(0);
        attr_hp.setEditable(false);
        fate_attributeTable.add(attr_hp, new GridConstraints(2, 12, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(30, -1), null, 0, false));

        for (int i = 1; i < columns.length-1; i++) {
            int finalI = i-1;

            JButton buttonup = new JButton("+");
            buttonup.setEnabled(false);
            buttonup.addActionListener(e -> {
                RAttr.get(finalI).setValue(RAttr.get(finalI).getValue()+1);
                TAttr.get(finalI).setValue(TAttr.get(finalI).getValue()+1);

                fate_attrRemain.decrement();
                fate_ButtonsDOWN.get(finalI).setEnabled(true);

                if ((sheet.getRace().getSize() == Race.SIZE_NORMAL && finalI == 2) || finalI == 3 || finalI == 8)
                    calculateHP();

                if (fate_attrRemain.getValue() == 0)
                    buttonsSetEnable(fate_ButtonsUP, false);
            });
            fate_ButtonsUP.add(buttonup);
            fate_attributeTable.add(buttonup, new GridConstraints(0, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

            JIntegerField attr = new JIntegerField(values.get(finalI).getValue());
            attr.setHorizontalAlignment(0);
            attr.setEditable(false);
            BAttr.add(attr);
            fate_attributeTable.add(attr, new GridConstraints(2, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));

            JIntegerField adv = new JIntegerField(0);
            adv.setHorizontalAlignment(0);
            adv.setEditable(false);
            RAttr.add(adv);
            fate_attributeTable.add(adv, new GridConstraints(3, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));

            JIntegerField sumattr = new JIntegerField();
            sumattr.setHorizontalAlignment(0);
            sumattr.setEditable(false);
            sumattr.setFont(new Font(sumattr.getFont().getName(),Font.ITALIC+Font.BOLD,sumattr.getFont().getSize()+2));
            TAttr.add(sumattr);
            fate_attributeTable.add(sumattr, new GridConstraints(4, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));

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
            fate_attributeTable.add(buttondown, new GridConstraints(5, i + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        }
    }

    void calculateHP() {
        int value = (TAttr.get(3).getValue() / 10) * 2 + TAttr.get(8).getValue() / 10;
        if (sheet.getRace().getSize() == Race.SIZE_NORMAL)
            value += TAttr.get(2).getValue() / 10;
        attr_hp.setValue(value);
    }
    void moveToNextTab(int tab) {
        tabbedPane.setEnabledAt(tab + 1, true);
        tabbedPane.setSelectedIndex(tab + 1);
        switch (tab + 1) {
            case 1:
                prof_Option4a.bindItems(connection.getProfsClasses(sheet.getRace().getId()));
                prof_Option4b.bindItems(connection.getProfsNames(sheet.getRace().getId(), prof_Option4a.getValue()));
                break;
            case 2:
                setBaseValues();
                break;
            case 3:
                createFateTable();
                setTotalValues();
                break;
        }
    }
    void updateExp(int value) {
        sheet.addExp(value);
        expField.setText("" + sheet.getExp());
    }
    void updateExp() {
        expField.setText("" + sheet.getExp());
    }
    void setBaseValues() {
        attr_move.setValue(sheet.getRace().getM());

        for (int i = 0; i<BAttr.size();i++) {
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
        fate_fate.setText(String.valueOf(sheet.getRace().getFate()));
        fate_resilience.setText(String.valueOf(sheet.getRace().getResilience()));
        fate_extra.setText(String.valueOf(sheet.getRace().getExtra()));
    }
    void buttonsSetEnable(List<JButton> list, boolean bool) {
        for (int i=0;i<10;i++) {
            if (sheet.getProf().getAttr(i)==1)
                list.get(i).setEnabled(bool);
        }
    }
}