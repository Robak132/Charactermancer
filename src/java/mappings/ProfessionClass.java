package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROFESSIONS_CLASSES")
public class ProfessionClass {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    public ProfessionClass() {}
    public ProfessionClass(int ID, String name) {
        this.ID = ID;
        this.name = name;
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

    @Override
    public String toString() {
        return String.format("Class {ID = %d, name = %s}", ID, name);
    }
}