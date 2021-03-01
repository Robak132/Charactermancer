import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import mappings.Profession;
import mappings.Race;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
    private JComboBox<String> raseSelect;
    private JPanel basePanel;
    private JButton exitButton;
    private JTextField raseSelectText;

    Race rase;
    Profession prof;
    List<Integer> base_atributes;
    List<JTextField> attributesTextFields = new ArrayList<>();
    int exp;
    boolean lock = false;
    boolean warning = false;

    public CharacterSheet(JFrame _frame, Main _screen, LanguagePack _languagepack, Connection _connection) {
        frame = _frame;
        previous_screen = _screen;
        languagePack = _languagepack;
        connection = _connection;

        createAll();
    }
    public CharacterSheet() {
    }

    private void createAll() {
        fillRases("");
        raseSelectText.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            if (!lock) {
                SwingUtilities.invokeLater(() -> {
                    lock = true;
                    fillRases(raseSelectText.getText());
                    lock = false;
                });
            }
        });

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
            chartext.getDocument().addDocumentListener((SimpleDocumentListener) e -> checkCharValue());
            basePanel.add(chartext, new GridConstraints(1, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
        }

        exitButton.addActionListener(e -> {
            frame.setContentPane(previous_screen.mainPanel);
            frame.validate();
        });
    }
    private void checkCharValue() {
        System.out.println(rase.getName());
    }
    public void fillRases(String text) {
        raseSelectText = (JTextField) raseSelect.getEditor().getEditorComponent();
        raseSelect.removeAllItems();
        List races = connection.getRaces();
        boolean changed = false;
        for (Object race_itr : races) {
            String name = ((Race) race_itr).getName();
            if (name.toUpperCase().contains(text.toUpperCase()))
                raseSelect.addItem(name);
            if (name.equals(text)) {
                rase = (Race) race_itr;
                changed = true;
            }
//            else
//                raseSelectText.setForeground(Color.red);
        }
        if (!changed)
            rase = null;
        raseSelect.setSelectedItem(text);
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

    public Race getRase() {
        return rase;
    }
    public void setRase(Race rase) {
        this.rase = rase;
    }
    public Profession getProf() {
        return prof;
    }
    public void setProf(Profession prof) {
        this.prof = prof;
    }
    public List<Integer> getBAtributes() {
        return base_atributes;
    }
    public void setBAtributes(List<Integer> base_atributes) {
        this.base_atributes = base_atributes;
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
}