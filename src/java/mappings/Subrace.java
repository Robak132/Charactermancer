package mappings;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SUBRACES")
public class Subrace {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "RANDOM_TALENTS")
    private int randomTalents;
    @Column
    private boolean def;

    @ManyToOne
    @JoinColumn(name = "IDRACE")
    private Race baseRace;

    public Subrace() {}
    public Subrace(int ID, String name, int randomTalents, boolean def) {
        this.ID = ID;
        this.name = name;
        this.randomTalents = randomTalents;
        this.def = def;
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
    public int getRandomTalents() {
        return randomTalents;
    }
    public void setRandomTalents(int extraTalents) {
        this.randomTalents = extraTalents;
    }
    public boolean isDef() {
        return def;
    }
    public void setDef(boolean def) {
        this.def = def;
    }

    public Race getBaseRace() {
        return baseRace;
    }
    public void setBaseRace(Race baseRace) {
        this.baseRace = baseRace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subrace subrace = (Subrace) o;
        return ID == subrace.ID && Objects.equals(name, subrace.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }

    @Override
    public String toString() {
        if (name.equals(baseRace.getName())) {
            return String.format("Subrace {ID = %d, name = %s}", ID, name);
        }
        return String.format("Subrace {ID = %d, name = %s [%s]}", ID, name, baseRace.getName());
    }
}