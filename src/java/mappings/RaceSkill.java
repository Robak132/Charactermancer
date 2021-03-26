package mappings;

import javax.persistence.*;

@Entity
@Table(name = "RACE_SKILLS")
public class RaceSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @ManyToOne
    @JoinColumn(name = "IDRACE")
    private Race race;
    @OneToOne
    @JoinColumn(name = "IDSKILL")
    private GroupSkill skill;

    public RaceSkill() {}
    public RaceSkill(int ID, Race race, GroupSkill skill) {
        this.ID = ID;
        this.race = race;
        this.skill = skill;
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
    public GroupSkill getSkill() {
        return skill;
    }
    public void setSkill(GroupSkill skill) {
        this.skill = skill;
    }
}