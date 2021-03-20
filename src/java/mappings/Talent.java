package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TALENTS")
public class Talent {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "NAMEENG")
    private String nameEng;
    @Column(name = "MAXLVL")
    private String maxLvl;
    @Column(name = "TEST")
    private String test;
    @Column(name = "DESCR")
    private String desc;

    public Talent() {}
    public Talent(int ID, String name, String nameEng, String maxLvl, String test, String desc) {
        this.ID = ID;
        this.name = name;
        this.nameEng = nameEng;
        this.maxLvl = maxLvl;
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
    public String getMaxLvl() {
        return maxLvl;
    }
    public void setMaxLvl(String maxLvl) {
        this.maxLvl = maxLvl;
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
}