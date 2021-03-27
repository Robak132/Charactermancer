package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SKILLS")
public class Skill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ADV")
    private boolean adv;
    @Column(name = "DESCR")
    private String descr;

    @ManyToOne
    @JoinColumn(name = "ATTR")
    private Attribute attr;

    public Skill() {}
    public Skill(int ID, String name, boolean adv, String descr) {
        this.ID = ID;
        this.name = name;
        this.adv = adv;
        this.descr = descr;
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
    public Attribute getAttr() {
        return attr;
    }
    public void setAttr(Attribute attr) {
        this.attr = attr;
    }
    public String getDescr() {
        return descr;
    }
    public void setDescr(String descr) {
        this.descr = descr;
    }
    public boolean isAdv() {
        return adv;
    }
    public void setAdv(boolean adv) {
        this.adv = adv;
    }
}