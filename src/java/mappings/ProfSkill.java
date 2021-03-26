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
    private GroupSkill skill;
    @ManyToOne
    @JoinColumn(name = "IDEXCL")
    private GroupSkill skillExcl;

    public ProfSkill() {}
    public ProfSkill(int ID, boolean s, Profession profession, GroupSkill skill, GroupSkill skillExcl) {
        this.ID = ID;
        this.S = s;
        this.profession = profession;
        this.skill = skill;
        this.skillExcl = skillExcl;
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
    public GroupSkill getSkill() {
        return skill;
    }
    public void setSkill(GroupSkill skill) {
        this.skill = skill;
    }
    public GroupSkill getSkillExcl() {
        return skillExcl;
    }
    public void setSkillExcl(GroupSkill skillExcl) {
        this.skillExcl = skillExcl;
    }
}