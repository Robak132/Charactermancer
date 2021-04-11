package mappings;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "PROFESSIONS_CLASSES")
public class ProfessionClass {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;

    @OneToMany
    @JoinColumn(name= "IDCLSS")
    private List<ProfessionCareer> careers;

    public ProfessionClass() {}
    public ProfessionClass(int ID, String name) {
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

    public List<ProfessionCareer> getCareers() {
        return careers;
    }
    public void setCareers(List<ProfessionCareer> careers) {
        this.careers = careers;
    }

    @Override
    public String toString() {
        return String.format("Class {ID = %d, name = %s}", ID, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfessionClass that = (ProfessionClass) o;
        return ID == that.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}