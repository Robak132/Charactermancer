import mappings.Profession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class CharacterGenProf {
    JFrame frame;
    JPanel mainPanel, previous_panel;
    LanguagePack languagePack;
    Connection connection;
    CharacterSheet sheet;

    private JTextField expField;
    private JTextField rollResultField1a;
    private JButton rollButton1;
    private JButton okButton1;
    private JButton SELECTButton5;
    private JComboBox<String> comboBox1;
    private JPanel Phase3;
    private JButton backButton;
    private JLabel charcreation;
    private JPanel Phase2;
    private JPanel Phase1;
    private JPanel Phase2Results;
    private JTextField rollResult1;
    private JTextField rollResultField1b;
    private JButton rollButton2;
    private JButton okButton2;
    private JTextField rollResult2;
    private JTextField rollResultField2a;
    private JTextField rollResultField2b;
    private JTextField rollResultField3a;
    private JTextField rollResultField3b;
    private JTextField rollResultField4a;
    private JTextField rollResultField4b;
    private JButton SELECTButton2;
    private JButton SELECTButton3;
    private JButton SELECTButton4;
    private JButton SELECTButton1;
    private JLabel rollLabel;
    private JComboBox<String> comboBox;

    int rollResultNumeric1, rollResultNumeric2;
    List<Profession> profList;

    public CharacterGenProf(JFrame _frame, JPanel _panel, LanguagePack _languagepack, Connection _connection, CharacterSheet _sheet) {
        frame = _frame;
        previous_panel = _panel;
        languagePack = _languagepack;
        connection = _connection;
        sheet = _sheet;
        expField.setText("" + sheet.getExp());
        profList = new ArrayList<>();
        fillCombos();

        // Phase 1 //
        rollButton1.addActionListener(e -> {
            rollResultNumeric1 = randomIntInRange(1, 100);
            rollResult1.setText("" + rollResultNumeric1);
            Profession prof = connection.getProfFromTable(sheet.getRase().getId(), rollResultNumeric1);
            profList.add(prof);
            rollResultField1a.setText(prof.getCareer());
            rollResultField1b.setText(prof.getProfession());
            rollResultField2a.setText(prof.getCareer());
            rollResultField2b.setText(prof.getProfession());
            changePhase1(false);
            Phase2.setVisible(true);
        });
        okButton1.addActionListener(e -> {
            try {
                if (Integer.parseInt(rollResult1.getText()) > 0 && Integer.parseInt(rollResult1.getText()) <= 100) {
                    rollResultNumeric1 = Integer.parseInt(rollResult1.getText());
                    Profession prof = connection.getProfFromTable(sheet.getRase().getId(), rollResultNumeric1);
                    profList.add(prof);
                    rollResultField1a.setText(prof.getCareer());
                    rollResultField1b.setText(prof.getProfession());
                    rollResultField2a.setText(prof.getCareer());
                    rollResultField2b.setText(prof.getProfession());
                    changePhase1(false);
                    Phase2.setVisible(true);
                }
            } catch (Exception ex) {
                rollResult1.setText("");
            }
        });

        // Phase 2 //
        rollButton2.addActionListener(e -> {
            rollResult2.setForeground(Color.BLACK);
            rollLabel.setVisible(false);
            Profession prof;
            if (profList.size() < 3) {
                do {
                    rollResultNumeric2 = randomIntInRange(1, 100);
                    rollResult2.setText("" + rollResultNumeric2);
                    prof = connection.getProfFromTable(sheet.getRase().getId(), rollResultNumeric2);
                } while (profList.contains(prof));
                profList.add(prof);
            }
            updateFields();
            Phase2Results.setVisible(true);
            Phase1.setVisible(false);
        });
        okButton2.addActionListener(e -> {
            try {
                if (Integer.parseInt(rollResult2.getText()) > 0 && Integer.parseInt(rollResult2.getText()) <= 100) {
                    rollResultNumeric2 = Integer.parseInt(rollResult2.getText());
                    Profession prof = connection.getProfFromTable(sheet.getRase().getId(), rollResultNumeric2);
                    if (!profList.contains(prof)) {
                        rollResult2.setForeground(Color.BLACK);
                        rollLabel.setVisible(false);
                        profList.add(prof);
                    }
                    else {
                        rollResult2.setForeground(Color.RED);
                        rollLabel.setVisible(true);
                    }
                }
            } catch (Exception ex) {
                rollResult2.setText("");
            }
            updateFields();
            Phase2Results.setVisible(true);
            Phase1.setVisible(false);
        });

        // Navigation //
        backButton.addActionListener(e -> {
            frame.setContentPane(previous_panel);
            frame.validate();
        });
    }
    void updateFields() {
        switch (profList.size()) {
            case 3:
                rollResultField4a.setText(profList.get(2).getCareer());
                rollResultField4b.setText(profList.get(2).getProfession());
                changePhase2(false);
                changePhase2Results(true);
                Phase3.setVisible(true);
            case 2:
                rollResultField3a.setText(profList.get(1).getCareer());
                rollResultField3b.setText(profList.get(1).getProfession());
                break;
            default:
                break;
        }
    }
    void changePhase1(boolean status) {
        rollButton1.setEnabled(status);
        rollResult1.setEditable(status);
        okButton1.setEnabled(status);
    }
    void changePhase2(boolean status) {
        rollButton2.setVisible(status);
        rollResult2.setVisible(status);
        okButton2.setVisible(status);
    }
    void changePhase2Results(boolean status) {
        SELECTButton2.setEnabled(status);
        SELECTButton3.setEnabled(status);
        SELECTButton4.setEnabled(status);
    }

    void fillCombos() {
        List list = connection.getProfs();
        comboBox.addItem("");
        comboBox1.addItem("");
        for (Object prof:list) {
            Profession temp = (Profession) prof;
            if(((DefaultComboBoxModel)comboBox.getModel()).getIndexOf(temp.getClss()) == -1)
                comboBox.addItem(temp.getClss());
            if(((DefaultComboBoxModel)comboBox1.getModel()).getIndexOf(temp.getProfession()) == -1)
                comboBox1.addItem(temp.getProfession());
        }
    }
    public int randomIntInRange(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}