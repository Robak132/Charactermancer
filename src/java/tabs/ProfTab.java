package tabs;

import components.FilteredComboBox;
import components.JIntegerField;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import main.CharacterGen;
import main.CharacterSheet;
import main.Connection;
import mappings.Profession;
import mappings.ProfessionCareer;
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
    private JTextField profOption4a;
    private FilteredComboBox<ProfessionCareer> profOption4b;
    private JButton profOption4Button;

    private final JTextField[][] profOptions = {
            {profOption1a, profOption1b},
            {profOption2a, profOption2b},
            {profOption3a, profOption3b}
    };
    private final JButton[] profButtons = {
            profOption1Button, profOption2Button, profOption3Button, profOption4Button
    };

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
        profOption4b.addItems(sheet.getRace().getRaceCareers());
        profOption4b.setUserFilter(ProfessionCareer::getName);

        profRollButton.addActionListener(e -> {
            Object[] result = CharacterGen.getRandomProf(connection, sheet.getSubrace());
            Profession rollProf = (Profession) result[1];
            int rollResultNumeric = (int) result[0];
            while (chosenProfessions.contains(rollProf)) {
                LogManager.getLogger(getClass().getName()).debug(String.format("%d doubled, changing to %d", rollResultNumeric, rollResultNumeric % 100 + 1));
                rollResultNumeric = rollResultNumeric % 100 + 1;
                rollProf = connection.getProfFromTable(sheet.getSubrace(), rollResultNumeric);
            }
            chosenProfessions.add(rollProf);
            profRollResult.setValue(rollResultNumeric);

            profOptions[chosenProfessions.size() - 1][0].setText(rollProf.getProfessionCareer().getProfessionClass().getName());
            profOptions[chosenProfessions.size() - 1][1].setText(rollProf.getProfessionCareer().getName());
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

            Profession prof = connection.getProfFromTable(sheet.getSubrace(), rollResultNumeric);
            if (chosenProfessions.contains(prof)) {
                // TODO: Make "Roll doubled" dialog or do sth better
            } else {
                chosenProfessions.add(prof);
                profOptions[chosenProfessions.size() - 1][0].setText(prof.getProfessionCareer().getName());
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

        profOption4b.addActionListener(e -> {
            try {
                profOption4a.setText(((ProfessionCareer) profOption4b.getSelectedItem()).getProfessionClass().getName());
            } catch (NullPointerException ex) {
                profOption4a.setText("");
            }
        });
        profOption4Button.addActionListener(e ->{
            sheet.setProfession(((ProfessionCareer) profOption4b.getSelectedItem()).getProfessionByLvl(1));
            lockAll();
        });
        profOption4Button.setMnemonic(KeyEvent.VK_4);
    }

    private void lockAll(int index) {
        sheet.setProfession(chosenProfessions.get(index));
        sheet.addExp(chosenProfessions.size() == 1 ? 50 : 25);

        lockAll();
    }
    private void lockAll() {
        profRollButton.setEnabled(false);
        profRollResult.setEditable(false);
        profOKButton.setEnabled(false);
        for (JButton button : profButtons) {
            button.setEnabled(false);
        }
        profOption4b.setLocked(true);

        parent.moveToNextTab();
    }
}
