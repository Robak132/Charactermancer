package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SUBRACES")
public class Subrace {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "EXTRA_TALENTS")
    private int extraTalents;
    @Column
    private Integer def;

    public Subrace() {}
    public Subrace(int ID, String name, int extraTalents, Integer def) {
        this.ID = ID;
        this.name = name;
        this.extraTalents = extraTalents;
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
    public int getExtraTalents() {
        return extraTalents;
    }
    public void setExtraTalents(int extraTalents) {
        this.extraTalents = extraTalents;
    }
    public boolean isDef() {
        return def==1;
    }
    public void setDef(Integer def) {
        this.def = def;
    }
}