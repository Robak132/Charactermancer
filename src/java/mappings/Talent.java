package mappings;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.DiscriminatorOptions;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("CASE WHEN IDBASE IS NOT NULL THEN 'SINGLE' ELSE 'GROUP' END")
@DiscriminatorOptions(force=true)
@Table(name = "TALENTS")
public abstract class Talent {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    protected String name;

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

    public abstract void setCurrentLvl(int currentLvl);
    public abstract void linkAttributeMap(Map<Integer, Attribute> attributeMap);
    public abstract boolean isAdvanceable();
    public abstract void setAdvanceable(boolean advanceable);

    public abstract List<TalentSingle> getSingleTalents();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Talent talent = (Talent) o;
        return getID() == talent.getID() && Objects.equals(name, talent.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }
}