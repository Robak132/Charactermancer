package tabs;

import components.FilteredComboBox;
import components.JIntegerField;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import main.CharacterGen;
import main.CharacterSheet;
import main.Connection;
import mappings.Race;
import mappings.Subrace;

public final class RaceTab {
    private CharacterSheet sheet;
    private CharacterGen parent;

    private JButton raceRollButton;
    private JIntegerField raceRollResult;
    private JButton raceOKButton;
    private JButton raceOption1Button;
    private JButton raceOption2Button;
    private FilteredComboBox<Race> raceOption2Combo;
    private JTextField raceOption1;
    private FilteredComboBox<Subrace> subraceOptionCombo;
    private JButton subraceOptionButton;
    private JPanel mainPanel;

    private Race race;
    private List<Subrace> subraces;

    public RaceTab() {
        // Needed for GUI Designer
    }
    public RaceTab(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        initialise(parent, sheet, connection);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        this.sheet = sheet;
        this.parent = parent;

        raceOption2Combo.addItems(connection.getRaces());
        raceOption2Combo.setUserFilter(Race::getName);

        raceRollButton.addActionListener(e -> {
            Object[] result = CharacterGen.getRandomRace(connection);
            int rollResultNumeric = (int) result[0];
            race = (Race) result[1];
            subraces = race.getSubraces();

            setRace(rollResultNumeric, race.getName());
            moveToOptions();
        });
        raceRollButton.setMnemonic(KeyEvent.VK_R);
        raceOKButton.addActionListener(e -> {
            int rollResultNumeric = raceRollResult.getValue();
            if (rollResultNumeric <= 0 || rollResultNumeric > 100) {
                raceRollResult.setText("");
                return;
            }

            race = connection.getRaceFromTable(rollResultNumeric);
            subraces = race.getSubraces();

            setRace(rollResultNumeric, race.getName());
            moveToOptions();
        });
        raceOKButton.setMnemonic(KeyEvent.VK_O);
        raceOption1Button.addActionListener(e -> {
            raceOption2Combo.setSelectedItem(race);
            subraces = race.getSubraces();
            sheet.addExp(20);

            moveToSubraces();
        });
        raceOption1Button.setMnemonic(KeyEvent.VK_1);
        raceOption2Button.addActionListener(e -> {
            race = (Race) raceOption2Combo.getSelectedItem();
            if (race != null) {
                raceOption1.setText(race.getName());
                subraces = race.getSubraces();
            }

            moveToSubraces();
        });
        raceOption2Button.setMnemonic(KeyEvent.VK_2);
        subraceOptionButton.addActionListener(e -> {
            sheet.setSubrace(subraces.get(subraceOptionCombo.getSelectedIndex()));

            subraceOptionButton.setEnabled(false);
            subraceOptionCombo.setLocked(true);
            parent.moveToNextTab();
        });
        subraceOptionButton.setMnemonic(KeyEvent.VK_3);
    }
    private void setRace(int rollResultNumeric, String name) {
        raceRollResult.setValue(rollResultNumeric);
        raceOption1.setText(name);
    }
    private void moveToSubraces() {
        raceOption1Button.setEnabled(false);
        raceOption1.setEditable(false);
        raceOption2Button.setEnabled(false);
        raceOption2Combo.setLocked(true);

        subraceOptionCombo.addItems(subraces);
        subraceOptionCombo.setUserFilter(Subrace::getName);

        if (subraces.size() == 1) {
            sheet.setSubrace((Subrace) subraceOptionCombo.getSelectedItem());
            parent.moveToNextTab();
        } else {
            subraceOptionButton.setEnabled(true);
            subraceOptionCombo.setEnabled(true);
        }
    }
    private void moveToOptions() {
        raceRollButton.setEnabled(false);
        raceRollResult.setEditable(false);
        raceOKButton.setEnabled(false);

        raceOption1.setEnabled(true);
        raceOption1Button.setEnabled(true);
        raceOption2Combo.setEnabled(true);
        raceOption2Button.setEnabled(true);
    }
}
