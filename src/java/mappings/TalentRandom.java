package mappings;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TALENTS_RND_TABLE")
public class TalentRandom {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "IDXDOWN")
    private int indexDOWN;
    @Column(name = "IDXUP")
    private int indexUP;

    @ManyToOne
    @JoinColumn(name = "IDTAL")
    private Talent talent;

    public TalentRandom() {}

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getIndexDOWN() {
        return indexDOWN;
    }
    public void setIndexDOWN(int idxdown) {
        this.indexDOWN = idxdown;
    }
    public int getIndexUP() {
        return indexUP;
    }
    public void setIndexUP(int idxup) {
        this.indexUP = idxup;
    }
    public Talent getTalent() {
        return talent;
    }
    public void setTalent(Talent talent) {
        this.talent = talent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TalentRandom that = (TalentRandom) o;
        return Objects.equals(talent, that.talent);
    }
    @Override
    public int hashCode() {
        return Objects.hash(talent);
    }
}