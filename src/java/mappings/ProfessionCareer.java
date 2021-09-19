package mappings;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "PROFESSIONS_CAREERS")
public class ProfessionCareer {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany
    @JoinColumn(name= "IDCAREER")
    private List<Profession> professions;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToOne
    @JoinColumn(name = "ID_CLASS")
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
    public List<Profession> getProfessions() {
        return professions;
    }
    public Profession getProfessionByLvl(int lvl) {
        for (Profession profession : professions) {
            if (profession.getLevel() == lvl) {
                return profession;
            }
        }
        return null;
    }
    public void setProfessions(List<Profession> professions) {
        this.professions = professions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfessionCareer career = (ProfessionCareer) o;
        return ID == career.ID && Objects.equals(name, career.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }

    @Override
    public String toString() {
        return String.format("Career {ID = %d, name = %s}", ID, name);
    }
}