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
    private Skill baseSkill;

    public GroupSkill() {}
    public GroupSkill(int ID, String name, Skill baseSkill, boolean group) {
        this.ID = ID;
        this.name = name;
        this.baseSkill = baseSkill;
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
    public Skill getBaseSkill() {
        return baseSkill;
    }
    public void setBaseSkill(Skill baseSkill) {
        this.baseSkill = baseSkill;
    }
    public boolean isGroup() {
        return group;
    }
    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isAdv() {
        return baseSkill.isAdv();
    }
    public Attribute getAttr() {
        return baseSkill.getAttr();
    }
    public void setAttr(Attribute attribute) {
        baseSkill.setAttr(attribute);
    }

    public int getStartValue() {
        return baseSkill.getAttr().getTotalValue();
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
    public String toString() {
        if (name.equals(baseSkill.getName())) {
            return String.format("Skill {ID = %d, name = %s, AV = %d, TV = %d}", ID, name, advValue, totalValue);
        } else {
            return String.format("Skill {ID = %d, name = %s [%s], AV = %d, TV = %d}", ID, name, baseSkill.getName(), advValue, totalValue);
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