package mappings;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "RACES")
public class Race {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "WW")
    private int ww;
    @Column(name = "US")
    private int us;
    @Column(name = "S")
    private int s;
    @Column(name = "WT")
    private int wt;
    @Column(name = "I")
    private int i;
    @Column(name = "ZW")
    private int zw;
    @Column(name = "ZR")
    private int zr;
    @Column(name = "IT")
    private int it;
    @Column(name = "SW")
    private int sw;
    @Column(name = "OGD")
    private int ogd;
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

    @OneToMany(mappedBy = "race")
    Set<ProfTable> profTable;

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
    public enum Attributes {
        WW(0), US(1), S(2), Wt(3), I(4), Zw(5), Zr(6), Int(7), SW(8), Ogd(9);

        private final int index;
        private static final Map<Integer, Attributes> map = new HashMap<>();

        Attributes(int index) {
            this.index = index;
        }
        static {
            for (Attributes attributes : Attributes.values()) {
                map.put(attributes.index, attributes);
            }
        }

        public static Attributes valueOf(int index) {
            return map.get(index);
        }
    }

    public Race() {}
    public Race(int id, String name, int ww, int us, int s, int wt, int i, int zw, int zr, int it, int sw, int ogd, int m, int fate, int resilience, int extra, int size, int randomTalents) {
        this.id = id;
        this.name = name;
        this.ww = ww;
        this.us = us;
        this.s = s;
        this.wt = wt;
        this.i = i;
        this.zw = zw;
        this.zr = zr;
        this.it = it;
        this.sw = sw;
        this.ogd = ogd;
        this.m = m;
        this.fate = fate;
        this.resilience = resilience;
        this.extra = extra;
        this.size = size;
        this.randomTalents = randomTalents;
    }

    public int getID() {
        return id;
    }
    public void setID(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getWw() {
        return ww;
    }
    public void setWw(int ww) {
        this.ww = ww;
    }
    public int getUs() {
        return us;
    }
    public void setUs(int us) {
        this.us = us;
    }
    public int getS() {
        return s;
    }
    public void setS(int s) {
        this.s = s;
    }
    public int getWt() {
        return wt;
    }
    public void setWt(int wt) {
        this.wt = wt;
    }
    public int getI() {
        return i;
    }
    public void setI(int i) {
        this.i = i;
    }
    public int getZw() {
        return zw;
    }
    public void setZw(int zw) {
        this.zw = zw;
    }
    public int getZr() {
        return zr;
    }
    public void setZr(int zr) {
        this.zr = zr;
    }
    public int getIt() {
        return it;
    }
    public void setIt(int it) {
        this.it = it;
    }
    public int getSw() {
        return sw;
    }
    public void setSw(int sw) {
        this.sw = sw;
    }
    public int getOgd() {
        return ogd;
    }
    public void setOgd(int ogd) {
        this.ogd = ogd;
    }
    public int getM() {
        return m;
    }
    public void setM(int m) {
        this.m = m;
    }
    public Integer getAttr(int number) {
        Integer[] table = new Integer[]{ww, us, s, wt, i, zw, zr, it, sw, ogd};
        return table[number];
    }
    public Integer[] getBaseAttr() {
        return new Integer[] {ww, us, s, wt, i, zw, zr, it, sw, ogd};
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
}