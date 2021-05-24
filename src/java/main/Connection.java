package main;

import java.util.HashSet;
import java.util.Set;
import mappings.ProfTable;
import mappings.Profession;
import mappings.ProfessionCareer;
import mappings.ProfessionClass;
import mappings.Race;
import mappings.Subrace;
import mappings.Talent;
import mappings.TalentGroup;
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

    public Connection() {
        refresh();
    }

    private void refresh() {
        String path = getClass().getResource("/db/database.sqlite").toString();
        StandardServiceRegistryBuilder tempRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml");
        if(path.startsWith("jar:")) {
            path = path.substring("jar:file:/".length());
            tempRegistry.applySetting(Environment.URL, "jdbc:sqllite:zip:" + path);
        }
        registry = tempRegistry.build();

        Metadata meta = new MetadataSources(registry).getMetadataBuilder().build();
        factory = meta.getSessionFactoryBuilder().build();
    }
    private void abort() {
        factory.close();
        StandardServiceRegistryBuilder.destroy(registry);
        refresh();
    }

    public Race getRaceFromTable(int n) {
        Race race = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT t.race FROM RaceTable t WHERE t.indexDOWN <= :param AND :param <= t.indexUP");
            query.setParameter("param", n);
            race = query.list().size()==0 ? null : (Race) query.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return race;
    }
    public Race getRace(String name) {
        Race race = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("FROM Race WHERE name = :param");
            query.setParameter("param", name);
            race = query.list().size()==0 ? null : (Race) query.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return race;
    }
    public List<Race> getRaces() {
        List<Race> races = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("FROM Race");
            races = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return races;
    }
    public List<Subrace> getSubraces(Race race) {
        if (race != null) {
            return race.getSubraces();
        }

        List<Subrace> subraces = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT r.subraces FROM Race r");
            subraces = query.list();
            session.close();
         } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return subraces;
    }

    public Profession getProfFromTable(Subrace subrace, int n) {
        Profession prof = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT t.prof FROM ProfTable t WHERE t.indexDOWN <= :param1 AND :param1 <= t.indexUP AND t.subrace = :param2");
            query.setParameter("param1", n);
            query.setParameter("param2", subrace);
            prof = query.list().size()==0 ? null : (Profession) query.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return prof;
    }
    public List<ProfessionClass> getProfessionClasses() {
        List<ProfessionClass> classes = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("FROM ProfessionClass WHERE name!='Zwierzę'");
            classes = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return classes;
    }
    public List<ProfessionCareer> getProfessionCareers(Race race) {
        if (race != null) {
            return race.getRaceCareers();
        }

        List<ProfessionCareer> careers = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("FROM ProfessionCareer C WHERE C.professionClass.name != 'Zwierzę'");
            careers = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return careers;
    }
    public List<ProfessionCareer> getProfessionCareers() {
        List<ProfessionCareer> careers = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("From ProfessionCareer");
            careers = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return careers;
    }
    public List<Profession> getProfessions(Subrace subrace, ProfessionClass clss, ProfessionCareer career) {
        List<Profession> professions = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT DISTINCT t.prof FROM ProfTable t WHERE t.subrace=:param AND t.prof.career.professionClass=:param2 AND t.prof.career=:param3");
            query.setParameter("param", subrace);
            query.setParameter("param2", clss);
            query.setParameter("param3", career);
            professions = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return professions;
    }
    public List<Profession> getProfessions() {
        List<Profession> professions = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("FROM Profession");
            professions = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return professions;
    }
    public List<Profession> getProfs(int race) {
        List<Profession> profs = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT t.prof FROM ProfTable t JOIN t.prof WHERE t.race.id =:param AND t.prof.career.professionClass.name!='Zwierzę'");
            query.setParameter("param", race);
            profs = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return profs;
    }
    public List<Profession> getProfs(int race, String clss) {
        if (clss == null)
            return getProfs(race);
        List<Profession> profs = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT t.prof FROM ProfTable t JOIN t.prof WHERE t.prof.career.professionClass = :param2 AND t.race.id =:param");
            query.setParameter("param", race);
            query.setParameter("param2", clss);
            profs = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return profs;
    }

    public TalentGroup getRandomTalent(int n) {
        TalentGroup talent = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT r.talent FROM TalentRandom r WHERE r.indexDOWN <= :param AND :param <= r.indexUP");
            query.setParameter("param", n);
            talent = query.list().size()==0 ? null : (TalentGroup) query.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return talent;
    }
    public List<Talent> getAllTalents() {
        List<Talent> talents = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("FROM Talent");
            talents = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return talents;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> list(Query<T> q){
        return q.list();
    }
}