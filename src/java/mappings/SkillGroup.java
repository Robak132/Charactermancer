package mappings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
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
    @OrderBy("name")
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

    @Override
    public List<SkillSingle> getSingleSkills() {
        List<SkillSingle> outputList = new ArrayList<>();
        for (Skill skill : skills) {
            outputList.addAll(skill.getSingleSkills());
        }
        return outputList;
    }
    public SkillSingle getRndSkill() {
        return (SkillSingle) Dice.randomItem(getSingleSkills());
    }

    public List<Skill> getSkills() {
        return skills;
    }
    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    @Override
    public boolean isAdv() {
        return skills.stream().allMatch(Skill::isAdv);
    }

    @Override
    public void setAdvValue(int advValue) {
        skills.forEach(skill -> skill.setAdvValue(advValue));
    }

    @Override
    public boolean isAdvanceable() {
        return skills.stream().allMatch(Skill::isAdvanceable);
    }
    @Override
    public void setAdvanceable(boolean advanceable) {
        skills.forEach(skill -> skill.setAdvanceable(advanceable));
    }

    @Override
    public boolean isEarning() {
        return skills.stream().allMatch(Skill::isEarning);
    }
    @Override
    public void setEarning(boolean earning) {
        skills.forEach(e->e.setEarning(earning));
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
