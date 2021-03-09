package mappings;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "RNG_TALENTS_TABLE")
public class RandomTalent {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "IDXDOWN")
    private int indexDOWN;
    @Column(name = "IDXUP")
    private int indexUP;
    @ManyToOne
    @JoinColumn(name = "IDTAL")
    private GroupTalent talent;

    public RandomTalent() {}
    public RandomTalent(int ID, int indexDOWN, int indexUP, GroupTalent talent) {
        this.ID = ID;
        this.indexDOWN = indexDOWN;
        this.indexUP = indexUP;
        this.talent = talent;
    }

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
    public GroupTalent getTalent() {
        return talent;
    }
    public void setTalent(GroupTalent talent) {
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
        RandomTalent that = (RandomTalent) o;
        return Objects.equals(talent, that.talent);
    }
    @Override
    public int hashCode() {
        return Objects.hash(talent);
    }
}