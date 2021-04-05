package mappings;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROFESSIONS")
public class Profession {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "PROFESSION")
    private String name;
    @Column(name = "LEVEL")
    private int level;

    @ManyToOne
    @JoinColumn(name = "IDCAREER")
    private ProfessionCareer career;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="PROF_ATTRIBUTES",
            joinColumns = @JoinColumn(name = "IDPROF"),
            inverseJoinColumns = @JoinColumn(name = "IDATTR"))
    private List<BaseAttribute> baseAttributes;

    @Fetch(FetchMode.JOIN)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name= "IDPROF")
    private List<ProfSkill> profSkills;

    public Profession() {}
    public Profession(int ID, String name, int level) {
        this.ID = ID;
        this.name = name;
        this.level = level;
    }

    public int getID() {
        return ID;
    }
    public void setID(int id) {
        this.ID = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String profession) {
        this.name = profession;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public ProfessionCareer getCareer() {
        return career;
    }
    public void setCareer(ProfessionCareer career) {
        this.career = career;
    }

    public List<BaseAttribute> getAttributes() {
        return baseAttributes;
    }
    public Integer[] getSimpleAttributes() {
        Integer[] intAttributes = new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (BaseAttribute baseAttribute : baseAttributes) {
            intAttributes[baseAttribute.getID()-1] = 1;
        }
        return intAttributes;
    }
    public boolean hasAttribute(int ID) {
        for (BaseAttribute attr : baseAttributes) {
            if (attr.getID() == ID) {
                return true;
            }
        }
        return false;
    }
    public void setAttributes(List<BaseAttribute> baseAttributes) {
        this.baseAttributes = baseAttributes;
    }

    public List<ProfSkill> getProfSkills() {
        return profSkills;
    }
    public List<SkillGroup> getProfSkills(List<Attribute> attributes) {
        List<SkillGroup> tempList = new ArrayList<>();
        for (ProfSkill skill : profSkills) {
            SkillGroup tempSkill = skill.getSkill();
            tempList.add(tempSkill);
            for (Skill singleSkill : tempSkill.getSkills()) {
                for (Attribute attribute : attributes) {
                    if (singleSkill.getAttr().equals(attribute.getBaseAttribute())) {
                        singleSkill.setLinkedAttribute(attribute);
                        singleSkill.setAdvanceable(true);
                        break;
                    }
                }
            }
        }
        return tempList;
    }

    public List<SkillGroup> getProfSkills(List<Attribute> attributes, List<Skill> skills) {
        List<SkillGroup> tempList = new ArrayList<>();
        for (ProfSkill skill : profSkills) {
            SkillGroup tempSkill = skill.getSkill();
            tempList.add(tempSkill);
            for (int i = 0; i < tempSkill.getSkills().size(); i++) {
                Skill singleSkill = tempSkill.getSkills().get(i);
                for (Skill compareSkill : skills) {
                    if (compareSkill.equals(singleSkill)) {
                        tempSkill.getSkills().set(i, compareSkill);
                        break;
                    }
                }
                for (Attribute attribute : attributes) {
                    if (singleSkill.getAttr().equals(attribute.getBaseAttribute())) {
                        tempSkill.getSkills().get(i).setLinkedAttribute(attribute);
                        tempSkill.getSkills().get(i).setAdvanceable(true);
                        break;
                    }
                }
            }
        }
        return tempList;
    }
    public void setProfSkills(List<ProfSkill> profSkills) {
        this.profSkills = profSkills;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Profession)) {
            return false;
        }
        Profession c = (Profession) o;
        return ID == c.ID;
    }

    @Override
    public String toString() {
        return String.format("Profession {\n" +
                "\t%s\n" +
                "\t%s\n" +
                "\tProfession {ID = %d, name = %s}\n" +
                "}", career.getProfessionClass(), career, ID, name);
    }
}