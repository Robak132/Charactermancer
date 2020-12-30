import javax.swing.*;
import java.awt.*;

public class Main {
    static JFrame frame;
    static LanguagePack languagePack;
    static JPanel startPanel;
    static Main screen1;
    final Connection connection;
    CharacterSheet sheet;

    JComboBox<ImageIcon> comboBox1;
    JButton createcharButton;
    JButton createsheetButton;
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
        frame.setMinimumSize(new Dimension(690, 400));
    }

    Main() {
        repaint();
        connection = new Connection();
        sheet = new CharacterSheet();
        comboBox1.addItem(new ImageIcon("src/main/resources/images/pl.gif"));
        comboBox1.addItem(new ImageIcon("src/main/resources/images/eng.gif"));
        createcharButton.addActionListener(e -> {
            frame.setContentPane(new CharacterGenRase(frame, this, languagePack, connection, sheet).mainPanel);
            frame.validate();
        });
        createsheetButton.addActionListener(e -> {
            frame.setContentPane(new CharacterSheet().mainPanel);
            frame.validate();
        });
        comboBox1.addActionListener(e -> {
            languagePack.setCode(comboBox1.getSelectedIndex());
            repaint();
        });
    }
    void update_data() {

    }
    void repaint() {
        createcharButton.setText(languagePack.localise(createcharButton.getName()));
        createsheetButton.setText(languagePack.localise(createsheetButton.getName()));
    }

}
