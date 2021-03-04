package mappings;

import javax.persistence.*;

@Entity
@Table(name = "PROFTABLE")
public class ProfTable {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "INDX")
    private int index;
    @ManyToOne
    @JoinColumn(name = "IDPROF")
    private Profession prof;
    @ManyToOne
    @JoinColumn(name = "IDRACE")
    private Race race;

    public ProfTable() {}
    public ProfTable(int id, int index, Profession prof, Race race) {
        this.id = id;
        this.index = index;
        this.prof = prof;
        this.race = race;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
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