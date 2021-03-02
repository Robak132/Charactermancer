package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RACES_SKILLS")
public class RaceSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "IDRACE")
    private int IDRace;
    @Column(name = "IDSKILL")
    private int IDSkill;

    public RaceSkill() {}
    public RaceSkill(int ID, int IDRace, int IDSkill) {
        this.ID = ID;
        this.IDRace = IDRace;
        this.IDSkill = IDSkill;
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
    public int getIDSkill() {
        return IDSkill;
    }
    public void setIDSkill(int IDSkill) {
        this.IDSkill = IDSkill;
    }
}