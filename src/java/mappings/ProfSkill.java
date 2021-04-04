package mappings;

import javax.persistence.*;

@Entity
@Table(name = "PROF_SKILLS")
public class ProfSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "S")
    private boolean S;

    @ManyToOne
    @JoinColumn(name = "IDPROF")
    private Profession profession;
    @ManyToOne
    @JoinColumn(name = "IDSKILL")
    private SkillGroup skill;

    public ProfSkill() {}
    public ProfSkill(int ID, boolean s, Profession profession, SkillGroup skill) {
        this.ID = ID;
        this.S = s;
        this.profession = profession;
        this.skill = skill;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public boolean isS() {
        return S;
    }
    public void setS(boolean s) {
        S = s;
    }
    public Profession getProfession() {
        return profession;
    }
    public void setProfession(Profession profession) {
        this.profession = profession;
    }
    public SkillGroup getSkill() {
        return skill;
    }
    public void setSkill(SkillGroup skill) {
        this.skill = skill;
    }
}