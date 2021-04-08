package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PROFESSIONS_CAREERS")
public class ProfessionCareer {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "IDCLSS")
    private ProfessionClass professionClass;

    public ProfessionCareer() {}
    public ProfessionCareer(int ID, String name) {
        this.ID = ID;
        this.name = name;
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
    public ProfessionClass getProfessionClass() {
        return professionClass;
    }
    public void setProfessionClass(ProfessionClass professionClass) {
        this.professionClass = professionClass;
    }

    @Override
    public String toString() {
        return String.format("Career {ID = %d, name = %s}", ID, name);
    }
}