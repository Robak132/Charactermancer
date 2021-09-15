package main;

import components.GridPanel;
import components.JIntegerField;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mappings.Attribute;
import mappings.Profession;
import mappings.Race;
import mappings.Skill;
import mappings.SkillSingle;
import mappings.Subrace;
import tools.Dice;

public class CharacterSheetBrowser {
    public JPanel mainPanel;
    private JTextField nameField;
    private JTextField classField;
    private JTextField raceField;
    private JTextField sizeField;
    private JTextField careerField;
    private JTextField professionField;
    private GridPanel skillsPanel;
    private JButton exitButton;

    private final JFrame frame;
    private final Main previousScreen;
    private final Connection connection;
    private CharacterSheet sheet;

    public CharacterSheetBrowser(JFrame _frame, CharacterSheet _sheet, Main _screen, Connection _connection) {
        this.frame = _frame;
        this.previousScreen = _screen;
        this.connection = _connection;
        this.sheet = _sheet;

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem i1 = new JMenuItem("Open");
        JMenuItem i2 = new JMenuItem("Save");
        i2.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            int userSelection = fileChooser.showSaveDialog(frame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".json")){
                    fileToSave = new File(fileChooser.getSelectedFile() + ".json");
                }
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            }
        });
        exitButton.addActionListener(e -> {
            this.frame.setContentPane(previousScreen.mainPanel);
            this.frame.validate();
        });
        exitButton.setMnemonic(KeyEvent.VK_E);

        menu.add(i1);
        menu.add(i2);
        mb.add(menu);
        frame.setJMenuBar(mb);

        if (sheet == null) {
            sheet = new CharacterSheet(connection);
            sheet.loadJSON("src/resources/test.json");
//            createNPC();
        }

        skillsPanel.createJLabel(0, 0, sheet.getAttribute(Attribute.MOVE).getName());
        skillsPanel.createIntegerField(1, 0, 3, 1, sheet.getAttribute(Attribute.MOVE).getBaseValue(), new Dimension(30, -1), false);

        for (int i = 1; i < sheet.getAttributes().size(); i++) {
            Attribute attribute = sheet.getAttribute(i);
            skillsPanel.createJLabel(0, i, attribute.getName());
            int addedValue = attribute.getBaseValue() + attribute.getRndValue();

            skillsPanel.createIntegerField(1, i, 1, 1, addedValue, new Dimension(30, -1), false);
            skillsPanel.createIntegerField(2, i, attribute.getAdvValue(), new Dimension(30, -1), false);
            JIntegerField sumAttr = skillsPanel.createIntegerField(3, i, 1, 1, attribute.getTotalValue(), new Dimension(30, -1), false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(),Font.ITALIC+Font.BOLD,sumAttr.getFont().getSize()+2));
        }
        skillsPanel.createJLabel(0, 12, "HP");
        JIntegerField attrHP = skillsPanel.createIntegerField(1, 12, 3, 1, sheet.getMaxHealthPoints(), new Dimension(30, -1), false);
        attrHP.setFont(new Font(attrHP.getFont().getName(),Font.ITALIC+Font.BOLD, attrHP.getFont().getSize()+2));
        skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
        fill();
    }
    private void fill() {
        raceField.setText(sheet.getRace().getName());
        sizeField.setText(sheet.getRace().getSize().name());

        classField.setText(sheet.getProfessionClass().getName());
        careerField.setText(sheet.getProfessionCareer().getName());
        professionField.setText(sheet.getProfession().getName());

        List<SkillSingle> allBasicSkills = connection.getSimpleSkills();
        allBasicSkills.sort(Comparator.comparing(Skill::getName));
//        System.out.println();
    }

    public void createNPC() {
        // Race
        Race race = Race.getRandomRace(connection).getValue();
        Subrace subrace = race.getRndSubrace();
        sheet.setSubrace(subrace);

        // Profession
        Profession profession = subrace.getRandomProf(connection).getValue();
        sheet.setProfession(profession);

        // Attributes
        Map<Integer, Attribute> attributes = race.getAttributes(connection);
        for (var attribute : attributes.entrySet()) {
            attribute.getValue().roll();
        }

        // Attributes Advance
        List<Attribute> tempAttributes = profession.getProfAttributes();
        for (int i=0; i<5; i++) {
            int ID = tempAttributes.get(Dice.randomInt(0, tempAttributes.size()-1)).getID();
            attributes.get(ID).incAdvValue();
        }
        sheet.setAttributes(attributes);

        // Race Skills
//        List<Skill> profSkills = profession.getProfSkills();
//        List<Skill> raceSkills = race.getRaceSkills(attributes, profSkills);
//        for (int i : new int[] {3, 3, 3, 5, 5, 5}) {
//            List<SkillSingle> skills = raceSkills.get(Dice.randomInt(0, raceSkills.size() - 1)).getSingleSkills();
//            SkillSingle active = skills.get(Dice.randomInt(0, skills.size() - 1));
//            active.setAdvValue(i);
//            sheet.addSkillMap(active);
//        }
//        for (int i=0; i<40; i++) {
//            Skill skill = profSkills.get(Dice.randomInt(0, profSkills.size() - 1));
//            List<SkillSingle> skills = skill.getSingleSkills();
//            SkillSingle active = skills.get(Dice.randomInt(0, skills.size() - 1));
//            if (active.getAdvValue() < 10) {
//                active.setAdvValue(active.getAdvValue()+1);
//            } else {
//                sheet.addSkillMap(active);
//                profSkills.remove(skill);
//            }
//        }
//        for (Skill skill : profSkills) {
//            List<SkillSingle> skills = skill.getSingleSkills();
//            SkillSingle active = skills.get(Dice.randomInt(0, skills.size() - 1));
//            sheet.addSkillMap(active);
//        }

        fill();
    }
}
