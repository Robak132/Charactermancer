package mappings;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "TALENTS_BASE")
public class TalentBase {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "NAMEENG")
    private String nameEng;
    @Column(name = "TEST")
    private String test;
    @Column(name = "DESCR")
    private String desc;
    @Column(name = "CONST_LVL")
    private Integer constLvl;

    @ManyToOne
    @JoinColumn(name = "MAX_LVL")
    private BaseAttribute attr;

    @Transient
    private Attribute linkedAttribute;

    public TalentBase() {}
    public TalentBase(int ID, String name, String nameEng, String test, String desc) {
        this.ID = ID;
        this.name = name;
        this.nameEng = nameEng;
        this.test = test;
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
    public String getNameEng() {
        return nameEng;
    }
    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }
    public BaseAttribute getAttr() {
        return attr;
    }
    public void setAttr(BaseAttribute attr) {
        this.attr = attr;
    }
    public String getTest() {
        return test;
    }
    public void setTest(String test) {
        this.test = test;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public Integer getConstLvl() {
        return constLvl;
    }
    public void setConstLvl(int constLvl) {
        this.constLvl = constLvl;
    }
    public int getMaxLvl() {
        if (linkedAttribute!=null) {
            return linkedAttribute.getBonus();
        } else {
            return constLvl;
        }
    }

    public void linkAttributeMap(Map<Integer, Attribute> attributeMap) {
        if (attr!=null) {
            this.linkedAttribute = attributeMap.getOrDefault(attr.getID(), null);
        }
    }
}