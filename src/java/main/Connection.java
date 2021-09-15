package main;

import mappings.*;
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

@SuppressWarnings("unchecked")
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
    public Subrace getSubrace(int ID, String name) {
        Subrace subrace = null;
        try {
            Session session = factory.openSession();
            Query<Subrace> query = session.createQuery("FROM Subrace WHERE ID=:ID AND name=:name");
            query.setParameter("ID", ID);
            query.setParameter("name", name);
            subrace = query.list().size()==0 ? null : query.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return subrace;
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

    public Attribute getAttribute(int ID, String name) {
        Attribute attribute = null;
        try {
            Session session = factory.openSession();
            Query<Attribute> query = session.createQuery("FROM Attribute WHERE ID=:ID AND name=:name");
            query.setParameter("ID", ID);
            query.setParameter("name", name);
            attribute = query.list().size()==0 ? null : query.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return attribute;
    }
    public List<Attribute> getAttributes(Integer importance) {
        List<Attribute> attributes = new ArrayList<>();
        try {
            Session session = factory.openSession();
            if (importance == null) {
                Query<Attribute> query = session.createQuery("FROM Attribute");
                attributes = query.list().size()==0 ? null : query.list();
            } else {
                Query<Attribute> query = session.createQuery("FROM Attribute WHERE importance=:importance");
                query.setParameter("importance", importance);
                attributes = query.list().size()==0 ? null : query.list();
            }
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return attributes;
    }
    public List<Attribute> getAttributes() {
        List<Attribute> attributes = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query<Attribute> query = session.createQuery("FROM Attribute");
            attributes = query.list().size()==0 ? null : query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return attributes;
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
    public Profession getProfession(int ID, String name) {
        Profession prof = null;
        try {
            Session session = factory.openSession();
            Query<Profession> query = session.createQuery("FROM Profession WHERE ID=:ID AND name=:name");
            query.setParameter("ID", ID);
            query.setParameter("name", name);
            prof = query.list().size()==0 ? null : query.list().get(0);
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
            Query<Profession> query = session.createQuery("SELECT t.prof FROM ProfTable t JOIN t.prof WHERE t.subrace.id =:param AND t.prof.career.professionClass.name!='Zwierzę'");
            query.setParameter("param", race);
            profs = query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return profs;
    }

    public List<SkillSingle> getSimpleSkills() {
        List<SkillSingle> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query<SkillSingle> query = session.createQuery("FROM SkillSingle WHERE baseSkill.adv=false");
            skills = query.list().size()==0 ? null : query.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }

    public Talent getRandomTalent(int n) {
        Talent talent = null;
        try {
            Session session = factory.openSession();
            Query<Talent> query = session.createQuery("SELECT r.talent FROM TalentRandom r WHERE r.indexDOWN <= :param AND :param <= r.indexUP");
            query.setParameter("param", n);
            talent = query.list().size()==0 ? null : query.list().get(0);
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
            talents = session.createQuery("FROM Talent").getResultList();
            session.close();

        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return talents;
    }
}