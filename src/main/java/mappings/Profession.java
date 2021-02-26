package mappings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "PROFESSIONS")
public class Profession {
    @Id
    @Column(name = "ID")
    private int id;
    @Column(name = "CLASS")
    private String clss;
    @Column(name = "CAREER")
    private String career;
    @Column(name = "PROFESSION")
    private String profession;
    @Column(name = "PROFESSIONENG")
    private String professioneng;
    @Column(name = "LEVEL")
    private int level;
    @Column(name = "WW")
    private boolean ww;
    @Column(name = "US")
    private boolean us;
    @Column(name = "S")
    private boolean s;
    @Column(name = "WT")
    private boolean wt;
    @Column(name = "I")
    private boolean i;
    @Column(name = "ZW")
    private boolean zw;
    @Column(name = "ZR")
    private boolean zr;
    @Column(name = "IT")
    private boolean it;
    @Column(name = "SW")
    private boolean sw;
    @Column(name = "OGD")
    private boolean ogd;

    @OneToMany
    List<ProfTable> profList = new ArrayList<>();

    public Profession() {}
    public Profession(int id, String clss, String career, String profession, String professioneng, int level, boolean ww, boolean us, boolean s, boolean wt, boolean i, boolean zw, boolean zr, boolean it, boolean sw, boolean ogd) {
        this.id = id;
        this.clss = clss;
        this.career = career;
        this.profession = profession;
        this.professioneng = professioneng;
        this.level = level;
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
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getClss() {
        return clss;
    }
    public void setClss(String clss) {
        this.clss = clss;
    }
    public String getCareer() {
        return career;
    }
    public void setCareer(String career) {
        this.career = career;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public String getProfessioneng() {
        return professioneng;
    }
    public void setProfessioneng(String professioneng) {
        this.professioneng = professioneng;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public List<Boolean> getAttributes() {
        return Arrays.asList(ww, us, s, wt, i, zw, zr, it, sw, ogd);
    }
    public boolean getWw() {
        return ww;
    }
    public void setWw(boolean ww) {
        this.ww = ww;
    }
    public boolean getUs() {
        return us;
    }
    public void setUs(boolean us) {
        this.us = us;
    }
    public boolean getS() {
        return s;
    }
    public void setS(boolean s) {
        this.s = s;
    }
    public boolean getWt() {
        return wt;
    }
    public void setWt(boolean wt) {
        this.wt = wt;
    }
    public boolean getI() {
        return i;
    }
    public void setI(boolean i) {
        this.i = i;
    }
    public boolean getZw() {
        return zw;
    }
    public void setZw(boolean zw) {
        this.zw = zw;
    }
    public boolean getZr() {
        return zr;
    }
    public void setZr(boolean zr) {
        this.zr = zr;
    }
    public boolean getIt() {
        return it;
    }
    public void setIt(boolean it) {
        this.it = it;
    }
    public boolean getSw() {
        return sw;
    }
    public void setSw(boolean sw) {
        this.sw = sw;
    }
    public boolean getOgd() {
        return ogd;
    }
    public void setOgd(boolean ogd) {
        this.ogd = ogd;
    }
    public List<ProfTable> getProfList() {
        return profList;
    }
    public void setProfList(List<ProfTable> profList) {
        this.profList = profList;
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
}