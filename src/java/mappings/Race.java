package mappings;

import java.util.*;
import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
    @Column(name = "RANDOM_TALENTS")
    private int randomTalents;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name= "IDRACE")
    private List<RaceAttribute> raceAttributes;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="RACE_SKILLS",
            joinColumns = @JoinColumn(name = "IDRACE"),
            inverseJoinColumns = @JoinColumn(name = "IDSKILL"))
    private List<SkillGroup> raceSkills;

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

    public Race() {}
    public Race(int ID, String name, int m, int fate, int resilience, int extra, int size, int randomTalents) {
        this.ID = ID;
        this.name = name;
        this.m = m;
        this.fate = fate;
        this.resilience = resilience;
        this.extra = extra;
        this.size = size;
        this.randomTalents = randomTalents;
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
    public int getRandomTalents() {
        return randomTalents;
    }
    public void setRandomTalents(int randomTalents) {
        this.randomTalents = randomTalents;
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

    public List<Attribute> getAttributes() {
        List<Attribute> attributeList = new ArrayList<>();
        for (RaceAttribute raceAttribute : raceAttributes) {
            attributeList.add(new Attribute(raceAttribute));
        }
        return attributeList;
    }

    public List<SkillGroup> getRaceSkills() {
        return raceSkills;
    }
    public List<SkillGroup> getRaceSkills(List<Attribute> attributes) {
        for (SkillGroup skill : raceSkills) {
            for (Skill singleSkill : skill.getSkills()) {
                for (Attribute attribute : attributes) {
                    if (singleSkill.getAttr().equals(attribute.getBaseAttribute())) {
                        singleSkill.setLinkedAttribute(attribute);
                        break;
                    }
                }
            }
        }
        return raceSkills;
    }

    @Override
    public String toString() {
        return String.format("Race {ID = %d, name = %s}", ID, name);
    }
}