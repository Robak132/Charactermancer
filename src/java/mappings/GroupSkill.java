package mappings;

import javax.persistence.*;

@Entity
@Table(name = "GROUPSKILLS")
public class GroupSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "GRP")
    private boolean group;
    @Transient
    private int startValue=0;
    @Transient
    private int advValue=0;
    @Transient
    private int totalValue=0;
    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private Skill base;

    public GroupSkill() {}
    public GroupSkill(int ID, String name, Skill base, boolean group) {
        this.ID = ID;
        this.name = name;
        this.base = base;
        this.group = group;
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
    public Skill getBase() {
        return base;
    }
    public void setBase(Skill base) {
        this.base = base;
    }
    public boolean isGroup() {
        return group;
    }
    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isAdv() {
        return base.isAdv();
    }
    public String getAttr() {
        return base.getAttr();
    }

    public int getStartValue() {
        return startValue;
    }
    public void setStartValue(int startValue) {
        this.startValue = startValue;
        setTotalValue();
    }
    public int getAdvValue() {
        return advValue;
    }
    public void setAdvValue(int advValue) {
        this.advValue = advValue;
        setTotalValue();
    }
    public int getTotalValue() {
        return totalValue;
    }

    private void setTotalValue() {
        if (!base.isAdv() || advValue != 0) {
            this.totalValue = startValue + advValue;
        } else {
            this.totalValue = 0;
        }
    }
}