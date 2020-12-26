package mappings;

import javax.persistence.*;
import java.util.ArrayList;
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
    private String profession;;
    @Column(name = "PROFESSIONENG")
    private String professioneng;
    @Column(name = "LEVEL")
    private int level;
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

    @OneToMany
    List<ProfTable> profList = new ArrayList<>();

    public Profession() {}
    public Profession(int id, String clss, String career, String profession, String professioneng, int level, int ww, int us, int s, int wt, int i, int zw, int zr, int it, int sw, int ogd) {
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
    public List<ProfTable> getProfList() {
        return profList;
    }
    public void setProfList(List<ProfTable> tableList) {
        this.profList = profList;
    }
}