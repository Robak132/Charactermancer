import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import mappings.Profession;
import mappings.Race;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class CharacterGen {
    JFrame frame;
    JPanel mainPanel;
    Main previous_screen;
    LanguagePack languagePack;
    Connection connection;
    CharacterSheet sheet;

    private JTextField expField;
    private JButton prof_Option1Button;
    private JTabbedPane tabbedPane;
    private JTextField race_RollResult;
    private JButton race_Option2Button;
    private JComboBox<String> race_Option2Combo;
    private JButton race_Option1Button;
    private JTextField race_Option1;
    private JButton exitButton;
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
    private JLabel rollLabel;
    private JComboBox<String> prof_Option4b;
    private JComboBox<String> prof_Option4a;
    private JButton prof_Option4Button;
    private JButton race_RollButton;
    private JButton race_OKButton;
    private JLabel imageLabel;
    private JPanel Sheet;
    private JButton rollButton;
    private JButton okButton;
    private JLabel charcreation;
    private JTextField textField1;
    private JTextField textField2;

    int rollResultNumeric, rollResultNumeric1, rollResultNumeric2;
    Race rollRace;
    List<Profession> profList = new ArrayList<>();
    JTextField[][] prof_Options = {
            {prof_Option1a, prof_Option1b},
            {prof_Option2a, prof_Option2b},
            {prof_Option3a, prof_Option3b}
    };
    JButton[] prof_Buttons = {
            prof_Option1Button, prof_Option2Button, prof_Option3Button, prof_Option4Button
    };

    List<JTextField> AAttr, BAttr, TAttr;
    List<JButton> PButtons, MButtons;
    List<Integer> AAttr_num, BAttr_num, TAttr_num;
    int aattr_it = 0;

    public CharacterGen(JFrame _frame, Main _screen, LanguagePack _languagepack, Connection _connection, CharacterSheet _sheet) {
        frame = _frame;
        previous_screen = _screen;
        languagePack = _languagepack;
        connection = _connection;
        sheet = _sheet;

        // Race //
        prepareRaces();

        race_RollButton.addActionListener(e -> {
            rollResultNumeric = randomIntInRange(1, 100);
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
            sheet.setRase(rollRace);
            sheet.addExp(20);
            updateExp();

            race_Option1Button.setEnabled(false);
            race_Option1.setEditable(false);
            race_Option2Button.setEnabled(false);
            setJComboBoxReadOnly(race_Option2Combo);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        race_Option2Button.addActionListener(e -> {
            try {
                race_Option1.setText(race_Option2Combo.getSelectedItem().toString());
                sheet.setRase(connection.getRace(race_Option2Combo.getSelectedItem().toString()));
                updateExp();

                race_Option1Button.setEnabled(false);
                race_Option1.setEditable(false);
                race_Option2Button.setEnabled(false);
                setJComboBoxReadOnly(race_Option2Combo);

                moveToNextTab(tabbedPane.getSelectedIndex());
            } catch (Exception ignored) {
            }
        });

        // Profession //
        prof_RollButton.addActionListener(e -> {
            Profession prof;
            do {
                rollResultNumeric1 = randomIntInRange(1, 100);
                prof_RollResult.setText("" + rollResultNumeric1);
                prof = connection.getProfFromTable(sheet.getRase().getId(), rollResultNumeric1);
            } while (profList.contains(prof));
            profList.add(prof);
            prof_Options[profList.size() - 1][0].setText(prof.getCareer());
            prof_Options[profList.size() - 1][1].setText(prof.getProfession());
            prof_Buttons[profList.size() - 1].setEnabled(true);

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
                    Profession prof = connection.getProfFromTable(sheet.getRase().getId(), rollResultNumeric1);
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
            sheet.addExp(50);
            updateExp();

            for (JButton button : prof_Buttons)
                button.setEnabled(false);
            setJComboBoxReadOnly(prof_Option4a);
            setJComboBoxReadOnly(prof_Option4b);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });

        prof_Option2Button.addActionListener(e -> {
            sheet.setProf(profList.get(1));
            sheet.addExp(25);
            updateExp();

            for (JButton button : prof_Buttons)
                button.setEnabled(false);
            setJComboBoxReadOnly(prof_Option4a);
            setJComboBoxReadOnly(prof_Option4b);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });
        prof_Option3Button.addActionListener(e -> {
            sheet.setProf(profList.get(2));
            sheet.addExp(25);
            updateExp();

            for (JButton button : prof_Buttons)
                button.setEnabled(false);
            setJComboBoxReadOnly(prof_Option4a);
            setJComboBoxReadOnly(prof_Option4b);

            moveToNextTab(tabbedPane.getSelectedIndex());
        });

        prof_Option4a.addActionListener(e -> {
            if (prof_Option4a.getSelectedItem().toString().equals("---")) {
                prof_Option4b.removeAllItems();
                List list = connection.getProfs(sheet.getRase().getId());
                prof_Option4b.addItem("---");
                fillprofs(list);
            } else {
                prof_Option4b.removeAllItems();
                List list = connection.getProfs(sheet.getRase().getId(), prof_Option4a.getSelectedItem().toString());
                fillprofs(list);
            }
        });
        prof_Option4Button.addActionListener(e -> {
            if (!prof_Option4b.getSelectedItem().toString().equals("---")) {
                Profession prof = connection.getProf(prof_Option4b.getSelectedItem().toString(), 1);
                sheet.setProf(prof);

                for (JButton button : prof_Buttons)
                    button.setEnabled(false);
                setJComboBoxReadOnly(prof_Option4a);
                setJComboBoxReadOnly(prof_Option4b);

                moveToNextTab(tabbedPane.getSelectedIndex());
            }
        });

        // Attributes
        String[] columns = {"M", "WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        List<JButton> buttonsUP = new ArrayList<>();
        List<JTextField> textFields = new ArrayList<>();
        List<JButton> buttonsDOWN = new ArrayList<>();
        Sheet.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        for (int i = 0; i < columns.length; i++) {
            c.gridx = i;
            c.gridy = 0;
            c.weightx = 1.0;
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(5, 5, 5, 5);
            JLabel label = new JLabel(columns[i], SwingConstants.CENTER);
            label.setSize(100, 10);
            Sheet.add(label, c);

            if (i != 0 && i != columns.length - 1) {
                c.gridy = 1;
                JButton temp = new JButton("+");
                temp.setEnabled(false);
                buttonsUP.add(temp);
                Sheet.add(temp, c);

                c.gridy = 2;
                JTextField textField3 = new JTextField("");
                textField3.setHorizontalAlignment(JTextField.CENTER);
                textField3.setEditable(false);
                textField3.setSize(50, 10);
                textFields.add(textField3);
                Sheet.add(textField3, c);

                c.gridy = 3;
                JTextField textField2 = new JTextField("");
                textField2.setHorizontalAlignment(JTextField.CENTER);
                textField2.setSize(50, 10);
                textFields.add(textField2);
                Sheet.add(textField2, c);

                c.gridy = 4;
                temp = new JButton("-");
                temp.setEnabled(false);
                buttonsDOWN.add(temp);
                Sheet.add(temp, c);
            }
        }
        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 2;

        JTextField textField = new JTextField("");
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setSize(50, 10);
        textFields.add(textField);
        Sheet.add(textField, c);

        c.gridx = columns.length - 1;
        c.gridy = 2;
        c.gridheight = 2;

        textField = new JTextField("");
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setSize(50, 10);
        textFields.add(textField);
        Sheet.add(textField, c);

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

    void fillCombos() {
        List list = connection.getProfs(sheet.getRase().getId());
        prof_Option4a.addItem("---");
        prof_Option4b.addItem("---");
        for (Object prof : list) {
            Profession temp = (Profession) prof;
            if (((DefaultComboBoxModel) prof_Option4a.getModel()).getIndexOf(temp.getClss()) == -1)
                prof_Option4a.addItem(temp.getClss());
            if (((DefaultComboBoxModel) prof_Option4b.getModel()).getIndexOf(temp.getProfession()) == -1)
                prof_Option4b.addItem(temp.getProfession());
        }
    }

    void fillprofs(List list) {
        for (Object prof : list) {
            Profession temp = (Profession) prof;
            if (((DefaultComboBoxModel) prof_Option4b.getModel()).getIndexOf(temp.getProfession()) == -1)
                prof_Option4b.addItem(temp.getProfession());
        }
    }

    void prepareRaces() {
        sheet.setExp(0);
        race_Option2Combo.addItem(languagePack.localise("human"));
        race_Option2Combo.addItem(languagePack.localise("halfling"));
        race_Option2Combo.addItem(languagePack.localise("dwarf"));
        race_Option2Combo.addItem(languagePack.localise("gnome"));
        race_Option2Combo.addItem(languagePack.localise("highelf"));
        race_Option2Combo.addItem(languagePack.localise("woodelf"));
    }

    void moveToNextTab(int tab) {
        tabbedPane.setEnabledAt(tab + 1, true);
        tabbedPane.setSelectedIndex(tab + 1);
        switch (tab + 1) {
            case 1:
                fillCombos();
                break;
            case 2:
                setBaseValues();
                break;
        }
    }

    void updateExp() {
        expField.setText("" + sheet.getExp());
    }

    void setBaseValues() {
    }

    public int randomIntInRange(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }

    private void setJComboBoxReadOnly(JComboBox jcb) {
        JTextField jtf = (JTextField) jcb.getEditor().getEditorComponent();
        jtf.setEditable(false);

        MouseListener[] mls = jcb.getMouseListeners();
        for (MouseListener listener : mls)
            jcb.removeMouseListener(listener);

        Component[] comps = jcb.getComponents();
        for (Component c : comps) {
            if (c instanceof AbstractButton) {
                c.setEnabled(false);

                MouseListener[] mls2 = c.getMouseListeners();
                for (MouseListener listener : mls2)
                    c.removeMouseListener(listener);
            }
        }
    }

}