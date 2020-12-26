package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROFTABLE")
public class ProfTable {
    private int id;
    private int index;
    private int IDprof;
    private int IDrase;

    public ProfTable() {}
    public ProfTable(int id, int IDprof, int index, int IDrase) {
        this.id = id;
        this.index = index;
        this.IDprof = IDprof;
        this.IDrase = IDrase;
    }

    @Id
    @Column(name = "ID")
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "IDPROF")
    public int getIDprof() {
        return IDprof;
    }
    public void setIDprof(int IDprof) {
        this.IDprof = IDprof;
    }

    @Column(name = "INDEX")
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    @Column(name = "IDRASE")
    public void setIDrase(int IDrase) {
        this.IDrase = IDrase;
    }
    public int getIDrase() {
        return IDrase;
    }
}