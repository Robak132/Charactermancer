import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CharacterGenAttr {
    JFrame frame;
    JPanel mainPanel, previous_panel;
    LanguagePack languagePack;
    Connection connection;
    CharacterSheet sheet;

    private JTextField BAttr1;
    private JTextField BAttr2;
    private JTextField BAttr3;
    private JTextField BAttr4;
    private JTextField BAttr5;
    private JTextField BAttr6;
    private JTextField BAttr7;
    private JTextField BAttr8;
    private JTextField BAttr9;
    private JTextField BAttr0;
    private JTextField AAttr1;
    private JTextField AAttr2;
    private JTextField AAttr3;
    private JTextField AAttr4;
    private JTextField AAttr5;
    private JTextField AAttr6;
    private JTextField AAttr7;
    private JTextField AAttr8;
    private JTextField AAttr9;
    private JTextField AAttr0;
    private JTextField TAttr1;
    private JTextField TAttr2;
    private JTextField TAttr3;
    private JTextField TAttr4;
    private JTextField TAttr5;
    private JTextField TAttr6;
    private JTextField TAttr7;
    private JTextField TAttr8;
    private JTextField TAttr9;
    private JTextField TAttr0;

    private JLabel charcreation;
    private JTextField expField;
    private JButton rollButton;
    private JButton backButton;
    private JButton okButton;
    private JPanel Phase1;
    private JLabel descLabel;
    private JButton SELECTButton;
    private JPanel Phase2;
    private JButton button1;

    int rollResultNumeric;
    List<JTextField> AAttr, BAttr, TAttr;
    List<Integer> AAttr_num, BAttr_num, TAttr_num;
    int aattr_it=0;

    public CharacterGenAttr(JFrame _frame, JPanel _panel, LanguagePack _languagepack, Connection _connection, CharacterSheet _sheet) {
        frame = _frame;
        previous_panel = _panel;
        languagePack = _languagepack;
        connection = _connection;
        sheet = _sheet;
        setBaseValues();
        update();

        // Phase1 //
        rollButton.addActionListener(e -> {
            if (aattr_it < AAttr.size()) {
                rollResultNumeric = randomIntInRange(1, 10) + randomIntInRange(1, 10);
                AAttr_num.add(rollResultNumeric);
                AAttr.get(aattr_it).setText("" + rollResultNumeric);
                AAttr.get(aattr_it).setEditable(false);
                int totalresult = BAttr_num.get(aattr_it) + AAttr_num.get(aattr_it);
                TAttr.get(aattr_it).setText("" + totalresult);
                TAttr_num.add(totalresult);
                aattr_it++;
            }
            if (aattr_it == AAttr.size()) {
                descLabel.setVisible(false);
                rollButton.setVisible(false);
                okButton.setVisible(false);
                Phase2.setVisible(true);
            }
        });
        // Navigation //
        backButton.addActionListener(e -> {
            frame.setContentPane(previous_panel);
            frame.validate();
        });
    }
    void setBaseValues() {
        AAttr_num = new ArrayList<>();
        BAttr_num = new ArrayList<>();
        TAttr_num = new ArrayList<>();

        BAttr1.setText(String.valueOf(sheet.getRase().getWw()));
        BAttr_num.add(sheet.getRase().getWw());
        BAttr2.setText(String.valueOf(sheet.getRase().getUs()));
        BAttr_num.add(sheet.getRase().getUs());
        BAttr3.setText(String.valueOf(sheet.getRase().getS()));
        BAttr_num.add(sheet.getRase().getS());
        BAttr4.setText(String.valueOf(sheet.getRase().getWt()));
        BAttr_num.add(sheet.getRase().getWt());
        BAttr5.setText(String.valueOf(sheet.getRase().getI()));
        BAttr_num.add(sheet.getRase().getI());
        BAttr6.setText(String.valueOf(sheet.getRase().getZw()));
        BAttr_num.add(sheet.getRase().getZw());
        BAttr7.setText(String.valueOf(sheet.getRase().getZr()));
        BAttr_num.add(sheet.getRase().getZr());
        BAttr8.setText(String.valueOf(sheet.getRase().getIt()));
        BAttr_num.add(sheet.getRase().getIt());
        BAttr9.setText(String.valueOf(sheet.getRase().getSw()));
        BAttr_num.add(sheet.getRase().getSw());
        BAttr0.setText(String.valueOf(sheet.getRase().getOgd()));
        BAttr_num.add(sheet.getRase().getOgd());

        BAttr = Arrays.asList(BAttr1, BAttr2, BAttr3, BAttr4, BAttr5, BAttr6, BAttr7, BAttr8, BAttr9, BAttr0);
        AAttr = Arrays.asList(AAttr1, AAttr2, AAttr3, AAttr4, AAttr5, AAttr6, AAttr7, AAttr8, AAttr9, AAttr0);
        TAttr = Arrays.asList(TAttr1, TAttr2, TAttr3, TAttr4, TAttr5, TAttr6, TAttr7, TAttr8, TAttr9, TAttr0);
    }
    void update() {
        expField.setText("" + sheet.getExp());
    }
    public int randomIntInRange(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}