package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "RACE_ATTRIBUTES")
public class Attribute {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "VALUE")
    private int baseValue;
    @Transient
    private int charValue;
    @Transient
    private int advValue;
    @Transient
    private int totalValue;

    @ManyToOne
    @JoinColumn(name = "IDATTR")
    private BaseAttribute baseAttribute;

    public Attribute(int ID, int value) {
        this.ID = ID;
        this.baseValue = value;
    }
    public Attribute() {
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getBaseValue() {
        updateAll();
        return baseValue;
    }
    public void setBaseValue(int baseValue) {
        this.baseValue = baseValue;
        updateAll();
    }
    public int getCharValue() {
        updateAll();
        return charValue;
    }
    public void setCharValue(int charValue) {
        this.charValue = charValue;
        updateAll();
    }
    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }
    public int getAdvValue() {
        updateAll();
        return advValue;
    }
    public void setAdvValue(int advValue) {
        this.advValue = advValue;
        updateAll();
    }
    public int getTotalValue() {
        updateAll();
        return totalValue;
    }
    public BaseAttribute getBaseAttribute() {
        return baseAttribute;
    }
    public void setBaseAttribute(BaseAttribute baseAttribute) {
        this.baseAttribute = baseAttribute;
    }

    public String getName() {
        return baseAttribute.getName();
    }

    private void updateAll() {
        totalValue=baseValue+advValue;
    }
}