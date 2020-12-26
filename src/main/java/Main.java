import mappings.RaseTable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    static JFrame frame;
    static LanguagePack languagePack;
    static JPanel startPanel;
    final Connection connection;
    CharacterSheet sheet;

    private JComboBox<ImageIcon> comboBox1;
    private JButton createcharButton;
    private JButton createsheetButton;
    JPanel mainPanel;

    public static void main(String[] args) {
        languagePack = new LanguagePack(0);

        frame = new JFrame("Charactermancer");
        startPanel = new Main().mainPanel;
        frame.setContentPane(startPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(600, 350));
    }

    Main() {
        Repaint();
        connection = new Connection();
        sheet = new CharacterSheet();
        comboBox1.addItem(new ImageIcon("src/main/resources/images/pl.gif"));
        comboBox1.addItem(new ImageIcon("src/main/resources/images/eng.gif"));

        createcharButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new CharacterGenRase(frame, startPanel, languagePack, connection, sheet).mainPanel);
                frame.validate();
            }
        });
        createsheetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new CharacterSheet().mainPanel);
                frame.validate();
            }
        });
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                languagePack.setCode(comboBox1.getSelectedIndex());
                Repaint();
            }
        });
    }

    void Repaint() {
        createcharButton.setText(languagePack.localise(createcharButton.getName()));
        createsheetButton.setText(languagePack.localise(createsheetButton.getName()));
    }

}
