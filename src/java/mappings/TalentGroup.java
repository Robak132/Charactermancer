package mappings;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import tools.Dice;

@Entity
@Table(name = "TALENTS_GROUPS")
public class TalentGroup {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="TALENTS_LINK",
            joinColumns = @JoinColumn(name = "IDGROUP"),
            inverseJoinColumns = @JoinColumn(name = "IDTALENT"))
    private List<Talent> talents;

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Talent getFirstTalent() {
        return talents.get(0);
    }
    public Talent getRndTalent() {
        return (Talent) Dice.randomItem(talents);
    }

    public List<Talent> getTalents() {
        return talents;
    }
    public int countTalents() {
        return talents.size();
    }
    public void setTalents(List<Talent> talents) {
        this.talents = talents;
    }
}
