package mappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import main.Connection;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
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
    @JoinColumn(name= "ID_RACE")
    private List<RaceAttribute> raceAttributes;
    @Transient
    private List<Attribute> attributes;

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
    public Map<Integer, Attribute> getAttributes(Connection connection) {
        return getAttributes(connection, null);
    }
    public Map<Integer, Attribute> getAttributes(Connection connection, Integer importance) {
        attributes = connection.getAttributes(importance);
        Map<Integer, Attribute> map = attributes.stream().collect(Collectors.toMap(Attribute::getID, Function.identity()));
        for (RaceAttribute raceAttribute : raceAttributes) {
            if (map.containsKey(raceAttribute.getAttribute())) {
                map.get(raceAttribute.getAttribute()).setBaseValue(raceAttribute.getValue());
            }
        }
        return map;
    }
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    // Skills
    public Map<Integer, Skill> getRaceSkills() {
        return raceSkills.stream().collect(Collectors.toMap(Skill::getID, Function.identity()));
    }
    public Map<Integer, Skill> getRaceSkills(Map<Integer, Skill> profSkills) {
        Map<Integer, Skill> raceSkills = getRaceSkills();
        for (Skill profSkill : profSkills.values()) {
            profSkill.setAdvanceable(true);
            if (raceSkills.containsKey(profSkill.getID())) {
                raceSkills.put(profSkill.getID(), profSkill);
            }
        }
        return raceSkills;
    }
    public void setRaceSkills(List<Skill> raceSkills) {
        this.raceSkills = raceSkills;
    }

    // Talents
    public Map<Integer, Talent> getRaceTalents() {
        Map<Integer, Talent> talentsMap = new HashMap<>();
        for (Talent talent : raceTalents) {
            talent.setCurrentLvl(1);
            talentsMap.put(talent.getID(), talent);
        }
        return talentsMap;
    }
    public Map<Integer, Talent> getRaceTalents(Map<Integer, Talent> profTalents) {
        Map<Integer, Talent> raceTalents = getRaceTalents();
        for (Talent talent : profTalents.values()) {
            talent.setAdvanceable(true);
            if (raceTalents.containsKey(talent.getID())) {
                talent.setCurrentLvl(1);
                raceTalents.put(talent.getID(), talent);
            }
        }
        return raceTalents;
    }
    public Talent matchTalents(Talent firstTalent, Talent secondTalent) {
        if (firstTalent instanceof TalentSingle) {
            if (secondTalent instanceof TalentSingle) {
                if (firstTalent.equals(secondTalent)) {
                    secondTalent.setAdvanceable(true);
                    return secondTalent;
                } else {
                    return firstTalent;
                }
            } else {
                for (TalentSingle singleSecondTalent : secondTalent.getSingleTalents()) {
                    matchTalents(firstTalent, singleSecondTalent);
                }
            }
        } else {
            List<Talent> tempList = new ArrayList<>();
            for (TalentSingle singleFirstTalent : firstTalent.getSingleTalents()) {
                tempList.add(matchTalents(singleFirstTalent, secondTalent));
            }
            ((TalentGroup) firstTalent).setChildTalents(tempList);
            return firstTalent;
        }
        return null;
    }
    public List<Talent> getRaceTalents(Map<Integer, Attribute> attributeMap, List<Talent> profTalents) {
        List<Talent> tempTalents = new ArrayList<>();
        for (Talent raceTalent : raceTalents) {
            for (Talent profTalent : profTalents) {
                raceTalent = matchTalents(raceTalent, profTalent);
            }
            raceTalent.setCurrentLvl(1);
            raceTalent.linkAttributeMap(attributeMap);
            tempTalents.add(raceTalent);
        }
        return tempTalents;
    }
    public void setRaceTalents(List<Talent> raceTalents) {
        this.raceTalents = raceTalents;
    }

    public static KeyValue<Integer, Race> getRandomRace(Connection connection) {
        int numeric = Dice.randomDice(1, 100);
        return new DefaultKeyValue<>(numeric, connection.getRaceFromTable(numeric));
    }

    @Override
    public String toString() {
        return String.format("Race {ID = %d, name = %s}", ID, name);
    }
}