import mappings.Race;

import javax.swing.*;
import java.util.*;

public class CharacterGenRace {
    JFrame frame;
    JPanel mainPanel;

    LanguagePack languagePack;
    Connection connection;
    CharacterSheet sheet;

    Main previous_screen;
    CharacterGenProf next_screen;

    JTextField rollResult;
    JButton rollButton;
    JButton okButton;
    JButton SELECT1Button;
    JTextField rollResultField;
    JComboBox<String> comboBox;
    JPanel Results;
    JButton exitButton;
    JLabel charcreation;
    JButton SELECT2Button;
    JButton forwardButton;
    JTextField expField;

    int rollResultNumeric;
    Race rollRase;

    public CharacterGenRace(JFrame _frame, Main _screen, LanguagePack _languagepack, Connection _connection, CharacterSheet _sheet) {
        frame = _frame;
        previous_screen = _screen;
        languagePack = _languagepack;
        connection = _connection;
        sheet = _sheet;

        sheet.setExp(0);
        comboBox.addItem(languagePack.localise("human"));
        comboBox.addItem(languagePack.localise("halfling"));
        comboBox.addItem(languagePack.localise("dwarf"));
        comboBox.addItem(languagePack.localise("gnome"));
        comboBox.addItem(languagePack.localise("highelf"));
        comboBox.addItem(languagePack.localise("woodelf"));
        repaint();
        update_data();

        // Phase 1 //
        rollButton.addActionListener(e -> {
            rollResultNumeric = randomIntInRange(1, 100);
            rollResult.setText("" + rollResultNumeric);
            rollRase = connection.getRaceFromTable(rollResultNumeric);
            rollResultField.setText(rollRase.getName());
            progress();
        });
        okButton.addActionListener(e -> {
            try {
                if (Integer.parseInt(rollResult.getText()) > 0 && Integer.parseInt(rollResult.getText()) <= 100) {
                    rollResultNumeric = Integer.parseInt(rollResult.getText());
                    rollRase = connection.getRaceFromTable(rollResultNumeric);
                    rollResultField.setText(rollRase.getName());
                    progress();
                }
            } catch (Exception ex) {
                rollResult.setText("");
            }
        });

        // Phase 2 //
        SELECT1Button.addActionListener(e -> {
            comboBox.setSelectedItem(rollResultField.getText());
            sheet.setRase(rollRase);
            sheet.addExp(20);
            update_data();
            next_screen = new CharacterGenProf(frame, this, languagePack, connection, sheet);
            frame.setContentPane(next_screen.mainPanel);
            frame.validate();
            forwardButton.setEnabled(true);
            forwardButton.setVisible(true);
            changePhase2(false);
        });
        SELECT2Button.addActionListener(e -> {
            rollResultField.setText(comboBox.getSelectedItem().toString());
            sheet.setRase(connection.getRace(comboBox.getSelectedItem().toString()));
            next_screen = new CharacterGenProf(frame, this, languagePack, connection, sheet);
            frame.setContentPane(next_screen.mainPanel);
            frame.validate();
            forwardButton.setEnabled(true);
            forwardButton.setVisible(true);
            changePhase2(false);
        });

        // Navigation //
        exitButton.addActionListener(e -> {
            frame.setContentPane(previous_screen.mainPanel);
            frame.validate();
        });
        forwardButton.addActionListener(e -> {
            frame.setContentPane(next_screen.mainPanel);
            frame.validate();
        });
    }
    void update_data() {
        expField.setText("" + sheet.getExp());
    }
    void changePhase2(boolean status) {
        SELECT1Button.setEnabled(status);
        SELECT2Button.setEnabled(status);
        comboBox.setEnabled(status);
    }
    void repaint() {
        charcreation.setText(languagePack.localise(charcreation.getName()));
        exitButton.setText(languagePack.localise(exitButton.getName()));
        forwardButton.setText(languagePack.localise(forwardButton.getName()));
        SELECT1Button.setText(languagePack.localise(SELECT1Button.getName()));
        SELECT2Button.setText(languagePack.localise(SELECT2Button.getName()));
    }
    void progress() {
        rollButton.setEnabled(false);
        rollResult.setEditable(false);
        okButton.setEnabled(false);
        Results.setVisible(true);
        changePhase2(true);
    }
    public int randomIntInRange(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}