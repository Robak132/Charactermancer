package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RACE_ATTRIBUTES")
public class RaceAttribute {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "VALUE")
    private int value;
    @Column(name = "ID_ATTR")
    private int attribute;

    public RaceAttribute(int ID, int value) {
        this.ID = ID;
        this.value = value;
    }
    public RaceAttribute() {
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public int getAttribute() {
        return attribute;
    }
    public void setAttribute(int baseAttribute) {
        this.attribute = baseAttribute;
    }
}