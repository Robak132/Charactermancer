package main;

import charactergen.CharacterGen;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import sheetbrowser.CharacterSheetBrowser;

public class Main implements DatabaseConnection {
    private static JFrame frame;
    public JPanel mainPanel;

    private final Connection connection;
    private JComboBox<ImageIcon> comboBox1;
    private JButton createcharButton;
    private JButton createsheetButton;
    private JButton statBlockParserButton;
    private final Locale[] languages = {Locale.ENGLISH, new Locale("pl", "PL")};
    private Dimension activeDimension = null;

    public static void main(String[] args) {
        frame = new JFrame("Charactermancer");
        frame.setContentPane(new Main().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 800));
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
            frame.setContentPane(new CharacterGen(frame, this).mainPanel);
            frame.validate();
        });
        createcharButton.setMnemonic(KeyEvent.VK_1);
        createsheetButton.addActionListener(e -> {
            frame.setContentPane(new CharacterSheetBrowser(frame, null,this, connection).mainPanel);
            frame.validate();
        });
        createsheetButton.setMnemonic(KeyEvent.VK_2);
        statBlockParserButton.addActionListener(e -> {
            frame.setContentPane(new StatBlockParser(frame, null,this, connection).mainPanel);
            frame.validate();
        });
        statBlockParserButton.setMnemonic(KeyEvent.VK_3);
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
    public Connection getConnection() {
        return connection;
    }
}
