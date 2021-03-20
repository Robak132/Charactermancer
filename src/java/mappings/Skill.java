package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SKILLS")
public class Skill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ATTR")
    private String attr;
    @Column(name = "ADV")
    private boolean adv;
    @Column(name = "DESCR")
    private String descr;

    public Skill() {}
    public Skill(int ID, String name, String attr, boolean adv, String descr) {
        this.ID = ID;
        this.name = name;
        this.attr = attr;
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
    public String getAttr() {
        return attr;
    }
    public void setAttr(String attr) {
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