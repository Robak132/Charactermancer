package mappings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GROUPSKILLS")
public class GroupSkill {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @ManyToOne
    @JoinColumn(name = "IDBASE")
    private Skill base;
    @Column(name = "CUSTOM")
    private int custom;

    public GroupSkill() {}
    public GroupSkill(int ID, String name, Skill base, int custom) {
        this.ID = ID;
        this.name = name;
        this.base = base;
        this.custom = custom;
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
    public int getCustom() {
        return custom;
    }
    public void setCustom(int custom) {
        this.custom = custom;
    }
}