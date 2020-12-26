package mappings;

import javax.persistence.*;

@Entity
@Table(name = "RASETABLE")
public class RaseTable {
    private int index;
    private int IDrase;

    public RaseTable() {}
    public RaseTable(int index) {
        this.index = index;
    }
    public RaseTable(int index, int IDrase) {
        this.index = index;
        this.IDrase = IDrase;
    }

    @Id
    @Column(name = "ID")
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    @Column(name = "IDRASE")
    public int getIDrase() {
        return IDrase;
    }
    public void setIDrase(int IDrase) {
        this.IDrase = IDrase;
    }
}