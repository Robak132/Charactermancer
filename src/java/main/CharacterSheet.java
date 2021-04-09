package main;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import components.SearchableComboBox;
import mappings.Attribute;
import mappings.Profession;
import mappings.Race;
import mappings.Skill;
import mappings.Subrace;
import mappings.Talent;
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
    public JPanel mainPanel;

    private JFrame frame;
    private Main previousScreen;
    private Connection connection;

    private SearchableComboBox raceSelect;
    private JPanel basePanel;
    private JButton exitButton;
    private final List<JTextField> attributesTextFields = new ArrayList<>();
    private int move;
    private int maxhp;
    private int hp;

    private Subrace subrace;
    private Profession prof;
    private List<Attribute> attributeList;
    private List<Skill> skillList;
    private List<Talent> talentList;
    private int exp;

    public CharacterSheet() {
        // Needed for GUI Designer
    }
    public CharacterSheet(JFrame frame, Main parent, Connection connection) {
        this.frame = frame;
        previousScreen = parent;
        this.connection = connection;
    }

    private void createAll() {
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
            basePanel.add(chartext, new GridConstraints(1, i+1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
        }

        exitButton.addActionListener(e -> {
            frame.setContentPane(previousScreen.mainPanel);
            frame.validate();
        });
    }
    public static void readJSONExample() {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader("src/resources/Nowy.json"));
            JSONArray atr = (JSONArray) jsonObject.get("attribs");

            JSONObject b = (JSONObject) atr.get(0);
            b.remove("current");
            b.put("current", 17);
            System.out.print(jsonObject);


            FileWriter file = new FileWriter("src/resources/employees.json");
            file.write(jsonObject.toJSONString());
            file.flush();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Race getRace() {
        return subrace.getBaseRace();
    }
    public Subrace getSubrace() {
        return subrace;
    }
    public void setSubrace(Subrace subrace) {
        this.subrace = subrace;
    }
    public Profession getProfession() {
        return prof;
    }
    public void setProfession(Profession prof) {
        this.prof = prof;
    }
    public List<Attribute> getAttributeList() {
        return attributeList;
    }
    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }
    public List<Skill> getSkillList() {
        return skillList;
    }
    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }
    public List<Talent> getTalentList() {
        return talentList;
    }
    public void setTalentList(List<Talent> talentList) {
        this.talentList = talentList;
    }
    public void addTalents(List<Talent> talents) {
        this.talentList.addAll(talents);
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

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("CharacterSheet {\n")
            .append("exp = ").append(exp).append("\n")
            .append(subrace).append("\n")
            .append(prof).append("\n")
            .append("Attributes = [\n");
        for (Attribute attribute : attributeList) {
            ret.append("\t").append(attribute).append("\n");
        }
        ret.append("]\n")
            .append("Skills = [\n");
        for (Skill skill : skillList) {
            ret.append("\t").append(skill).append("\n");
        }
        ret.append("]\n")
            .append("Talents = [\n");
        for (Talent talent : talentList) {
            ret.append("\t").append(talent).append("\n");
        }
        ret.append("]");

        return ret.toString();
    }
}