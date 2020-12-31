package mappings;

import javax.persistence.*;

@Entity
@Table(name = "RACETABLE")
public class RaceTable {
    private int index;
    private int IDrase;

    public RaceTable() {}
    public RaceTable(int index) {
        this.index = index;
    }
    public RaceTable(int index, int IDrase) {
        this.index = index;
        this.IDrase = IDrase;
    }

    @Id
    @Column(name = "ID")
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    @Column(name = "IDRASE")
    public int getIDrase() {
        return IDrase;
    }
    public void setIDrase(int IDrase) {
        this.IDrase = IDrase;
    }
}