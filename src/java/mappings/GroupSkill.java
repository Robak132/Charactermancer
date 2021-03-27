package mappings;

import javax.persistence.*;

@Entity
@Table(name = "SKILLS_GROUPED")
public class GroupSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "GRP")
    private boolean group;
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
    public Attribute getAttr() {
        return base.getAttr();
    }
    public void setAttr(Attribute attribute) {
        base.setAttr(attribute);
    }

    public int getStartValue() {
        return base.getAttr().getTotalValue();
    }
    public int getAdvValue() {
        return advValue;
    }
    public void setAdvValue(int advValue) {
        this.advValue = advValue;
    }
    public int getTotalValue() {
        updateAll();
        return totalValue;
    }

    private void updateAll() {
        if (isAdv() && advValue == 0) {
            totalValue = 0;
        } else {
            totalValue = getStartValue() + advValue;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupSkill skill = (GroupSkill) o;
        return ID == skill.ID;
    }
}