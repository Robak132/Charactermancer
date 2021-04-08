package main;

import components.JIntegerField;
import components.SearchableComboBox;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mappings.Race;
import mappings.Subrace;

public class RaceTab {
    private CharacterSheet sheet;
    private Connection connection;
    private CharacterGen parent;

    private JButton raceRollButton;
    private JIntegerField raceRollResult;
    private JButton raceOKButton;
    private JButton raceOption1Button;
    private JButton raceOption2Button;
    private SearchableComboBox raceOption2Combo;
    private JTextField raceOption1;
    private JPanel mainPanel;
    private SearchableComboBox subraceOptionCombo;
    private JButton subraceOptionButton;

    private List<Subrace> subraces;

    public RaceTab() {
    }
    public RaceTab(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        initialise(parent, sheet, connection);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        this.sheet = sheet;
        this.connection = connection;
        this.parent = parent;

        raceOption2Combo.addItems(connection.getRacesNames(), false);

        raceRollButton.addActionListener(e -> {
            Object[] result = CharacterGen.getRandomRace(connection);
            int rollResultNumeric = (int) result[0];
            Race rollRace = (Race) result[1];
            subraces = rollRace.getSubraces();

            sheet.setRace(rollRace);
            raceRollResult.setValue(rollResultNumeric);
            raceOption1.setText(rollRace.getName());

            raceRollButton.setEnabled(false);
            raceRollResult.setEditable(false);
            raceOKButton.setEnabled(false);

            raceOption1.setEnabled(true);
            raceOption1Button.setEnabled(true);
            raceOption2Combo.setEnabled(true);
            raceOption2Button.setEnabled(true);
        });
        raceRollButton.setMnemonic(KeyEvent.VK_R);
        raceOKButton.addActionListener(e -> {
            try {
                if (raceRollResult.getValue() > 0 && raceRollResult.getValue() <= 100) {
                    int rollResultNumeric = raceRollResult.getValue();
                    Race rollRace = connection.getRaceFromTable(rollResultNumeric);

                    sheet.setRace(rollRace);
                    subraces = rollRace.getSubraces();
                    raceOption1.setText(rollRace.getName());

                    raceRollButton.setEnabled(false);
                    raceRollResult.setEditable(false);
                    raceOKButton.setEnabled(false);

                    raceOption1.setEnabled(true);
                    raceOption1Button.setEnabled(true);
                    raceOption2Combo.setEnabled(true);
                    raceOption2Button.setEnabled(true);
                }
            } catch (Exception ex) {
                raceRollResult.setText("");
            }
        });
        raceOKButton.setMnemonic(KeyEvent.VK_O);

        raceOption1Button.addActionListener(e -> {
            raceOption2Combo.setSelectedItem(raceOption1.getText());
            parent.expField.changeValue(20);

            raceOption1Button.setEnabled(false);
            raceOption1.setEditable(false);
            raceOption2Button.setEnabled(false);
            raceOption2Combo.setLocked(true);

            if (subraces.size() != 1) {
                subraces.forEach(ev -> subraceOptionCombo.addItem(ev.getName()));
                subraceOptionCombo.refresh(false);
                subraceOptionButton.setEnabled(true);
                subraceOptionCombo.setEnabled(true);
            } else {
                parent.moveToNextTab();
            }
        });
        raceOption1Button.setMnemonic(KeyEvent.VK_1);
        raceOption2Button.addActionListener(e -> {
            try {
                raceOption1.setText((String) raceOption2Combo.getSelectedItem());
                Race rollRace = connection.getRace((String) raceOption2Combo.getSelectedItem());
                subraces = rollRace.getSubraces();

                raceOption1Button.setEnabled(false);
                raceOption1.setEditable(false);
                raceOption2Button.setEnabled(false);
                raceOption2Combo.setLocked(true);

                if (subraces.size() != 1) {
                    subraces.forEach(ev -> subraceOptionCombo.addItem(ev.getName()));
                    subraceOptionCombo.refresh(false);
                    subraceOptionButton.setEnabled(true);
                    subraceOptionCombo.setEnabled(true);
                } else {
                    parent.moveToNextTab();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        raceOption2Button.setMnemonic(KeyEvent.VK_2);

        subraceOptionButton.addActionListener(e -> {
            subraceOptionButton.setEnabled(false);
            subraceOptionCombo.setLocked(true);
            parent.moveToNextTab();
        });
        subraceOptionButton.setMnemonic(KeyEvent.VK_3);
    }
}
