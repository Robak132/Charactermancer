package mappings;

import javax.persistence.*;
import java.util.Map;

@Entity
@DiscriminatorValue("SINGLE")
public class SkillSingle extends Skill {
    @Id
    private int ID;

    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private SkillBase baseSkill;

    @Transient
    private int advValue=0;
    @Transient
    private boolean advanceable;
    @Transient
    private boolean earning;

    public SkillSingle() {
        // Needed for Hibernate/JPA
    }

    @Override
    public int getID() {
        return ID;
    }
    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    public int getAdvValue() {
        return advValue;
    }
    public boolean isAdvanceable() {
        return advanceable;
    }
    public void setAdvanceable(boolean advanceable) {
        this.advanceable = advanceable;
    }
    public SkillBase getBaseSkill() {
        return baseSkill;
    }
    public void setBaseSkill(SkillBase baseSkill) {
        this.baseSkill = baseSkill;
    }
    public String getAttrName() {
        return baseSkill.getAttr().getName();
    }
    public boolean isEarning() {
        return earning;
    }
    public void setEarning(boolean earning) {
        this.earning = earning;
    }

    public Integer getTotalValue() {
        try {
            return baseSkill.getLinkedAttribute().getTotalValue() + advValue;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void linkAttributeMap(Map<Integer, Attribute> attributeMap) {
        baseSkill.linkAttributeMap(attributeMap);
    }
    @Override
    public boolean isAdv() {
        return baseSkill.isAdv();
    }
    @Override
    public void setAdvValue(int advValue) {
        this.advValue = advValue;
    }

    @Override
    public String toString() {
        return String.format("SingleSkill {ID = %d, name = %s, AV = %d}", ID, name, advValue);
    }
}
