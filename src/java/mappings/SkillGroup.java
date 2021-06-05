package mappings;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;
import java.util.Map;

import tools.Dice;

@Entity
@DiscriminatorValue("GROUP")
public class SkillGroup extends Skill{
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "LOCKED")
    private boolean locked;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name= "SKILLS_HIERARCHY",
            joinColumns = @JoinColumn(name = "IDPARENT"),
            inverseJoinColumns = @JoinColumn(name = "IDCHILD"))
    @OrderBy(value="name")
    private List<Skill> skills;

    public SkillGroup() {
        // Needed for Hibernate/JPA
    }

    @Override
    public int getID() {
        return ID;
    }
    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Skill getFirstSkill() {
        // TODO: make returns only SkillSingle
        return skills.get(0);
    }
    public Skill getRndSkill() {
        // TODO: make returns only SkillSingle
        return (Skill) Dice.randomItem(skills);
    }

    public List<Skill> getSkills() {
        // TODO: make returns only SkillSingle
        return skills;
    }
    public int countSkills() {
        return skills.size();
    }
    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    @Override
    public boolean isAdv() {
        boolean adv = true;
        for (Skill skill : skills) {
            if (!skill.isAdv())
                adv = false;
        }
        return adv;
    }
    @Override
    public void setAdvValue(int advValue) {
        for (Skill skill : skills) {
            skill.setAdvValue(advValue);
        }
    }
    @Override
    public void linkAttributeMap(Map<Integer, Attribute> attributeMap) {
        for (Skill skill : skills) {
            skill.linkAttributeMap(attributeMap);
        }
    }
    @Override
    public String toString() {
        return String.format("SkillGroup {ID = %d, name = %s}", ID, name);
    }
}
