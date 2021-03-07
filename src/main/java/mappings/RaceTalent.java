package mappings;

import javax.persistence.*;

@Entity
@Table(name = "RACES_TALENTS")
public class RaceTalent {
    @Id
    @Column(name = "ID")
    private int ID;
    @ManyToOne
    @JoinColumn(name = "IDRACE")
    private Race race;
    @ManyToOne
    @JoinColumn(name = "IDTALENT")
    private GroupTalent talent;
    @ManyToOne
    @JoinColumn(name = "IDEXC")
    private GroupTalent exec;

    public RaceTalent() {}
    public RaceTalent(int ID, Race race, GroupTalent talent, GroupTalent exec) {
        this.ID = ID;
        this.race = race;
        this.talent = talent;
        this.exec = exec;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public Race getRace() {
        return race;
    }
    public void setRace(Race race) {
        this.race = race;
    }
    public GroupTalent[] getAllTalents() {
        return new GroupTalent[] {talent, exec};
    }
    public GroupTalent getTalent() {
        return talent;
    }
    public void setTalent(GroupTalent talent) {
        this.talent = talent;
    }
    public GroupTalent getExec() {
        return exec;
    }
    public void setExec(GroupTalent exec) {
        this.exec = exec;
    }
}