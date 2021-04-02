package mappings;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "PROFESSIONS")
public class Profession {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "PROFESSION")
    private String profession;
    @Column(name = "LEVEL")
    private int level;

    @ManyToOne
    @JoinColumn(name = "IDCAREER")
    private ProfessionCareer career;

    @OneToMany(mappedBy = "prof")
    Set<ProfTable> profTable;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="PROF_ATTRIBUTES",
            joinColumns = @JoinColumn(name = "IDPROF"),
            inverseJoinColumns = @JoinColumn(name = "IDATTR"))
    List<Attribute> attributes;

    public Profession() {}
    public Profession(int id, String profession, int level) {
        this.id = id;
        this.profession = profession;
        this.level = level;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public ProfessionCareer getCareer() {
        return career;
    }
    public void setCareer(ProfessionCareer career) {
        this.career = career;
    }
    public List<Attribute> getAttributes() {
        return attributes;
    }
    public boolean hasAttribute(int ID) {
        for (Attribute attr : attributes) {
            if (attr.getID() == ID) {
                return true;
            }
        }
        return false;
    }
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Profession)) {
            return false;
        }
        Profession c = (Profession) o;
        return id == c.id;
    }

    @Override
    public String toString() {
        return String.format("Profession {class = %s, career = %s, profession = %s}", career.getProfessionClass(), career, profession);
    }
}