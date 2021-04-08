package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SKILLS_BASE")
public class BaseSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ADV")
    private boolean adv;
    @Column(name = "DESCR")
    private String desc;

    @ManyToOne
    @JoinColumn(name = "ATTR")
    private BaseAttribute attr;

    public BaseSkill() {}
    public BaseSkill(int ID, String name, boolean adv, String desc) {
        this.ID = ID;
        this.name = name;
        this.adv = adv;
        this.desc = desc;
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
    public BaseAttribute getAttr() {
        return attr;
    }
    public void setAttr(BaseAttribute attr) {
        this.attr = attr;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public boolean isAdv() {
        return adv;
    }
    public void setAdv(boolean adv) {
        this.adv = adv;
    }
}