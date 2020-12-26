package mappings;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RASES")
public class Rase {
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

    @OneToMany
    List<RaseTable> raseList = new ArrayList<>();
    @OneToMany
    List<ProfTable> profList = new ArrayList<>();

    public Rase() {}
    public Rase(int id, String name, int ww, int us, int s, int wt, int i, int zw, int zr, int it, int sw, int ogd) {
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
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
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
    public List<RaseTable> getRaseList() {
        return raseList;
    }
    public void setRaseList(List<RaseTable> tableList) {
        this.raseList = tableList;
    }
    public List<ProfTable> getProfList() {
        return profList;
    }
    public void setProfList(List<ProfTable> tableList) {
        this.profList = profList;
    }
}