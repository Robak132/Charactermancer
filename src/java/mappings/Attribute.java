package mappings;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import tools.Dice;

@Entity
@Table(name = "ATTRIBUTES")
public class Attribute {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ROLLABLE")
    private boolean rollable;
    @Column(name = "IMPORTANCE")
    private int importance;

    @Transient
    private int baseValue;
    @Transient
    private int rndValue;
    @Transient
    private int advValue;

    public static int MOVE = 11;

    public Attribute(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }
    public Attribute() {}

    public int roll() {
        rndValue = Dice.randomDice(2, 10);
        return rndValue;
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
    public boolean isRollable() {
        return rollable;
    }
    public void setRollable(boolean rollable) {
        this.rollable = rollable;
    }
    public int getImportance() {
        return importance;
    }
    public void setImportance(int importance) {
        this.importance = importance;
    }

    public int getBaseValue() {
        return baseValue;
    }
    public void setBaseValue(int baseValue) {
        this.baseValue = baseValue;
    }
    public int getRndValue() {
        return rndValue;
    }
    public void setRndValue(int rndValue) {
        this.rndValue = rndValue;
    }
    public int getAdvValue() {
        return advValue;
    }
    public void setAdvValue(int advValue) {
        this.advValue = advValue;
    }
    public int incAdvValue() {
        advValue++;
        return advValue;
    }
    public int getTotalValue() {
        return baseValue+rndValue+advValue;
    }
    public int getBonus() {
        return getTotalValue() / 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute that = (Attribute) o;
        return ID == that.ID && Objects.equals(name, that.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }

    @Override
    public String toString() {
        return String.format("Attribute {ID = %3d, name = %3s, BV = %3d, RV = %3d, AV = %3d, TV = %3d}",
                ID, name, baseValue, rndValue, advValue, baseValue+rndValue+advValue);
    }
}