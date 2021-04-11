package main;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;

public class Main {
    private static JFrame frame;
    public JPanel mainPanel;

    private final Connection connection;
    private JComboBox<ImageIcon> comboBox1;
    private JButton createcharButton;
    private JButton createsheetButton;
    private final Locale[] languages = {Locale.ENGLISH, new Locale("pl", "PL")};
    private Dimension activeDimension = null;

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
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
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
            frame.setContentPane(new CharacterSheetMaker(frame, this, connection).mainPanel);
            frame.validate();
        });
        createsheetButton.setMnemonic(KeyEvent.VK_2);
        comboBox1.addActionListener(e -> Locale.setDefault(languages[comboBox1.getSelectedIndex()]));
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                if (activeDimension == null || !activeDimension.equals(new Dimension(frame.getWidth(), frame.getHeight()))) {
                    activeDimension = new Dimension(frame.getWidth(), frame.getHeight());
                    LogManager.getLogger(getClass().getName()).debug(String.format("Dimensions changed: (%d, %d)", frame.getWidth(), frame.getHeight()));
                }
            }
        });
    }
}
