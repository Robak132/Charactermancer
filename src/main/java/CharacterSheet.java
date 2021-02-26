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
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CharacterSheet {
    private static JFrame frame;
    private JComboBox<String> comboBox1;
    static JPanel panel;
    JPanel mainPanel;
    private JButton createPlayerCharacterButton;
    private JButton makeCharacterSheetButton;
    private JTextField Char0, Char1, Char2, Char3, Char4, Char5, Char10, Char11;
    private JLabel Char0L, Char1L, Char2L, Char3L, Char4L, Char5L, Char6L, Char7L, Char8L, Char9L, Char10L, Char11L;
    private JButton saveButton;
    private JLabel Char21L;
    private JLabel Char22L;
    private JLabel Char23L;
    private JLabel Char24L;
    private JLabel Char25L;
    private JLabel Char26L;
    private JLabel Char27L;
    private JLabel Char28L;
    private JLabel Char29L;
    private JLabel Char210L;
    private JTextField Char21;
    private JTextField Char210;
    private JTextField Char24;
    private JTextField Char25;
    private JTextField Char26;
    private JTextField Char27;
    private JTextField Char28;
    private JTextField Char29;
    private JTextField Char22;
    private JTextField Char23;
    private JTextField nameField;
    private JComboBox<String> raseSelect;

    Race rase;
    Profession prof;
    List<Integer> base_atributes;
    int exp;

    public CharacterSheet() {
//        comboBox1.addItem("II Edition");
//        comboBox1.addItem("IV Edition");
//        comboBox1.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JComboBox cb = (JComboBox)e.getSource();
//                if (((String) cb.getSelectedItem()).equals("II Edition")) {
//                    Char0L.setText("");
//                    Char0.setEnabled(false);
//                    Char0.setVisible(false);
//                    Char1L.setText("");
//                    Char1.setEnabled(false);
//                    Char1.setVisible(false);
//                    Char2L.setText("WW");
//                    Char3L.setText("US");
//                    Char4L.setText("K");
//                    Char5L.setText("Odp");
//                    Char6L.setText("Zr");
//                    Char7L.setText("Int");
//                    Char8L.setText("SW");
//                    Char9L.setText("Ogd");
//                    Char10L.setText("");
//                    Char10.setEnabled(false);
//                    Char10.setVisible(false);
//                    Char11L.setText("");
//                    Char11.setEnabled(false);
//                    Char11.setVisible(false);
//                    Char21L.setText("");
//                    Char21.setEnabled(false);
//                    Char21.setVisible(false);
//                    Char22L.setText("A");
//                    Char23L.setText("Żyw");
//                    Char24L.setText("S");
//                    Char25L.setText("Wt");
//                    Char26L.setText("Sz");
//                    Char27L.setText("Mag");
//                    Char28L.setText("PO");
//                    Char29L.setText("PP");
//                    Char210L.setText("");
//                    Char210.setEnabled(false);
//                    Char210.setVisible(false);
//                }
//                if (((String) cb.getSelectedItem()).equals("IV Edition")) {
//                    Char0L.setText("M");
//                    Char0.setEnabled(true);
//                    Char0.setVisible(true);
//                    Char1L.setText("WW");
//                    Char1.setEnabled(true);
//                    Char1.setVisible(true);
//                    Char2L.setText("US");
//                    Char3L.setText("S");
//                    Char4L.setText("Wt");
//                    Char5L.setText("I");
//                    Char6L.setText("Zw");
//                    Char7L.setText("Zr");
//                    Char8L.setText("Int");
//                    Char9L.setText("SW");
//                    Char10L.setText("Ogd");
//                    Char10.setEnabled(true);
//                    Char10.setVisible(true);
//                    Char11L.setText("Żyw");
//                    Char11.setEnabled(true);
//                    Char11.setVisible(true);
//                    Char21L.setText("WSB");
//                    Char21.setEnabled(true);
//                    Char21.setVisible(true);
//                    Char22L.setText("USB");
//                    Char23L.setText("SB");
//                    Char24L.setText("WtB");
//                    Char25L.setText("IB");
//                    Char26L.setText("ZwB");
//                    Char27L.setText("ZrB");
//                    Char28L.setText("IntB");
//                    Char29L.setText("SWB");
//                    Char210L.setText("OgdB");
//                    Char210.setEnabled(true);
//                    Char210.setVisible(true);
//                }
//            }
//        });
//        saveButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //ReadJSONExample();
//            }
//        });
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
        System.out.println(base_atributes);
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

    public static void ReadJSONExample() {
        JSONParser parser = new JSONParser();
        JSONArray a = null;
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader("src/main/resources/Nowy.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray atr = (JSONArray) jsonObject.get("attribs");

            JSONObject b = (JSONObject) atr.get(0);
            b.remove("current");
            b.put("current", 17);
            System.out.print(jsonObject);


            FileWriter file = new FileWriter("src/main/resources/employees.json");
            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
