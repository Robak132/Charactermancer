package mappings;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import tools.Dice;

import javax.persistence.*;

@Entity
@DiscriminatorValue("GROUP")
public class TalentGroup extends Talent {
    @Id
    private int ID;
    @Column(name = "LOCKED")
    protected boolean locked;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name= "TALENTS_HIERARCHY",
            joinColumns = @JoinColumn(name = "ID_PARENT"),
            inverseJoinColumns = @JoinColumn(name = "ID_CHILD"))
    protected List<Talent> childTalents;

    public TalentGroup() {
        // Needed for Hibernate/JPA
    }

    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Talent getFirstTalent() {
        return childTalents.get(0);
    }
    public Talent getRndTalent() {
        return (Talent) Dice.randomItem(childTalents);
    }
    public int getRndTalentIndex() {
        return Dice.randomInt(0, childTalents.size() - 1);
    }

    public List<Talent> getChildTalents() {
        return childTalents;
    }
    public int countChildTalents() {
        return childTalents.size();
    }
    public void setChildTalents(List<Talent> talents) {
        this.childTalents = talents;
    }

    @Override
    public void setCurrentLvl(int currentLvl) {
        if (childTalents.size() > 0) {
            childTalents.forEach(e->e.setCurrentLvl(currentLvl));
        }
    }
    @Override
    public void linkAttributeMap(Map<Integer, Attribute> attributeMap) {
        for (Talent talent : childTalents) {
            talent.linkAttributeMap(attributeMap);
        }
    }

    @Override
    public String toString() {
        return String.format("GroupTalent {ID = %d, name = %s [%d], locked = %s}", ID, name, childTalents.size(), locked);
    }
}
