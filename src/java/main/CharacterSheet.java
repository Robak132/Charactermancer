package main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;
import mappings.Attribute;
import mappings.Profession;
import mappings.ProfessionCareer;
import mappings.ProfessionClass;
import mappings.Race;
import mappings.Race.Size;
import mappings.Skill;
import mappings.SkillSingle;
import mappings.Subrace;
import mappings.Talent;
import mappings.TalentSingle;
import org.json.JSONArray;
import org.json.JSONObject;

public class CharacterSheet {
    public JPanel mainPanel;
    public Connection connection;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final int MOVE = 11;

    private int exp;
    private int healthPoints;
    private Subrace subrace;
    private Profession profession;
    private Map<Integer, Attribute> attributes = new ConcurrentHashMap<>();
    private Map<Integer, SkillSingle> skills = new ConcurrentHashMap<>();
    private Map<Integer, TalentSingle> talents = new ConcurrentHashMap<>();
    private List<SkillSingle> skillList = new ArrayList<>();
    private List<TalentSingle> talentList = new ArrayList<>();

    public CharacterSheet(Connection connection) {
        this.connection = connection;
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

    public Size getSize() {
        return subrace.getBaseRace().getSize();
    }

    public Profession getProfession() {
        return profession;
    }
    public ProfessionCareer getProfessionCareer() {
        return profession.getProfessionCareer();
    }
    public ProfessionClass getProfessionClass() {
        return profession.getProfessionCareer().getProfessionClass();
    }
    public void setProfession(Profession prof) {
        this.profession = prof;
    }

    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }
    public Attribute getAttribute(int index) {
        return attributes.get(index);
    }
    public void setAttributes(Map<Integer, Attribute> attributes) {
        this.attributes = attributes;
    }
    public void addAttribute(Integer key, Attribute value) {
        attributes.put(key, value);
    }

    public void addSkill(SkillSingle skill) {
        skills.put(skill.getID(), skill);
    }
    public Map<Integer, SkillSingle> getSkills() {
        return skills;
    }

    public void addTalent(TalentSingle talent) {
        talents.put(talent.getID(), talent);
    }
    public Map<Integer, TalentSingle> getTalents() {
        return talents;
    }
    public void setTalentList(List<TalentSingle> talentList) {
        this.talentList = talentList;
    }
    public void addTalents(List<TalentSingle> talents) {
        this.talentList.addAll(talents);
    }

    public int getExp() {
        return exp;
    }
    public void setExp(int exp) {
        pcs.firePropertyChange("exp", this.exp, exp);
        this.exp = exp;
    }
    public void addExp(int exp) {
        setExp(this.exp + exp);
    }

    public int getMaxHealthPoints() {
        if (!attributes.containsKey(3) || !attributes.containsKey(4) || !attributes.containsKey(9)) {
            return 0;
        }

        int value = (attributes.get(4).getTotalValue() / 10) * 2 + attributes.get(9).getTotalValue() / 10;
        if (subrace.getBaseRace().getSize() == Race.Size.NORMAL) {
            value += attributes.get(3).getTotalValue() / 10;
        }
        return value;
    }
    public int getHealthPoints() {
        return healthPoints;
    }
    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }
    public void resetHealthPoints() {
        this.healthPoints = getMaxHealthPoints();
    }

    public String printAttributes() {
        StringBuilder ret = new StringBuilder()
            .append("Attributes = [\n");
        for (Attribute attribute : attributes.values()) {
            ret.append("\t").append(attribute).append("\n");
        }
        System.out.println(ret);
        return ret.toString();
    }

    public void addObserver(String propertyName, PropertyChangeListener l) {
        pcs.addPropertyChangeListener(propertyName, l);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("CharacterSheet {\n")
            .append("exp = ").append(exp).append("\n")
            .append(subrace).append("\n")
            .append(profession).append("\n")
            .append(printAttributes())
            .append("]\n")
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
    public void ToJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("exp", exp);

        JSONObject subraceObject = new JSONObject();
        subraceObject.put("ID", subrace.getID());
        subraceObject.put("race_ID", subrace.getBaseRace().getID());
        subraceObject.put("name", subrace.getName());
        jsonObject.put("subrace", subraceObject);

        JSONObject professionObject = new JSONObject();
        professionObject.put("ID", profession.getID());
        professionObject.put("name", profession.getName());
        jsonObject.put("profession", professionObject);

        JSONArray attributesArray = new JSONArray();
        for (Attribute attribute : attributes.values()) {
            JSONObject attributeJSON = new JSONObject();
            attributeJSON.put("ID", attribute.getID());
            attributeJSON.put("name", attribute.getName());
            attributeJSON.put("base_value", attribute.getBaseValue());
            attributeJSON.put("rnd_value", attribute.getRndValue());
            attributeJSON.put("adv_value", attribute.getAdvValue());
            attributesArray.put(attributeJSON);
        }
        jsonObject.put("attributes", attributesArray);

        JSONArray skillsArray = new JSONArray();
        for (SkillSingle skill : skills.values()) {
            if (skill.getAdvValue() > 0) {
                JSONObject skillJSON = new JSONObject();
                skillJSON.put("ID", skill.getID());
                skillJSON.put("base_skill_ID", skill.getBaseSkill().getID());
                skillJSON.put("name", skill.getName());
                skillJSON.put("earning", skill.isEarning());
                skillJSON.put("advanceable", skill.isAdvanceable());
                skillJSON.put("adv_value", skill.getAdvValue());
                skillsArray.put(skillJSON);
            }
        }
        jsonObject.put("skills", skillsArray);

        JSONArray talentsArray = new JSONArray();
        for (TalentSingle talent : talents.values()) {
            JSONObject talentJSON = new JSONObject();
            talentJSON.put("ID", talent.getID());
            talentJSON.put("name", talent.getName());
            talentJSON.put("lvl", talent.getCurrentLvl());
            talentJSON.put("advanceable", talent.isAdvanceable());
            talentsArray.put(talentJSON);
        }
        jsonObject.put("talents", talentsArray);

        try (PrintWriter file = new PrintWriter(Paths.get("src/resources/test.json").toFile())) {
            file.println(jsonObject.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject.toString(4));
    }
    public void loadJSON(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject jsonObject = new JSONObject(content);
            exp = jsonObject.getInt("exp");

            JSONObject subraceObject = jsonObject.getJSONObject("subrace");
            int ID = subraceObject.getInt("ID");
            String name = subraceObject.getString("name");
            subrace = connection.getSubrace(ID, name);

            JSONObject professionObject = jsonObject.getJSONObject("profession");
            ID = professionObject.getInt("ID");
            name = professionObject.getString("name");
            profession = connection.getProfession(ID, name);

            JSONArray attributesArray = jsonObject.getJSONArray("attributes");
            for (int i=0; i<attributesArray.length(); i++) {
                JSONObject object = attributesArray.getJSONObject(i);
                ID = object.getInt("ID");
                name = object.getString("name");
                Attribute attribute = connection.getAttribute(ID, name);
                attribute.setBaseValue(object.getInt("base_value"));
                attribute.setRndValue(object.getInt("rnd_value"));
                attribute.setAdvValue(object.getInt("adv_value"));
                attributes.put(ID, attribute);
            }

            JSONArray skillsArray = jsonObject.getJSONArray("skills");
            for (int i=0; i<skillsArray.length(); i++) {
                JSONObject object = skillsArray.getJSONObject(i);
                ID = object.getInt("ID");
                name = object.getString("name");
                SkillSingle skill = connection.getSkill(ID, name);
                skill.setAdvValue(object.getInt("adv_value"));
                skill.setAdvanceable(object.getBoolean("advanceable"));
                skill.setEarning(object.getBoolean("earning"));
                skills.put(ID, skill);
            }

            JSONArray talentsArray = jsonObject.getJSONArray("talents");
            for (int i=0; i<talentsArray.length(); i++) {
                JSONObject object = talentsArray.getJSONObject(i);
                ID = object.getInt("ID");
                name = object.getString("name");
                TalentSingle talent = connection.getTalent(ID, name);
                talent.setCurrentLvl(object.getInt("lvl"));
                talent.setAdvanceable(object.getBoolean("advanceable"));
                talents.put(ID, talent);
            }

            System.out.println(jsonObject.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}