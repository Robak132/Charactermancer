package mappings;

import javax.persistence.*;

@Entity
@Table(name = "PROF_SKILLS")
public class ProfSkill {
    @Id
    int ID;
    @Column(name = "S")
    private boolean S;

    @ManyToOne
    @JoinColumn(name = "ID_PROF")
    private Profession profession;
//    @ManyToOne
//    @JoinColumn(name = "IDSKILL")
    @Transient
    private SkillGroup skill;

    public ProfSkill() {}
    public ProfSkill(boolean s, Profession profession, SkillGroup skill) {
        this.S = s;
        this.profession = profession;
        this.skill = skill;
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