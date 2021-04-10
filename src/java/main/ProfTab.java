package main;

import components.JIntegerField;
import components.SearchableComboBox;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mappings.Profession;
import mappings.ProfessionCareer;
import mappings.ProfessionClass;
import org.apache.logging.log4j.LogManager;

public class ProfTab {
    private CharacterSheet sheet;
    private CharacterGen parent;

    private JPanel mainPanel;
    private JButton profOKButton;
    private JButton profRollButton;
    private JIntegerField profRollResult;
    private JTextField profOption1a;
    private JTextField profOption1b;
    private JButton profOption1Button;
    private JTextField profOption2a;
    private JTextField profOption2b;
    private JButton profOption2Button;
    private JTextField profOption3a;
    private JTextField profOption3b;
    private JButton profOption3Button;
    private SearchableComboBox profOption4a;
    private SearchableComboBox profOption4b;
    private JButton profOption4Button;

    private final JTextField[][] profOptions = {
            {profOption1a, profOption1b},
            {profOption2a, profOption2b},
            {profOption3a, profOption3b}
    };
    private final JButton[] profButtons = {
            profOption1Button, profOption2Button, profOption3Button, profOption4Button
    };

    private List<ProfessionClass> professionClasses;
    private List<ProfessionCareer> professionCareers;
    private final List<Profession> chosenProfessions = new ArrayList<>();

    public ProfTab() {
        // Needed for GUI Designer
    }
    public ProfTab(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        initialise(parent, sheet, connection);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        this.sheet = sheet;
        this.parent = parent;
        professionClasses = connection.getProfessionClasses();
        professionCareers = connection.getProfessionCareers();
        fillCombos();

        profRollButton.addActionListener(e -> {
            Object[] result = CharacterGen.getRandomProf(connection, sheet.getRace());
            Profession rollProf = (Profession) result[1];
            int rollResultNumeric = (int) result[0];
            while (chosenProfessions.contains(rollProf)) {
                LogManager.getLogger(getClass().getName()).debug(String.format("%d doubled, changing to %d", rollResultNumeric, rollResultNumeric % 100 + 1));
                rollResultNumeric = rollResultNumeric % 100 + 1;
                rollProf = connection.getProfFromTable(sheet.getRace(), rollResultNumeric);
            }
            chosenProfessions.add(rollProf);
            profRollResult.setValue(rollResultNumeric);

            profOptions[chosenProfessions.size() - 1][0].setText(rollProf.getCareer().getProfessionClass().getName());
            profOptions[chosenProfessions.size() - 1][1].setText(rollProf.getCareer().getName());
            profButtons[chosenProfessions.size() - 1].setEnabled(true);

            if (chosenProfessions.size() >= 3) {
                profRollButton.setEnabled(false);
                profRollResult.setEditable(false);
                profOKButton.setEnabled(false);

                profOption4Button.setEnabled(true);
                profOption4a.setEnabled(true);
                profOption4b.setEnabled(true);
            }
        });
        profRollButton.setMnemonic(KeyEvent.VK_R);
        profOKButton.addActionListener(e -> {
            int rollResultNumeric = profRollResult.getValue();
            if (rollResultNumeric <= 0 || rollResultNumeric > 100) {
                profRollResult.setText("");
                return;
            }

            Profession prof = connection.getProfFromTable(sheet.getRace(), rollResultNumeric);
            if (chosenProfessions.contains(prof)) {
                // TODO: Make "Roll doubled" dialog or do sth better
            } else {
                chosenProfessions.add(prof);
                profOptions[chosenProfessions.size() - 1][0].setText(prof.getCareer().getName());
                profOptions[chosenProfessions.size() - 1][1].setText(prof.getName());
                profButtons[chosenProfessions.size() - 1].setEnabled(true);

                if (chosenProfessions.size() >= 3) {
                    profRollButton.setEnabled(false);
                    profRollResult.setEditable(false);
                    profOKButton.setEnabled(false);
                }
            }
        });
        profOKButton.setMnemonic(KeyEvent.VK_O);

        profOption1Button.addActionListener(e -> lockAll(0));
        profOption1Button.setMnemonic(KeyEvent.VK_1);
        profOption2Button.addActionListener(e -> lockAll(1));
        profOption2Button.setMnemonic(KeyEvent.VK_2);
        profOption3Button.addActionListener(e -> lockAll(2));
        profOption3Button.setMnemonic(KeyEvent.VK_3);

        profOption4a.addActionListener(e -> comboChangedClass());
        profOption4Button.addActionListener(e -> comboChangedClass());
    }

    private void lockAll(int index) {
        sheet.setProfession(chosenProfessions.get(index));
        parent.expField.changeValue(chosenProfessions.size() == 1 ? 50 : 25);

        profRollButton.setEnabled(false);
        profRollResult.setEditable(false);
        profOKButton.setEnabled(false);
        for (JButton button : profButtons) {
            button.setEnabled(false);
        }
        profOption4a.setLocked(true);
        profOption4b.setLocked(true);

        parent.moveToNextTab();
    }

    private void fillCombos() {
        profOption4a.addItem(" ");
        profOption4b.addItem(" ");
        professionClasses.forEach(e -> profOption4a.addItem(e.getName()));
        professionCareers.forEach(e -> profOption4b.addItem(e.getName()));
        profOption4a.refresh();
        profOption4b.refresh();
    }
    private void comboChangedClass() {
        profOption4b.removeAllItems();
        if (" ".equals(profOption4a.getSelectedItem())) {
            profOption4b.addItem(" ");
            professionCareers.forEach(e -> profOption4b.addItem(e.getName()));
        } else {
            ProfessionClass clss = professionClasses.get(profOption4a.getSelectedIndex());
            for (ProfessionCareer career : professionCareers) {
                if (career.getProfessionClass().equals(clss)) {
                    profOption4b.addItem(career.getName());
                }
            }
        }
        profOption4b.refresh();
    }
}
