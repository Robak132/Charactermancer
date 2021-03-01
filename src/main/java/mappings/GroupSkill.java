package mappings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GROUPSKILLS")
public class GroupSkill {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "IDBASE")
    private int idbase;

    public GroupSkill() {}
    public GroupSkill(int id, String name, int idbase) {
        this.id = id;
        this.name = name;
        this.idbase = idbase;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIdbase() {
        return idbase;
    }
    public void setIdbase(int id) {
        this.id = id;
    }
}