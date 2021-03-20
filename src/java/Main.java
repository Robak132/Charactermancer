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
        frame.setContentPane(new Main().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(850, 700));
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
    }
}
