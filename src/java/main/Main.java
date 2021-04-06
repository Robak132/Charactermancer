package main;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Locale;

public class Main {
    static JFrame frame;
    static JPanel startPanel;
    final Connection connection;

    JComboBox<ImageIcon> comboBox1;
    JButton createcharButton;
    JButton createsheetButton;
    JPanel mainPanel;
    Locale[] languages = new Locale[] {Locale.ENGLISH, new Locale("pl", "PL")};
    Dimension activeDimension = null;

    public static void main(String[] args) {
        frame = new JFrame("Charactermancer");
        frame.setContentPane(new Main().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 775));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    Main() {
        Locale.setDefault(Locale.ENGLISH);
        connection = new Connection();
        comboBox1.addItem(new ImageIcon("src/resources/images/eng.gif"));
        comboBox1.addItem(new ImageIcon("src/resources/images/pl.gif"));
        createcharButton.addActionListener(e -> {
            frame.setContentPane(new CharacterGen(frame, this, connection).mainPanel);
            frame.validate();
        });
        createcharButton.setMnemonic(KeyEvent.VK_1);
        createsheetButton.addActionListener(e -> {
            frame.setContentPane(new CharacterSheet(frame, this, connection).mainPanel);
            frame.validate();
        });
        createsheetButton.setMnemonic(KeyEvent.VK_2);
        comboBox1.addActionListener(e -> {
            Locale.setDefault(languages[comboBox1.getSelectedIndex()]);
        });
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                if (activeDimension == null || !activeDimension.equals(new Dimension(frame.getWidth(), frame.getHeight()))) {
                    activeDimension = new Dimension(frame.getWidth(), frame.getHeight());
                    System.out.printf("%d, %d\n", frame.getWidth(), frame.getHeight());
                }
            }
        });
    }
}
