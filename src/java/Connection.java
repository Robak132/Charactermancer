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
    Race getRace(String name) {
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
    Profession getProf(String clss, String profession, int level) {
        Profession prof = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("FROM Profession p WHERE p.career.professionClass =:param1 AND name =:param2 AND level =:param3");
            SQLQuery.setParameter("param1", clss);
            SQLQuery.setParameter("param2", profession);
            SQLQuery.setParameter("param3", level);
            prof = SQLQuery.list().size()==0 ? null : (Profession) SQLQuery.list().get(0);
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
            Query SQLQuery = session.createQuery("FROM Profession WHERE name =:param2 AND level =:param3");
            SQLQuery.setParameter("param2", profession);
            SQLQuery.setParameter("param3", level);
            prof = SQLQuery.list().size()==0 ? null : (Profession) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return prof;
    }
    List<ProfessionClass> getProfessionClass() {
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
    List<Profession> getProfs(int race) {
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
    List<Profession> getProfs(int race, String clss) {
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

    List<Skill> getAlternateSkillsForGroup(int custom) {
        List<Skill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT G FROM Skill G JOIN G.baseSkill WHERE G.baseSkill.id=:param AND G.group=false");
            SQLQuery.setParameter("param", custom);
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }
    List<Skill> getProfessionSkills(Profession prof) {
        List<Skill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT p.skill FROM ProfSkill p WHERE p.profession.ID =:param ORDER BY p.skill.name");
            SQLQuery.setParameter("param", prof.getID());
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }
    List<Skill> getProfessionSkills(Profession prof, List<BaseAttribute> attributesToBind) {
        List<Skill> skills = getProfessionSkills(prof);
        for (Skill skill : skills) {
            for (BaseAttribute baseAttribute : attributesToBind) {
                if (skill.getAttr().equals(baseAttribute)) {
                    skill.setAttr(baseAttribute);
                    break;
                }
            }
        }
        return skills;
    }
    List<BaseSkill> getProfessionBaseSkills(Profession prof) {
        List<BaseSkill> baseSkills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT p.skill.base FROM ProfSkill p WHERE p.profession.ID =:param");
            SQLQuery.setParameter("param", prof.getID());
            baseSkills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return baseSkills;
    }

    List<RaceTalent> getTalentsByRace(int race) {
        List<RaceTalent> talents = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT r FROM RaceTalent r WHERE r.race.id=:param");
            SQLQuery.setParameter("param", race);
            talents = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return talents;
    }
    List<GroupTalent> getAlternateTalentsForGroup(int custom) {
        List<GroupTalent> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT G FROM GroupTalent G WHERE G.baseTalent.id=:param AND G.group=false");
            SQLQuery.setParameter("param", custom);
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }
    GroupTalent getRandomTalent(int n) {
        GroupTalent talent = null;
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT r.talent FROM RandomTalent r WHERE r.indexDOWN <= :param AND :param <= r.indexUP");
            SQLQuery.setParameter("param", n);
            talent = SQLQuery.list().size()==0 ? null : (GroupTalent) SQLQuery.list().get(0);
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return talent;
    }

    List<String> getProfsClasses(int race) {
        List<String> result = new ArrayList<>();
        for (ProfessionClass prof: getProfessionClass())
            result.add(prof.getName());
        return result;
    }
    List<String> getProfsNames(int race, String clss) {
        List<String> result = new ArrayList<>();
        for (Object prof : getProfs(race, clss))
            result.add(((Profession) prof).getName());
        return result;
    }
    List<String> getRacesNames() {
        List<String> result = new ArrayList<>();
        for (Object prof : getRaces())
            result.add(((Race) prof).getName());
        return result;
    }
}