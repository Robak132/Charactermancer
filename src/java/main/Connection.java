package main;

import mappings.Profession;
import mappings.ProfessionClass;
import mappings.Race;
import mappings.Skill;
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
    private void abort() {
        factory.close();
        StandardServiceRegistryBuilder.destroy(registry);
        refresh();
    }

    public Race getRaceFromTable(int n) {
        Race race = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT t.race FROM RaceTable t WHERE t.indexDOWN <= :param AND :param <= t.indexUP");
            SQLQuery.setParameter("param", n);
            race = SQLQuery.list().size()==0 ? null : (Race) SQLQuery.list().get(0);
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
            Query SQLQuery = session.createQuery("FROM Race WHERE name = :param");
            SQLQuery.setParameter("param", name);
            race = SQLQuery.list().size()==0 ? null : (Race) SQLQuery.list().get(0);
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
            races = list(session.createQuery("FROM Race"));
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return races;
    }

    public Profession getProfFromTable(int race, int n) {
        Profession prof = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT t.prof FROM ProfTable t WHERE t.indexDOWN <= :param1 AND :param1 <= t.indexUP AND t.race.id = :param2");
            SQLQuery.setParameter("param1", n);
            SQLQuery.setParameter("param2", race);
            prof = SQLQuery.list().size()==0 ? null : (Profession) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return prof;
    }
    public List<ProfessionClass> getProfessionClass() {
        List<ProfessionClass> profs = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("FROM ProfessionClass");
            profs = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return profs;
    }
    public List<Profession> getProfs(int race) {
        List<Profession> profs = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT t.prof FROM ProfTable t JOIN t.prof WHERE t.race.id =:param AND t.prof.career.professionClass!='Zwierzęta'");
            SQLQuery.setParameter("param", race);
            profs = SQLQuery.list();
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
            Query SQLQuery = session.createQuery("SELECT t.prof FROM ProfTable t JOIN t.prof WHERE t.prof.career.professionClass = :param2 AND t.race.id =:param");
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

    public TalentGroup getRandomTalent(int n) {
        TalentGroup talent = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT r.talent FROM TalentRandom r WHERE r.indexDOWN <= :param AND :param <= r.indexUP");
            SQLQuery.setParameter("param", n);
            talent = SQLQuery.list().size()==0 ? null : (TalentGroup) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return talent;
    }
    public List<String> getRacesNames() {
        List<String> result = new ArrayList<>();
        for (Race prof : getRaces())
            result.add(prof.getName());
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> list(Query q){
        return q.list();
    }
}