package mappings;

import java.util.ArrayList;
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

    @Override
    public List<TalentSingle> getSingleTalents() {
        List<TalentSingle> outputList = new ArrayList<>();
        for (Talent talent : childTalents) {
            outputList.addAll(talent.getSingleTalents());
        }
        return outputList;
    }
    @Override
    public void update(Map<Integer, TalentSingle> talentMap) {
        List<Talent> tempList = new ArrayList<>();
        for (Talent talent : childTalents) {
            if (talentMap.containsKey(talent.getID())) {
                tempList.add(talentMap.get(talent.getID()));
            } else {
                tempList.add(talent);
            }
        }
        setChildTalents(tempList);
    }

    public Talent getRndTalent() {
        return (TalentSingle) Dice.randomItem(getSingleTalents());
    }

    public List<Talent> getChildTalents() {
        return childTalents;
    }
    public void setChildTalents(List<Talent> talents) {
        this.childTalents = talents;
    }

    @Override
    public int getID() {
        return ID;
    }
    @Override
    public void setID(int ID) {
        this.ID = ID;
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
    public boolean isAdvanceable() {
        boolean adv = true;
        for (Talent talent : childTalents) {
            if (!talent.isAdvanceable()) {
                adv = false;
                break;
            }
        }
        return adv;
    }
    @Override
    public void setAdvanceable(boolean advanceable) {
        childTalents.forEach(e->e.setAdvanceable(advanceable));
    }

    @Override
    public String toString() {
        return String.format("GroupTalent {ID = %d, name = %s [%d], locked = %s}", ID, name, childTalents.size(), locked);
    }
}
