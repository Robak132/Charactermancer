package mappings;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import tools.Dice;

@Entity
@Table(name = "RACES")
public class Race {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "NAME")
    private String name;
    @Column(name = "M")
    private int m;
    @Column(name = "FATE")
    private int fate;
    @Column(name = "RESILIENCE")
    private int resilience;
    @Column(name = "EXTRA")
    private int extra;
    @Column(name = "SIZE")
    private int size;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany
    @JoinColumn(name= "IDRACE")
    private List<RaceAttribute> raceAttributes;
    @Transient
    private Map<Integer, Attribute> attributesMap;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany
    @JoinColumn(name= "IDRACE")
    @OrderBy("def DESC, name")
    private List<Subrace> subraces;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name="RACE_CAREERS",
            joinColumns = @JoinColumn(name = "IDRACE"),
            inverseJoinColumns = @JoinColumn(name = "IDCAREER"))
    private List<ProfessionCareer> raceCareers;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name="RACE_SKILLS",
            joinColumns = @JoinColumn(name = "IDRACE"),
            inverseJoinColumns = @JoinColumn(name = "IDSKILL"))
    @OrderBy("name")
    private List<Skill> raceSkills;

    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(name="RACE_TALENTS",
            joinColumns = @JoinColumn(name = "IDRACE"),
            inverseJoinColumns = @JoinColumn(name = "IDTALENT"))
    @OrderBy("name")
    private List<Talent> raceTalents;

    public enum Size {
        TINY(0),
        LITTLE(1),
        SMALL(2),
        NORMAL(3),
        LARGE(4),
        ENORMOUS(5),
        MONSTROUS(6);

        private final int value;
        private static final Map<Integer, Size> map = new HashMap<>();

        Size(int value) {
            this.value = value;
        }
        static {
            for (Size size : Size.values()) {
                map.put(size.value, size);
            }
        }

        public static Size valueOf(int value) {
            return map.get(value);
        }
    }

    public Race() {
        // Needed for Hibernate/JPA
    }
    public Race(int ID, String name, int m, int fate, int resilience, int extra, int size) {
        this.ID = ID;
        this.name = name;
        this.m = m;
        this.fate = fate;
        this.resilience = resilience;
        this.extra = extra;
        this.size = size;
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
    public void setName(String name) {
        this.name = name;
    }
    public int getM() {
        return m;
    }
    public void setM(int m) {
        this.m = m;
    }
    public int getFate() {
        return fate;
    }
    public void setFate(int fate) {
        this.fate = fate;
    }
    public int getResilience() {
        return resilience;
    }
    public void setResilience(int resilience) {
        this.resilience = resilience;
    }
    public int getExtra() {
        return extra;
    }
    public void setExtra(int extra) {
        this.extra = extra;
    }
    public Size getSize() {
        return Size.valueOf(size);
    }
    public void setSize(Size size) {
        this.size = size.ordinal();
    }

    public List<Subrace> getSubraces() {
        return subraces;
    }
    public Subrace getRndSubrace() {
        return (Subrace) Dice.randomItem(subraces);
    }
    public void setSubraces(List<Subrace> subraces) {
        this.subraces = subraces;
    }

    public List<ProfessionCareer> getRaceCareers() {
        return raceCareers;
    }
    public void setRaceCareers(List<ProfessionCareer> raceCareers) {
        this.raceCareers = raceCareers;
    }

    // Attributes
    private void createAttributeMap() {
        Map<Integer, Attribute> attributeMap = new ConcurrentHashMap<>();
        for (RaceAttribute raceAttribute : raceAttributes) {
            attributeMap.put(raceAttribute.getBaseAttribute().getID(), new Attribute(raceAttribute));
        }
        this.attributesMap = attributeMap;
    }
    public Map<Integer, Attribute> getAttributes() {
        if (attributesMap == null) {
            createAttributeMap();
        }
        return attributesMap;
    }
    public Attribute getAttribute(int index) {
        if (attributesMap == null) {
            createAttributeMap();
        }
        return attributesMap.get(index);
    }

    public List<RaceAttribute> getRaceAttributes() {
        return raceAttributes;
    }
    public RaceAttribute getRaceAttribute(int attributeID) {
        for (RaceAttribute raceAttribute: raceAttributes) {
            if (raceAttribute.getBaseAttribute().getID() == attributeID) {
                return raceAttribute;
            }
        }
        return null;
    }

    // Skills
    public List<Skill> getRaceSkills() {
        return raceSkills;
    }
    public List<Skill> getRaceSkills(Map<Integer, Attribute> attributeMap) {
        raceSkills.forEach(e->e.linkAttributeMap(attributeMap));
        return raceSkills;
    }
    public List<Skill> getRaceSkills(Map<Integer, Attribute> attributeMap, List<Skill> profSkills) {
        List<Skill> tempSkills = new ArrayList<>();
        for (Skill raceSkill : raceSkills) {
            for (Skill profSkill : profSkills) {
                if (raceSkill.equals(profSkill)) {
                    raceSkill = profSkill;
                    raceSkill.setAdvanceable(true);
                    break;
                }
            }
            raceSkill.linkAttributeMap(attributeMap);
            tempSkills.add(raceSkill);
        }
        return tempSkills;
    }
    public void setRaceSkills(List<Skill> raceSkills) {
        this.raceSkills = raceSkills;
    }

    public List<Talent> getRaceTalents() {
        raceTalents.forEach(e->e.setCurrentLvl(1));
        return raceTalents;
    }
    public List<Talent> getRaceTalents(Map<Integer, Attribute> attributeMap) {
        for (Talent talent : raceTalents) {
            talent.setCurrentLvl(1);
            talent.linkAttributeMap(attributeMap);
        }
        return raceTalents;
    }
    public void setRaceTalents(List<Talent> raceTalents) {
        this.raceTalents = raceTalents;
    }


    @Override
    public String toString() {
        return String.format("Race {ID = %d, name = %s}", ID, name);
    }
}