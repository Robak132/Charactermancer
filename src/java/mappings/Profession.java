package mappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "PROFESSIONS")
public class Profession {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "PROFESSION")
    private String name;
    @Column(name = "LEVEL")
    private int level;

    @Transient
    private Map<Integer, Attribute> attributesMap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCAREER")
    private ProfessionCareer career;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name="PROF_ATTRIBUTES",
            joinColumns = @JoinColumn(name = "ID_PROF"),
            inverseJoinColumns = @JoinColumn(name = "ID_ATTR"))
    private List<Attribute> profAttributes;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany
    @JoinColumn(name= "ID_PROF")
    @OrderBy("earning DESC")
    private List<ProfSkill> profSkills;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name="PROF_TALENTS",
            joinColumns = @JoinColumn(name = "IDPROF"),
            inverseJoinColumns = @JoinColumn(name = "IDTAL"))
    private List<Talent> profTalents;

    public Profession() {}
    public Profession(int ID, String name, int level) {
        this.ID = ID;
        this.name = name;
        this.level = level;
    }

    public int getID() {
        return ID;
    }
    public void setID(int id) {
        this.ID = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String profession) {
        this.name = profession;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public ProfessionCareer getProfessionCareer() {
        return career;
    }
    public void setCareer(ProfessionCareer career) {
        this.career = career;
    }

    // Attributes
    public List<Attribute> getProfAttributes() {
        return profAttributes;
    }
    public void setProfAttributes(List<Attribute> profAttributes) {
        this.profAttributes = profAttributes;
    }
    public List<Attribute> getAttributesList(Map<Integer, Attribute> attributes) {
        if (attributesMap == null) {
            createAttributeMap(attributes);
        }

        return new ArrayList<>(attributesMap.values());
    }
    public boolean hasAttribute(int ID) {
        for (Attribute attr : profAttributes) {
            if (attr.getID() == ID) {
                return true;
            }
        }
        return false;
    }
    public void setAttributes(List<Attribute> profAttributes) {
        this.profAttributes = profAttributes;
    }

    // Skills
    public Map<Integer, Skill> getProfSkills() {
        Map<Integer, Skill> skillsMap = new HashMap<>();
        for (ProfSkill profSkill : profSkills) {
            Skill skill = profSkill.getSkill();
            skill.setAdvanceable(true);
            skill.setEarning(profSkill.isEarning());
            skillsMap.put(skill.getID(), skill);
        }
        return skillsMap;
    }
    public void setProfSkills(List<ProfSkill> profSkills) {
        this.profSkills = profSkills;
    }

    // Talents
    public Map<Integer, Talent> getProfTalents() {
        Map<Integer, Talent> talentsMap = new HashMap<>();
        for (Talent talent : profTalents) {
            talent.setAdvanceable(true);
            talent.setCurrentLvl(0);
            talentsMap.put(talent.getID(), talent);
        }
        return talentsMap;
    }
    public void setProfTalents(List<Talent> profTalents) {
        this.profTalents = profTalents;
    }

    private void createAttributeMap(Map<Integer, Attribute> attributes) {
        attributesMap = new ConcurrentHashMap<>();
        for (Attribute attribute : profAttributes) {
            attributesMap.put(attribute.getID(), attributes.get(attribute.getID()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Profession that = (Profession) o;
        return ID == that.ID && Objects.equals(name, that.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }
    @Override
    public String toString() {
        return String.format("Profession {ID = %d, name = %s}", ID, name);
    }
}