package mappings;

import java.util.Objects;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("CASE WHEN IDBASE IS NOT NULL THEN 'SINGLE' ELSE 'GROUP' END")
@DiscriminatorOptions(force=true)
@Table(name = "SKILLS")
public abstract class Skill {
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

    public abstract boolean isAdv();
    public abstract void setAdvValue(int advValue);
    public abstract boolean isAdvanceable();
    public abstract void setAdvanceable(boolean advanceable);
    public abstract boolean isEarning();
    public abstract void setEarning(boolean earning);
    public abstract void linkAttributeMap(Map<Integer, Attribute> attributeMap);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Skill skill = (Skill) o;
        return getID() == skill.getID() || Objects.equals(name, skill.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }
}