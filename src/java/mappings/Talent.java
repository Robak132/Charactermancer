package mappings;

import java.util.Map;
import java.util.Objects;
import javax.persistence.*;

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