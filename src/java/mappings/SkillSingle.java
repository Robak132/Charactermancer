package mappings;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import tools.ColorPalette;

@Entity
@DiscriminatorValue("SINGLE")
public class SkillSingle extends Skill {
    @Id
    private int ID;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private SkillBase baseSkill;
    @Transient
    private int minimalValue;

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
    public int getMinimalValue() {
        return minimalValue;
    }
    public SkillBase getBaseSkill() {
        return baseSkill;
    }
    public void setBaseSkill(SkillBase baseSkill) {
        this.baseSkill = baseSkill;
    }
    public String getAttrName() {
        return baseSkill.getLinkedAttribute().getName();
    }
    public boolean isGrouped() {
        return baseSkill.isGrouped();
    }

    public Integer getTotalValue() {
        if (advValue != 0 || !baseSkill.isAdv()) {
            return baseSkill.getLinkedAttribute().getTotalValue() + advValue;
        } else {
            return 0;
        }
    }

    public Color getColor() {
        if (isEarning()) {
            return ColorPalette.BLUE;
        }
        if (isAdvanceable()) {
            return ColorPalette.HALF_GREEN;
        }
        if (isAdv() && getAdvValue() == 0) {
            return Color.RED;
        }
        return Color.BLACK;
    }

    @Override
    public void resetAdvValue() {
        advValue=minimalValue;
    }
    @Override
    public void updateMinimalValue() {
        this.minimalValue = advValue;
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
    public boolean isAdvanceable() {
        return advanceable;
    }
    @Override
    public void setAdvanceable(boolean advanceable) {
        this.advanceable = advanceable;
    }
    @Override
    public boolean isEarning() {
        return earning;
    }
    @Override
    public void setEarning(boolean earning) {
        this.earning = earning;
    }

    @Override
    public List<SkillSingle> getSingleSkills() {
        return List.of(this);
    }
    @Override
    public void update(Map<Integer, SkillSingle> skillMap) {}

    @Override
    public String toString() {
        return String.format("SingleSkill {ID = %d, name = %s, AV = %d}", ID, name, advValue);
    }
}
