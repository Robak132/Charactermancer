package mappings;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "TALENTS")
public class Talent {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private BaseTalent baseTalent;

    @Transient
    private int currentLvl = 0;
    @Transient
    private boolean advanceable = false;
    @Transient
    private Attribute linkedAttribute = null;

    public Talent() {}
    public Talent(int ID, String name, int currentLvl, BaseTalent baseTalent) {
        this.ID = ID;
        this.name = name;
        this.currentLvl = currentLvl;
        this.baseTalent = baseTalent;
    }

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
    public int getCurrentLvl() {
        return currentLvl;
    }
    public void setCurrentLvl(int currentLvl) {
        this.currentLvl = currentLvl;
    }
    public Attribute getLinkedAttribute() {
        return linkedAttribute;
    }
    public void setLinkedAttribute(Attribute linkedAttribute) {
        this.linkedAttribute = linkedAttribute;
    }
    public boolean isAdvanceable() {
        return advanceable;
    }
    public void setAdvanceable(boolean advanceable) {
        this.advanceable = advanceable;
    }

    public Integer getMax() {
        if (linkedAttribute != null) {
            return linkedAttribute.getBonus();
        } else if (baseTalent.getConstLvl() != null) {
            return baseTalent.getConstLvl();
        }
        return null;
    }
    public String getMaxString() {
        if (getMax() != null) {
            return String.valueOf(getMax());
        }
        return "";
    }
    public BaseAttribute getAttr() {
        return baseTalent.getAttr();
    }
    public void setAttr(BaseAttribute baseAttribute) {
        baseTalent.setAttr(baseAttribute);
    }
    public BaseTalent getBaseTalent() {
        return baseTalent;
    }
    public void setBaseTalent(BaseTalent baseTalent) {
        this.baseTalent = baseTalent;
    }

    @Override
    public String toString() {
        if (name.equals(baseTalent.getName())) {
            return String.format("Talent {ID = %d, name = %s, lvl = %d}", ID, name, currentLvl);
        } else {
            return String.format("Talent {ID = %d, name = %s [%s], lvl = %d}", ID, name, baseTalent.getName(), currentLvl);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Talent that = (Talent) o;
        return ID == that.ID && Objects.equals(name, that.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }
}