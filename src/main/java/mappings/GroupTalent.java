package mappings;

import javax.persistence.*;

@Entity
@Table(name = "GROUPTALENTS")
public class GroupTalent {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "IDBASE")
    private int base;

    public GroupTalent() {}
    public GroupTalent(int ID, String name, int base) {
        this.ID = ID;
        this.name = name;
        this.base = base;
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
    public int getBase() {
        return base;
    }
    public void setBase(int base) {
        this.base = base;
    }
}