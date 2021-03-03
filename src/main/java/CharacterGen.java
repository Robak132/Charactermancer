import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import components.JIntegerField;
import components.SearchableJComboBox;
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
    private JIntegerField expField;
    private JTabbedPane tabbedPane;
    private JButton exitButton;
    private JLabel rollLabel;

    private JTextField race_rollResult;
    private JButton race_option2Button;
    private SearchableJComboBox race_option2Combo;
    private JButton race_option1Button;
    private JTextField race_option1;
    private JButton race_rollButton;
    private JButton race_OKButton;

    private JButton prof_option1Button;
    private JButton prof_OKButton;
    private JButton prof_rollButton;
    private JTextField prof_rollResult;
    private JTextField prof_option1a;
    private JTextField prof_option1b;
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
    JTextField[][] prof_Options = {
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

    private JIntegerField mouse_source = null;
    Color mouse_color;
    boolean attr_locked = true;

    int rollResultNumeric;
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
        race_option2Combo.bindItems(connection.getRacesNames());

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

                prof_rollResult.setText("" + rollResultNumeric);
            } while (profList.contains(rollProf));
            profList.add(rollProf);
            prof_Options[profList.size() - 1][0].setText(rollProf.getCareer());
            prof_Options[profList.size() - 1][1].setText(rollProf.getProfession());
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
                    Profession prof = connection.getProfFromTable(sheet.getRace().getId(), rollResultNumeric);
                    if (profList.contains(prof)) {
                        rollLabel.setVisible(true);
                        throw new Exception();
                    } else {
                        rollLabel.setVisible(false);
                        profList.add(prof);
                        prof_Options[profList.size() - 1][0].setText(prof.getCareer());
                        prof_Options[profList.size() - 1][1].setText(prof.getProfession());
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
        prof_option4a.addActionListener(e -> prof_option4b.bindItems(connection.getProfsNames(sheet.getRace().getId(), prof_option4a.getValue())));

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

    void calculateTotal() {
        for (int i=0;i<10;i++)
            TAttr.get(i).setValue(BAttr.get(i).getValue() + RAttr.get(i).getValue());
        calculateHP();
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
                prof_option4a.bindItems(connection.getProfsClasses(sheet.getRace().getId()));
                prof_option4b.bindItems(connection.getProfsNames(sheet.getRace().getId(), prof_option4a.getValue()));
                break;
            case 2:
                setBaseValues();
                break;
            case 3:
                createFateTable();
                setTotalValues();
                break;
            case 4:
                break;
        }
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
        returns[1] = connection.getProfFromTable(race.getId(), numeric);
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
}