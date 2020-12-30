import mappings.Profession;
import mappings.Rase;
import mappings.RaseTable;
import mappings.Skill;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    private StandardServiceRegistry registry;
    private SessionFactory factory;

    Connection() {
        try {
            Files.copy(new File("./src/main/resources/db/database_backup.h2.db").toPath(), new File("./src/main/resources/db/database_backup_2.h2.db").toPath(), StandardCopyOption.REPLACE_EXISTING);            Files.copy(new File("./src/main/resources/db/database.h2.db").toPath(), new File("./src/main/resources/db/database_backup.h2.db").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {}
        try {
            Files.copy(new File("./src/main/resources/db/database.h2.db").toPath(), new File("./src/main/resources/db/database_backup.h2.db").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {}
        refresh();
    }
    private void refresh() {
        URL path = Connection.class.getResource("Connection.class");
        if(path.toString().startsWith("jar:"))
            registry = new StandardServiceRegistryBuilder().configure("hibernate_jar.cfg.xml").build();
        else
            registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();

        Metadata meta = new MetadataSources(registry).getMetadataBuilder().build();
        factory = meta.getSessionFactoryBuilder().build();
    }
    void abort() {
        factory.close();
        StandardServiceRegistryBuilder.destroy(registry);
        refresh();
    }
    Rase getRaseFromTable(int n) {
        Rase rase = new Rase();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT r FROM RaseTable t, Rase r WHERE t.IDrase = r.id AND t.id = :param");
            SQLQuery.setParameter("param", n);
            rase = (Rase) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
        }
        return rase;
    }
    Rase getRase(String name) {
        Rase rase = new Rase();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("FROM Rase WHERE name = :param");
            SQLQuery.setParameter("param", name);
            rase = (Rase) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
        }
        return rase;
    }
    Profession getProfFromTable(int rase, int n) {
        Profession prof = new Profession();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT p FROM ProfTable t, Profession p WHERE t.IDprof = p.id AND index = :param1 AND IDrase= :param2");
            SQLQuery.setParameter("param1", n);
            SQLQuery.setParameter("param2", rase);
            prof = (Profession) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
        }
        return prof;
    }
    List getProfs() {
        List profs = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("FROM Profession WHERE level=1 AND clss!='Zwierzęta'");
            profs = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
        }
        return profs;
    }
}
