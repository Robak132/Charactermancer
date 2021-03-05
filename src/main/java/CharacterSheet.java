import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import components.SearchableJComboBox;
import mappings.Profession;
import mappings.Race;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CharacterSheet {
    JFrame frame;
    JPanel mainPanel;
    Main previous_screen;
    LanguagePack languagePack;
    Connection connection;

    private JButton createPlayerCharacterButton;
    private JButton makeCharacterSheetButton;
    private JButton saveButton;
    private JTextField nameField;
    private SearchableJComboBox raceSelect;
    private JPanel basePanel;
    private JButton exitButton;
    private JTextField raceSelectText;

    private Race race;
    private Profession prof;
    private Integer[] base_attributes = new Integer[10];
    private Integer[] adv_attributes = new Integer[10];
    private Integer[] sum_attributes = new Integer[10];
    private List<JTextField> attributesTextFields = new ArrayList<>();

    private int move, maxhp, hp, exp;
    private boolean warning = false;

    public CharacterSheet(JFrame _frame, Main _screen, LanguagePack _languagepack, Connection _connection) {
        frame = _frame;
        previous_screen = _screen;
        languagePack = _languagepack;
        connection = _connection;
        exp = 0;

        createAll();
    }
    public CharacterSheet() {
        exp = 0;
    }

    private void createAll() {
        raceSelect.addItems(connection.getRacesNames());

        String[] columns = {"M", "WW", "US", "S", "Wt", "I", "Zw", "Zr", "Int", "SW", "Ogd", "Żyw"};
        basePanel.setLayout(new GridLayoutManager(2, columns.length + 2, new Insets(0, 0, 0, 0), -1, -1));
        Spacer spacer1 = new Spacer();
        Spacer spacer2 = new Spacer();
        basePanel.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        basePanel.add(spacer2, new GridConstraints(0, columns.length + 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));

        for (int i = 0; i < columns.length; i++) {
            JLabel charlabel = new JLabel();
            charlabel.setHorizontalAlignment(0);
            charlabel.setHorizontalTextPosition(0);
            charlabel.setText(columns[i]);
            basePanel.add(charlabel, new GridConstraints(0, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
            JTextField chartext = new JTextField();
            chartext.setHorizontalAlignment(0);
            attributesTextFields.add(chartext);
//            chartext.getDocument().addDocumentListener((SimpleDocumentListener) e -> checkCharValue());
            basePanel.add(chartext, new GridConstraints(1, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
        }

        exitButton.addActionListener(e -> {
            frame.setContentPane(previous_screen.mainPanel);
            frame.validate();
        });
    }
    private void checkCharValue() {
        System.out.println(race.getName());
    }
    public static void ReadJSONExample() {
        JSONParser parser = new JSONParser();
        JSONArray a = null;
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("src/main/resources/Nowy.json"));
            JSONArray atr = (JSONArray) jsonObject.get("attribs");

            JSONObject b = (JSONObject) atr.get(0);
            b.remove("current");
            b.put("current", 17);
            System.out.print(jsonObject);


            FileWriter file = new FileWriter("src/main/resources/employees.json");
            file.write(jsonObject.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public Race getRace() {
        return race;
    }
    public Profession getProf() {
        return prof;
    }
    public void setRace(Race race) {
        this.race = race;
    }
    public void setProf(Profession prof) {
        this.prof = prof;
    }

    public Integer[] getBaseAttributes() {
        return base_attributes;
    }
    public int getBaseAttribute(Race.Attributes attr) {
        return base_attributes[attr.ordinal()];
    }
    public Integer[] getAdvAttributes() {
        return adv_attributes;
    }
    public int getAdvAttribute(Race.Attributes attr) {
        return adv_attributes[attr.ordinal()];
    }
    public Integer[] getSumAttributes() {
        return sum_attributes;
    }
    public int getSumAttribute(Race.Attributes attr) {
        return sum_attributes[attr.ordinal()];
    }
    public void setBaseAttributes(int index, int attribute) {
        this.base_attributes[index] = attribute;
    }
    public void setAdvAttributes(int index, int attribute) {
        this.adv_attributes[index] = attribute;
    }
    public void setSumAttributes(int index, int attribute) {
        this.sum_attributes[index] = attribute;
    }

    public int getExp() {
        return exp;
    }
    public void setExp(int exp) {
        this.exp = exp;
    }
    public void addExp(int exp) {
        this.exp += exp;
    }
    public int getMove() {
        return move;
    }
    public void setMove(int move) {
        this.move = move;
    }
    public int getMaxHP() {
        return maxhp;
    }
    public void setMaxHP(int maxhp) {
        this.maxhp = maxhp;
    }
    public int getHP() {
        return hp;
    }
    public void setHP(int hp) {
        this.hp = hp;
    }
    public void setHP() {
        this.hp = maxhp;
    }
}