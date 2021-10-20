package charactergen;

import components.JIntegerField;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import main.CharacterSheet;
import main.Connection;
import main.Main;
import mappings.Attribute;
import mappings.Profession;
import mappings.Race;
import mappings.Skill;
import mappings.SkillGroup;
import mappings.SkillSingle;
import mappings.Subrace;
import mappings.Talent;
import mappings.TalentGroup;
import org.apache.logging.log4j.LogManager;
import sheetbrowser.CharacterSheetBrowser;
import tools.Dice;

public class CharacterGen {
    public JPanel mainPanel;
    public final Main previousScreen;

    private final JFrame frame;
    private final Connection connection;
    private final CharacterSheet sheet;
    private JLabel imageLabel;
    private JTabbedPane tabbedPane;
    private JIntegerField expField;
    private JButton exitButton;

    private RaceTab raceTab;
    private ProfTab profTab;
    private AttributesTab attributesTab;
    private FateTab fateTab;
    private RaceSkillTab raceSkillTab;
    private ProfSkillTab profSkillTab;

    public CharacterGen(JFrame frame, Main screen, Connection connection) {
        this.frame = frame;
        this.previousScreen = screen;
        this.connection = connection;
        sheet = new CharacterSheet(connection);
        sheet.addObserver("exp", expField);

        // Race //
        raceTab.initialise(this, sheet, this.connection);

        // Pane controls //
        tabbedPane.addChangeListener(e -> {
            int tab = tabbedPane.getSelectedIndex();
            String iconPath = String.format("src/resources/images/round%d.png", tab + 1);
            ImageIcon icon = new ImageIcon(iconPath);
            imageLabel.setIcon(icon);
        });
        exitButton.addActionListener(e -> {
            this.frame.setContentPane(previousScreen.mainPanel);
            this.frame.validate();
        });
        exitButton.setMnemonic(KeyEvent.VK_E);
    }

    public void moveToNextTab() {
        int tab = tabbedPane.getSelectedIndex();
        tabbedPane.setEnabledAt(tab + 1, true);
        tabbedPane.setSelectedIndex(tab + 1);
        LogManager.getLogger(getClass().getName()).info(String.format("Loaded tab %d", tab + 1));
        switch (tab + 1) {
            case 1:
                profTab.initialise(this, sheet, connection);
                break;
            case 2:
                attributesTab.initialise(this, sheet, connection);
                break;
            case 3:
                fateTab.initialise(this, sheet, connection);
                break;
            case 4:
                raceSkillTab.initialise(this, sheet, connection);
                break;
            case 5:
                profSkillTab.initialise(this, sheet, connection);
                break;
            default:
                break;
        }
    }
    public void export() {
        frame.setContentPane(new CharacterSheetBrowser(frame, sheet, previousScreen, connection).mainPanel);
        frame.validate();
    }
    private void createUIComponents() {
        expField = new JIntegerField(0);
    }

    // Base functions to use with GUI and text //
    public Subrace getRandomSubrace(Connection connection) {
        Race race = Race.getRandomRace(connection).getValue();
        return race.getRndSubrace();
    }

    public static Map<Integer, Attribute> randomAttributeAdvances(Profession profession, Map<Integer, Attribute> startAttributes, int maxPoints) {
        Map<Integer, Attribute> attributes = new ConcurrentHashMap<>(startAttributes);
        List<Attribute> profAttributes = profession.getAttributesList(attributes);

        for (Attribute attribute : attributes.values()) {
            attribute.setAdvValue(0);
        }

        for (int i = 0; i < maxPoints; i++) {
            int activeSlot = Dice.randomInt(0, profAttributes.size() - 1);
            Attribute active = profAttributes.get(activeSlot);
            if (active.incAdvValue() == maxPoints) {
                profAttributes.remove(activeSlot);
            }
        }
        return attributes;
    }
    public static List<SkillSingle> randomizeSkillsWithList(List<Skill> startSkills, int[] values) {
        List<SkillSingle> skills = new ArrayList<>();
        List<Integer> lookupTable = new ArrayList<>();
        for (int i=0;i<startSkills.size();i++) {
            if (startSkills.get(i) instanceof SkillSingle) {
                startSkills.get(i).setAdvValue(0);
                skills.add((SkillSingle) startSkills.get(i));
            } else {
                skills.add(((SkillGroup) startSkills.get(i)).getRndSkill());
            }
            lookupTable.add(i);
        }

        for (int value : values) {
            int index = Dice.randomInt(0, lookupTable.size() - 1);
            skills.get(lookupTable.get(index)).setAdvValue(value);
            lookupTable.remove(lookupTable.get(index));
        }

        return skills;
    }
    public List<Talent> randomizeTalents(List<TalentGroup> talents) {
        List<Talent> returnList = new ArrayList<>();
        for (TalentGroup talent : talents) {
            returnList.add(talent.getRndTalent());
        }
        return returnList;
    }
    public static Object[] getRandomTalent(Connection connection, Map<Integer, Attribute> attributeMap) {
        Object[] returns = new Object[2];
        int numeric = Dice.randomDice(1, 100);
        Talent talent = connection.getRandomTalent(numeric);
        talent.setCurrentLvl(1);
        talent.linkAttributeMap(attributeMap);

        returns[0] = numeric;
        returns[1] = talent;
        return returns;
    }


}