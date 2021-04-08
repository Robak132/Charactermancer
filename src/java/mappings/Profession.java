package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name="PROF_ATTRIBUTES",
            joinColumns = @JoinColumn(name = "IDPROF"),
            inverseJoinColumns = @JoinColumn(name = "IDATTR"))
    private List<BaseAttribute> baseAttributes;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany
    @JoinColumn(name= "IDPROF")
    @OrderBy("S DESC")
    private List<ProfSkill> profSkills;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name="PROF_TALENTS",
            joinColumns = @JoinColumn(name = "IDPROF"),
            inverseJoinColumns = @JoinColumn(name = "IDTAL"))
    private List<TalentGroup> profTalents;

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
                singleSkill.setEarning(skill.isS());
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

    public List<TalentGroup> getProfTalents(List<Attribute> attributes) {
        for (TalentGroup talentGroup : profTalents) {
            for (Talent singleTalent : talentGroup.getTalents()) {
                for (Attribute attribute : attributes) {
                    if (singleTalent.getAttr() != null && singleTalent.getAttr().equals(attribute.getBaseAttribute())) {
                        singleTalent.setLinkedAttribute(attribute);
                        singleTalent.setAdvanceable(true);
                        break;
                    }
                }
            }
        }
        return profTalents;
    }
    public void setProfTalents(List<ProfSkill> profSkills) {
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