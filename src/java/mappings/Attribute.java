package mappings;

public class Attribute {
    private int rndValue = 0;
    private int advValue = 0;
    private int totalValue = 0;
    private RaceAttribute raceAttribute;
    private BaseAttribute baseAttribute;

    public Attribute() {
    }
    public Attribute(RaceAttribute raceAttribute) {
        this.raceAttribute = raceAttribute;
        this.baseAttribute = raceAttribute.getBaseAttribute();
    }

    public int getID() {
        return baseAttribute.getID();
    }
    public String getName() {
        return baseAttribute.getName();
    }

    public int getBaseValue() {
        return raceAttribute.getValue();
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
        updateAll();
        return totalValue;
    }
    public int getBonus() {
        return getTotalValue() / 10;
    }
    private void updateAll() {
        totalValue=raceAttribute.getValue() + rndValue + advValue;
    }

    public RaceAttribute getRaceAttribute() {
        return raceAttribute;
    }
    public BaseAttribute getBaseAttribute() {
        return baseAttribute;
    }

    @Override
    public String toString() {
        return String.format("Attribute {ID = %3d, name = %3s, BV = %3d, RV = %3d, AV = %3d, TV = %3d}",
                getID(), getName(), getBaseValue(), rndValue, advValue, totalValue);
    }
}
