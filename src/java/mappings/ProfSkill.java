package mappings;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PROF_SKILLS")
@IdClass(ProfSkillPK.class)
public class ProfSkill implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "ID_PROF")
    private Profession profession;

    @Id
    @ManyToOne
    @JoinColumn(name = "ID_SKILL")
    private Skill skill;

    @Column(name = "S")
    private boolean S;

    public ProfSkill() {}

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
    public Skill getSkill() {
        return skill;
    }
    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}

@Embeddable
class ProfSkillPK implements Serializable {
    private int profession;
    private int skill;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfSkillPK that = (ProfSkillPK) o;
        return profession == that.profession && skill == that.skill;
    }
    @Override
    public int hashCode() {
        return Objects.hash(profession, skill);
    }
}
