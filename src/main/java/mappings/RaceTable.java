package mappings;

import javax.persistence.*;

@Entity
@Table(name = "RACETABLE")
public class RaceTable {
    @Id
    @Column(name = "ID")
    private int index;

    @ManyToOne
    @JoinColumn(name = "IDRASE")
    private Race race;

    public RaceTable() {}
    public RaceTable(int index) {
        this.index = index;
    }
    public RaceTable(int index, Race race) {
        this.index = index;
        this.race = race;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public Race getRace() {
        return race;
    }
    public void setRace(Race race) {
        this.race = race;
    }
}