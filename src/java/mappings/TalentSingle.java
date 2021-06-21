package mappings;

import java.util.List;
import java.util.Map;
import javax.persistence.*;

@Entity
@DiscriminatorValue("SINGLE")
public class TalentSingle extends Talent {
    @Id
    private int ID;

    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private TalentBase baseTalent;

    @Transient
    private int currentLvl;
    @Transient
    private boolean advanceable;

    public TalentSingle() {
        // Needed for Hibernate/JPA
    }

    public int getCurrentLvl() {
        return currentLvl;
    }
    public int getMaxLvl() {
        return baseTalent.getMaxLvl();
    }
    public TalentBase getBaseTalent() {
        return baseTalent;
    }
    public void setBaseTalent(TalentBase baseTalent) {
        this.baseTalent = baseTalent;
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
        this.currentLvl = currentLvl;
    }
    @Override
    public void linkAttributeMap(Map<Integer, Attribute> attributeMap) {
        baseTalent.linkAttributeMap(attributeMap);
    }
    @Override
    public boolean isAdvanceable() {
        return advanceable;
    }
    @Override
    public void setAdvanceable(boolean advanceable) {
        this.advanceable = advanceable;
    }
    @Override
    public List<TalentSingle> getSingleTalents() {
        return List.of(this);
    }

    public Integer getMax() {
        return baseTalent.getMaxLvl();
    }
    public String getMaxString() {
        return String.valueOf(getMax());
    }

    @Override
    public String toString() {
        return String.format("SingleTalent {ID = %d, name = %s, lvl = %d}", ID, name, currentLvl);
    }
}
