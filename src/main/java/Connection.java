import mappings.GroupSkill;
import mappings.Profession;
import mappings.Race;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    private StandardServiceRegistry registry;
    private SessionFactory factory;

    Connection() {
        refresh();
    }
    private void refresh() {
        String path = this.getClass().getResource("/db/database.sqlite").toString();
        StandardServiceRegistryBuilder tempRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml");
        if(path.startsWith("jar:")) {
            path = path.substring("jar:file:/".length());
            tempRegistry.applySetting(Environment.URL, "jdbc:sqllite:zip:" + path);
        }
        registry = tempRegistry.build();

        Metadata meta = new MetadataSources(registry).getMetadataBuilder().build();
        factory = meta.getSessionFactoryBuilder().build();
    }
    void abort() {
        factory.close();
        StandardServiceRegistryBuilder.destroy(registry);
        refresh();
    }

    Race getRaceFromTable(int n) {
        Race race = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT r FROM RaceTable t, Race r WHERE t.IDrase = r.id AND t.id = :param");
            SQLQuery.setParameter("param", n);
            race = (Race) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return race;
    }
    Race getRace(String name) {
        Race race = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("FROM Race WHERE name = :param");
            SQLQuery.setParameter("param", name);
            race = (Race) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return race;
    }
    List<Race> getRaces() {
        List<Race> races = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("FROM Race");
            races = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return races;
    }

    Profession getProfFromTable(int race, int n) {
        Profession prof = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT p FROM ProfTable t, Profession p WHERE t.IDprof = p.id AND t.index = :param1 AND t.IDrace= :param2");
            SQLQuery.setParameter("param1", n);
            SQLQuery.setParameter("param2", race);
            prof = (Profession) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return prof;
    }
    Profession getProf(String clss, String profession, int level) {
        Profession prof = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("FROM Profession WHERE clss =:param1 AND profession =:param2 AND level =:param3");
            SQLQuery.setParameter("param1", clss);
            SQLQuery.setParameter("param2", profession);
            SQLQuery.setParameter("param3", level);
            prof = (Profession) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return prof;
    }
    Profession getProf(String profession, int level) {
        Profession prof = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("FROM Profession WHERE profession =:param2 AND level =:param3");
            SQLQuery.setParameter("param2", profession);
            SQLQuery.setParameter("param3", level);
            prof = (Profession) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return prof;
    }
    List<Profession> getProfs(int race) {
        List<Profession> profs = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT p FROM ProfTable t, Profession p WHERE t.IDprof = p.id AND IDrace =:param AND p.clss!='Zwierzęta'");
            SQLQuery.setParameter("param", race);
            profs = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return profs;
    }
    List<Profession> getProfs(int race, String clss) {
        if (clss == null)
            return getProfs(race);
        List<Profession> profs = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT p FROM ProfTable t, Profession p WHERE t.IDprof = p.id AND p.clss = :param2 AND IDrace =:param AND p.clss!='Zwierzęta'");
            SQLQuery.setParameter("param", race);
            SQLQuery.setParameter("param2", clss);
            profs = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return profs;
    }

    List<GroupSkill> getBaseSkillsByRace(int race) {
        List<GroupSkill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT s FROM RaceSkill r, GroupSkill s, Skill sk WHERE r.IDRace=:param AND s.ID=r.IDSkill AND s.IDbase=sk.ID AND sk.adv=0 ORDER BY s.name");
            SQLQuery.setParameter("param", race);
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }
    List<GroupSkill> getAdvSkillsByRace(int race) {
        List<GroupSkill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT s FROM RaceSkill r, GroupSkill s, Skill sk WHERE r.IDRace=:param AND s.ID=r.IDSkill AND s.IDbase=sk.ID AND sk.adv=1 ORDER BY s.name");
            SQLQuery.setParameter("param", race);
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }

    public List<String> getProfsClasses(int race) {
        List<String> result = new ArrayList<>();
        for (Object prof: getProfs(race))
            result.add(((Profession) prof).getClss());
        return result;
    }
    public List<String> getProfsNames(int race, String clss) {
        List<String> result = new ArrayList<>();
        for (Object prof : getProfs(race, clss))
            result.add(((Profession) prof).getProfession());
        return result;
    }
    public List<String> getRacesNames() {
        List<String> result = new ArrayList<>();
        for (Object prof : getRaces())
            result.add(((Race) prof).getName());
        return result;
    }
}
