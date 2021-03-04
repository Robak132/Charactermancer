package mappings;

import javax.persistence.*;

@Entity
@Table(name = "RACES_SKILLS")
public class RaceSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "IDRACE")
    private int IDRace;
    @OneToOne
    @JoinColumn(name = "IDSKILL")
    private GroupSkill skill;

    public RaceSkill() {}
    public RaceSkill(int ID, int IDRace) {
        this.ID = ID;
        this.IDRace = IDRace;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getIDRace() {
        return IDRace;
    }
    public void setIDRace(int IDRace) {
        this.IDRace = IDRace;
    }
    public GroupSkill getSkill() {
        return skill;
    }
    public void setSkill(GroupSkill skill) {
        this.skill = skill;
    }
}