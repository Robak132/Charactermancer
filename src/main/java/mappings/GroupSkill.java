package mappings;

import javax.persistence.*;

@Entity
@Table(name = "GROUPSKILLS")
public class GroupSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "GRP")
    private boolean group;
    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private Skill base;

    public GroupSkill() {}
    public GroupSkill(int ID, String name, Skill base, boolean group) {
        this.ID = ID;
        this.name = name;
        this.base = base;
        this.group = group;
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
    public Skill getBase() {
        return base;
    }
    public void setBase(Skill base) {
        this.base = base;
    }
    public boolean isGroup() {
        return group;
    }
    public void setGroup(boolean group) {
        this.group = group;
    }
}