package mappings;

import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name = "ATTRIBUTES")
public class Attribute {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    @Transient
    private int baseValue;
    @Transient
    private int rndValue;
    @Transient
    private int advValue;
    @Transient
    private int totalValue;

    public Attribute(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }
    public Attribute() {}

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

    public int getBaseValue() {
        return baseValue;
    }
    public void setBaseValue(int baseValue) {
        this.baseValue = baseValue;
        updateAll();
    }
    public int getRndValue() {
        return rndValue;
    }
    public void setRndValue(int rndValue) {
        this.rndValue = rndValue;
        updateAll();
    }
    public int getAdvValue() {
        return advValue;
    }
    public void setAdvValue(int advValue) {
        this.advValue = advValue;
        updateAll();
    }
    public int getTotalValue() {
        return totalValue;
    }

    private void updateAll() {
        totalValue=baseValue+ rndValue +advValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Attribute attribute = (Attribute) o;
        return ID == attribute.ID && Objects.equals(name, attribute.name);
    }

    @Override
    public String toString() {
        return String.format("Attribute {ID = %d, name = %3s, BV = %3d, RV = %3d, AV = %3d, TV = %3d}", ID, name, baseValue, rndValue, advValue, totalValue);
    }
}