import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;

public class Main {
    static JFrame frame;
    static LanguagePack languagePack;
    static JPanel startPanel;
    final Connection connection;

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
        frame.setMinimumSize(new Dimension(700, 550));
    }

    Main() {
        repaint();
        connection = new Connection();
        comboBox1.addItem(new ImageIcon("src/main/resources/images/pl.gif"));
        comboBox1.addItem(new ImageIcon("src/main/resources/images/eng.gif"));
        createcharButton.addActionListener(e -> {
            frame.setContentPane(new CharacterGen(frame, this, languagePack, connection).mainPanel);
            frame.validate();
        });
        createsheetButton.addActionListener(e -> {
            frame.setContentPane(new CharacterSheet(frame, this, languagePack, connection).mainPanel);
            frame.validate();
        });
        comboBox1.addActionListener(e -> {
            languagePack.setCode(comboBox1.getSelectedIndex());
            repaint();
        });
    }

    void repaint() {
//        createcharButton.setText(languagePack.localise(createcharButton.getName()));
//        createsheetButton.setText(languagePack.localise(createsheetButton.getName()));
    }

}
