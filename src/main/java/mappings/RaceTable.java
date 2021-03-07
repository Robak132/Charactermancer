package mappings;

import javax.persistence.*;

@Entity
@Table(name = "RACETABLE")
public class RaceTable {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "IDXDOWN")
    private int indexDOWN;
    @Column(name = "IDXUP")
    private int indexUP;
    @ManyToOne
    @JoinColumn(name = "IDRACE")
    private Race race;

    public RaceTable() {}
    public RaceTable(int ID, int indexDOWN, int indexUP, Race race) {
        this.ID = ID;
        this.indexDOWN = indexDOWN;
        this.indexUP = indexUP;
        this.race = race;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getIndexDOWN() {
        return indexDOWN;
    }
    public void setIndexDOWN(int indexDOWN) {
        this.indexDOWN = indexDOWN;
    }
    public int getIndexUP() {
        return indexUP;
    }
    public void setIndexUP(int indexUP) {
        this.indexUP = indexUP;
    }
    public Race getRace() {
        return race;
    }
    public void setRace(Race race) {
        this.race = race;
    }
}