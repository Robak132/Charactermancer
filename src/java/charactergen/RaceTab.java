package charactergen;

import components.FilteredComboBox;
import components.JIntegerField;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import main.CharacterSheet;
import main.Connection;
import main.DatabaseConnection;
import mappings.Race;
import mappings.Subrace;
import org.apache.commons.collections4.KeyValue;

class RaceTab implements DatabaseConnection {
    private CharacterSheet sheet;
    private CharacterGen parent;
    private Connection connection;

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
    public RaceTab(CharacterGen parent, CharacterSheet sheet) {
        initialise(parent, sheet);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet) {
        this.sheet = sheet;
        this.parent = parent;
        this.connection = getConnection();

        raceRollButton.addActionListener(e -> {
            KeyValue<Integer, Race> result = Race.getRandomRace(connection);
            int rollResultNumeric = result.getKey();
            race = result.getValue();
            subraces = race.getSubraces();

            setRace(rollResultNumeric, race.getName());

            List<Race> races = connection.getRaces();
            races.remove(race);
            raceOption2Combo.addItems(races);
            raceOption2Combo.setListRenderer(Race::getName);

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
        subraceOptionCombo.setListRenderer(Subrace::getName);

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

    @Override
    public Connection getConnection() {
        if (connection == null) {
            connection = parent.getConnection();
        }
        return connection;
    }
}
