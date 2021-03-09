package mappings;

import javax.persistence.*;

@Entity
@Table(name = "GROUPTALENTS")
public class GroupTalent {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private Talent baseTalent;

    public GroupTalent() {}
    public GroupTalent(int ID, String name, Talent baseTalent) {
        this.ID = ID;
        this.name = name;
        this.baseTalent = baseTalent;
    }

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
    public Talent getBaseTalent() {
        return baseTalent;
    }
    public void setBaseTalent(Talent baseTalent) {
        this.baseTalent = baseTalent;
    }
}