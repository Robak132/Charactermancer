package mappings;

import javax.persistence.*;

@Entity
@Table(name = "PROFTABLE")
public class ProfTable {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "IDXDOWN")
    private int indexDOWN;
    @Column(name = "IDXUP")
    private int indexUP;
    @ManyToOne
    @JoinColumn(name = "IDPROF")
    private Profession prof;
    @ManyToOne
    @JoinColumn(name = "IDRACE")
    private Race race;

    public ProfTable() {}
    public ProfTable(int ID, int indexDOWN, int indexUP, Profession prof, Race race) {
        this.ID = ID;
        this.indexDOWN = indexDOWN;
        this.indexUP = indexUP;
        this.prof = prof;
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
    public Profession getProf() {
        return prof;
    }
    public void setProf(Profession prof) {
        this.prof = prof;
    }
    public Race getRace() {
        return race;
    }
    public void setRace(Race race) {
        this.race = race;
    }
}