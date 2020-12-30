package mappings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SKILLS")
public class Skill {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "IDBASE")
    private int idbase;

    public Skill() {}
    public Skill(int id, String name, int idbase) {
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