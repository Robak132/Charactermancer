package mappings;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import tools.Dice;

@Entity
@Table(name = "SKILLS_GROUPS")
public class SkillGroup {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="SKILLS_LINK",
            joinColumns = @JoinColumn(name = "IDGROUP"),
            inverseJoinColumns = @JoinColumn(name = "IDSKILL"))
    private List<Skill> skills;

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Skill getFirstSkill() {
        return skills.get(0);
    }
    public Skill getRndSkill() {
        return (Skill) Dice.randomItem(skills);
    }

    public List<Skill> getSkills() {
        return skills;
    }
    public void cleanSkills() {
        for (Skill skill : skills) {
            skill.clean();
        }
    }
    public int countSkills() {
        return skills.size();
    }
    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
}
