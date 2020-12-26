import mappings.Rase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class CharacterGenRase {
    JFrame frame;
    JPanel mainPanel, previous_panel, next_panel;
    LanguagePack languagePack;
    Connection connection;
    CharacterSheet sheet;

    private JTextField rollResult;
    private JButton rollButton;
    private JButton okButton;
    private JButton SELECT1Button;
    private JTextField rollResultField;
    private JComboBox<String> comboBox;
    private JPanel Results;
    private JButton exitButton;
    private JLabel charcreation;
    private JButton SELECT2Button;
    private JButton forwardButton;
    private JTextField expField;

    int rollResultNumeric;
    Rase rollRase;
    int expvalue;
    int raseNumeric;

    public CharacterGenRase(JFrame _frame, JPanel _panel, LanguagePack _languagepack, Connection _connection, CharacterSheet _sheet) {
        frame = _frame;
        previous_panel = _panel;
        languagePack = _languagepack;
        connection = _connection;
        sheet = _sheet;

        comboBox.addItem(languagePack.localise("human"));
        comboBox.addItem(languagePack.localise("halfling"));
        comboBox.addItem(languagePack.localise("dwarf"));
        comboBox.addItem(languagePack.localise("gnome"));
        comboBox.addItem(languagePack.localise("highelf"));
        comboBox.addItem(languagePack.localise("woodelf"));

        // Phase 1 //
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollResultNumeric = randomIntInRange(1, 100);
                rollResult.setText("" + rollResultNumeric);
                rollRase = connection.getRaseFromTable(rollResultNumeric);
                rollResultField.setText(rollRase.getName());
                progress();
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (Integer.parseInt(rollResult.getText()) > 0 && Integer.parseInt(rollResult.getText()) <= 100) {
                        rollResultNumeric = Integer.parseInt(rollResult.getText());
                        rollRase = connection.getRaseFromTable(rollResultNumeric);
                        rollResultField.setText(rollRase.getName());
                        progress();
                    }
                } catch (Exception ex) {
                    rollResult.setText("");
                }
            }
        });

        // Phase 2 //
        SELECT1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBox.setSelectedItem(rollResultField.getText());
                sheet.setRase(rollRase);
                sheet.addExp(20);
                expField.setText("" + sheet.getExp());
                next_panel = new CharacterGenProf(frame, mainPanel, languagePack, connection, sheet).mainPanel;
                frame.setContentPane(next_panel);
                frame.validate();
                forwardButton.setEnabled(true);
                forwardButton.setVisible(true);
                changePhase2(false);
            }
        });
        SELECT2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollResultField.setText(comboBox.getSelectedItem().toString());
                sheet.setRase(connection.getRase(comboBox.getSelectedItem().toString()));
                next_panel = new CharacterGenProf(frame, mainPanel, languagePack, connection, sheet).mainPanel;
                frame.setContentPane(next_panel);
                frame.validate();
                forwardButton.setEnabled(true);
                forwardButton.setVisible(true);
                changePhase2(false);
            }
        });

        // Navigation //
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(previous_panel);
                frame.validate();
            }
        });
        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(next_panel);
                frame.validate();
            }
        });
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