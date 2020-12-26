package mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROFESJETALENTY")
public class ProfesjeTalenty {
    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "IDProf")
    private int IDProf;
    @Column(name = "IDTalentu")
    private int IDTalentu;

    public ProfesjeTalenty() {}
    public ProfesjeTalenty(int ID, int IDProf, int IDTalentu) {
        this.ID = ID;
        this.IDProf = IDProf;
        this.IDTalentu = IDTalentu;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getIDProf() {
        return IDProf;
    }
    public void setIDProf(int IDProf) {
        this.IDProf = IDProf;
    }
    public int getIDTalentu() {
        return IDTalentu;
    }
    public void setIDTalentu(int IDTalentu) {
        this.IDTalentu = IDTalentu;
    }
}