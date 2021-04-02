package mappings;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "TALENTS_GROUPED")
public class GroupTalent {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "GRP")
    private boolean group;
    @Transient
    private int currentLvl = 1;
    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private Talent baseTalent;

    public GroupTalent() {}
    public GroupTalent(int ID, String name, boolean group, int currentLvl, Talent baseTalent) {
        this.ID = ID;
        this.name = name;
        this.group = group;
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
    public boolean isGroup() {
        return group;
    }
    public void setGroup(boolean group) {
        this.group = group;
    }
    public int getCurrentLvl() {
        return currentLvl;
    }
    public void setCurrentLvl(int currentLvl) {
        this.currentLvl = currentLvl;
    }
    public Talent getBaseTalent() {
        return baseTalent;
    }
    public void setBaseTalent(Talent baseTalent) {
        this.baseTalent = baseTalent;
    }

    @Override
    public String toString() {
        if (name.equals(baseTalent.getName())) {
            return String.format("Talent {name = %s, lvl = %d}", name, currentLvl);
        } else {
            return String.format("Talent {name = %s (%s), lvl = %d}", name, baseTalent.getName(), currentLvl);
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
        GroupTalent that = (GroupTalent) o;
        return ID == that.ID && Objects.equals(name, that.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }
}