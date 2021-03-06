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

    public static void main(String[] args) {
        frame = new JFrame("Charactermancer");
        startPanel = new Main().mainPanel;
        frame.setContentPane(startPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(750, 600));
    }

    Main() {
        Locale.setDefault(Locale.ENGLISH);
        connection = new Connection();
        comboBox1.addItem(new ImageIcon("src/main/resources/images/eng.gif"));
        comboBox1.addItem(new ImageIcon("src/main/resources/images/pl.gif"));
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
    }
}
