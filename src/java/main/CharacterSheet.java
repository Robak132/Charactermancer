package main;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
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

public class CharacterSheet {
    public JPanel mainPanel;
    private final PropertyChangeSupport pcs = new  PropertyChangeSupport(this);

    private final int MOVE = 11;
    private int healthPoints;
    private Subrace subrace;
    private Profession prof;
    private Map<Integer, Attribute> attributes = new ConcurrentHashMap<>();
    private List<SkillSingle> skillList = new ArrayList<>();
    private List<TalentSingle> talentList = new ArrayList<>();
    private int exp;

    public CharacterSheet() {}

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
        return prof;
    }
    public ProfessionCareer getProfessionCareer() {
        return prof.getProfessionCareer();
    }
    public ProfessionClass getProfessionClass() {
        return prof.getProfessionCareer().getProfessionClass();
    }
    public void setProfession(Profession prof) {
        this.prof = prof;
    }

    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<Integer, Attribute> attributes) {
        this.attributes = attributes;
    }
    public void addAttribute(Integer key, Attribute value) {
        attributes.put(key, value);
    }

    public List<SkillSingle> getSkillList() {
        return skillList;
    }
    public void setSkillList(List<SkillSingle> skillList) {
        this.skillList = skillList;
    }
    public List<TalentSingle> getTalentList() {
        return talentList;
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
            .append(prof).append("\n")
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
}