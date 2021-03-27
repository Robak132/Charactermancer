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
            Query SQLQuery = session.createQuery("FROM Profession WHERE clss =:param1 AND profession =:param2 AND level =:param3");
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
            Query SQLQuery = session.createQuery("FROM Profession WHERE profession =:param2 AND level =:param3");
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
    List<Profession> getProfs(int race) {
        List<Profession> profs = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT t.prof FROM ProfTable t JOIN t.prof WHERE t.race.id =:param AND t.prof.clss!='Zwierzęta'");
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
            Query SQLQuery = session.createQuery("SELECT t.prof FROM ProfTable t JOIN t.prof WHERE t.prof.clss = :param2 AND t.race.id =:param");
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

    List<GroupSkill> getSkillsByRace(Race race) {
        List<GroupSkill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT r.skill FROM RaceSkill r WHERE r.race.id=:param ORDER BY r.skill.base.adv, r.skill.name");
            SQLQuery.setParameter("param", race.getID());
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }
    List<GroupSkill> getBaseSkillsByRace(int race) {
        List<GroupSkill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT r.skill FROM RaceSkill r JOIN r.skill WHERE r.race.id=:param AND NOT r.skill.base.adv ORDER BY r.skill.name");
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
            Query SQLQuery = session.createQuery("SELECT r.skill FROM RaceSkill r JOIN r.skill WHERE r.race.id=:param AND r.skill.base.adv ORDER BY r.skill.name");
            SQLQuery.setParameter("param", race);
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }
    List<GroupSkill> getAlternateSkillsForGroup(int custom) {
        List<GroupSkill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT G FROM GroupSkill G JOIN G.base WHERE G.base.id=:param AND G.group=false");
            SQLQuery.setParameter("param", custom);
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }
    List<GroupSkill> getProfessionSkills(Profession prof) {
        List<GroupSkill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT p.skill FROM ProfSkill p WHERE p.profession.id =:param");
            SQLQuery.setParameter("param", prof.getId());
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
    }
    List<Skill> getProfessionBaseSkills(Profession prof) {
        List<Skill> skills = new ArrayList<>();
        try {
            Session session = factory.openSession();
            Query SQLQuery = session.createQuery("SELECT p.skill.base FROM ProfSkill p WHERE p.profession.id =:param");
            SQLQuery.setParameter("param", prof.getId());
            skills = SQLQuery.list();
            session.close();
        } catch (Exception ex) {
            abort();
            ex.printStackTrace();
        }
        return skills;
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

    List<Attribute> getRaceAttributes(Race race) {
        List<Attribute> attributeList = new ArrayList<>();
        for (RaceAttribute raceAttribute : race.getAttributes()) {
            Attribute attribute = raceAttribute.getAttribute();
            attribute.setBaseValue(raceAttribute.getValue());
            attributeList.add(attribute);
        }
        return attributeList;
    }

    List<String> getProfsClasses(int race) {
        List<String> result = new ArrayList<>();
        for (Object prof: getProfs(race))
            result.add(((Profession) prof).getClss());
        return result;
    }
    List<String> getProfsNames(int race, String clss) {
        List<String> result = new ArrayList<>();
        for (Object prof : getProfs(race, clss))
            result.add(((Profession) prof).getProfession());
        return result;
    }
    List<String> getRacesNames() {
        List<String> result = new ArrayList<>();
        for (Object prof : getRaces())
            result.add(((Race) prof).getName());
        return result;
    }
}