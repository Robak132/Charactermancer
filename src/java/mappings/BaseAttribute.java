package mappings;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ATTRIBUTES")
public class BaseAttribute {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    public BaseAttribute(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }
    public BaseAttribute() {}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseAttribute that = (BaseAttribute) o;
        return ID == that.ID && Objects.equals(name, that.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }
}