package mappings;

import javax.persistence.*;

@Entity
@Table(name = "SKILLS")
public class Skill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private BaseSkill baseSkill;

    @Transient
    private boolean advanceable = false;
    @Transient
    private Attribute linkedAttribute = null;
    @Transient
    private int advValue = 0;
    @Transient
    private int totalValue = 0;

    public Skill() {}
    public Skill(int ID, String name, BaseSkill baseSkill) {
        this.ID = ID;
        this.name = name;
        this.baseSkill = baseSkill;
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
    public BaseSkill getBaseSkill() {
        return baseSkill;
    }
    public void setBaseSkill(BaseSkill baseSkill) {
        this.baseSkill = baseSkill;
    }

    public boolean isAdv() {
        return baseSkill.isAdv();
    }
    public BaseAttribute getAttr() {
        return baseSkill.getAttr();
    }
    public void setAttr(BaseAttribute baseAttribute) {
        baseSkill.setAttr(baseAttribute);
    }

    public boolean isAdvanceable() {
        return advanceable;
    }
    public void setAdvanceable(boolean advanceable) {
        this.advanceable = advanceable;
    }
    public int getStartValue() {
        return linkedAttribute.getTotalValue();
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

    public Attribute getLinkedAttribute() {
        return linkedAttribute;
    }
    public void setLinkedAttribute(Attribute linkedAttribute) {
        this.linkedAttribute = linkedAttribute;
    }

    public void clean() {
        advValue = 0;
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
            return String.format("Skill {ID = %3d, name = %s, AV = %d, TV = %d}", ID, name, advValue, totalValue);
        } else {
            return String.format("Skill {ID = %3d, name = %s [%s], AV = %d, TV = %d}", ID, name, baseSkill.getName(), advValue, totalValue);
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
        Skill skill = (Skill) o;
        return ID == skill.ID;
    }
}