package main;

import components.GridPanel;
import components.JIntegerField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import mappings.SkillBase;
import mappings.SkillSingle;
import mappings.Subrace;
import tools.ColorPalette;
import tools.Dice;

public class CharacterSheetBrowser {
    public JPanel mainPanel;
    private JTextField nameField;
    private JTextField classField;
    private JTextField raceField;
    private JTextField sizeField;
    private JTextField careerField;
    private JTextField professionField;
    private GridPanel attributesPanel;
    private JButton exitButton;
    private GridPanel skillsPanel;

    private final JFrame frame;
    private final Main previousScreen;
    private final Connection connection;
    private CharacterSheet sheet;

    public CharacterSheetBrowser(JFrame _frame, CharacterSheet _sheet, Main _screen, Connection _connection) {
        this.frame = _frame;
        this.previousScreen = _screen;
        this.connection = _connection;
        this.sheet = _sheet;

        createMenu();
        exitButton.addActionListener(e -> {
            this.frame.setContentPane(previousScreen.mainPanel);
            this.frame.validate();
        });
        exitButton.setMnemonic(KeyEvent.VK_E);


        if (sheet == null) {
            sheet = new CharacterSheet(connection);
            sheet.loadJSON("src/resources/test.json");
//            createNPC();
        }

        attributesPanel.createJLabel(0, 0, sheet.getAttribute(Attribute.MOVE).getName());
        attributesPanel.createIntegerField(1, 0, 3, 1, sheet.getAttribute(Attribute.MOVE).getBaseValue(), new Dimension(30, -1), false);

        for (int i = 1; i < sheet.getAttributes().size(); i++) {
            Attribute attribute = sheet.getAttribute(i);
            attributesPanel.createJLabel(0, i, attribute.getName());
            int addedValue = attribute.getBaseValue() + attribute.getRndValue();

            attributesPanel.createIntegerField(1, i, 1, 1, addedValue, new Dimension(30, -1), false);
            attributesPanel.createIntegerField(2, i, attribute.getAdvValue(), new Dimension(30, -1), false);
            JIntegerField sumAttr = attributesPanel.createIntegerField(3, i, 1, 1, attribute.getTotalValue(), new Dimension(30, -1), false);
            sumAttr.setFont(new Font(sumAttr.getFont().getName(),Font.ITALIC+Font.BOLD,sumAttr.getFont().getSize()+2));
        }
        attributesPanel.createJLabel(0, 12, "HP");
        JIntegerField attrHP = attributesPanel.createIntegerField(1, 12, 3, 1, sheet.getMaxHealthPoints(), new Dimension(30, -1), false);
        attrHP.setFont(new Font(attrHP.getFont().getName(),Font.ITALIC+Font.BOLD, attrHP.getFont().getSize()+2));
        attributesPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

        Map<Integer, SkillSingle> skillsMap = connection.getSingleSkills();
        sheet.getSkills().values().forEach(skill -> skillsMap.put(skill.getID(), skill));
        List<SkillSingle> skillsList = skillsMap.values().stream().sorted(Comparator.comparing(Skill::getName)).collect(Collectors.toList());

        List<SkillBase> headers = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;

        for (SkillSingle skill : skillsList) {
            skill.linkAttributeMap(sheet.getAttributes());
            int finalRow, column;

            if ((!skill.isAdv() || skill.getAdvValue() != 0) && skill.isGrouped() && !headers.contains(skill.getBaseSkill())) {
                finalRow = skill.isAdv() ? advItr++ : baseItr++;
                column = skill.isAdv() ? 4 : 0;
                int width = skill.isAdv() ? 3 : 1;

                JTextField nameField = skillsPanel.createTextField(finalRow, column, 1, width, skill.getBaseSkill().getName(), new Dimension(220, -1), false);
                nameField.setBackground(ColorPalette.WHITE_BLUE);
                nameField.setFont(nameField.getFont().deriveFont(Font.BOLD));
                column+=width;

                JTextField attributeField = skillsPanel.createTextField(finalRow, column++, skill.getAttrName(), new Dimension(30, -1), false);
                attributeField.setBackground(ColorPalette.WHITE_BLUE);
                attributeField.setFont(nameField.getFont().deriveFont(Font.BOLD));

                if (!skill.isAdv()) {
                    JIntegerField totalField = skillsPanel.createIntegerField(finalRow, column++, 1, 2, skill.getBaseSkill().getValue(),
                            new Dimension(30, -1), false);
                    totalField.setBackground(ColorPalette.WHITE_BLUE);
                    totalField.setFont(totalField.getFont().deriveFont(Font.ITALIC + Font.BOLD, totalField.getFont().getSize() + 2));
                }
                headers.add(skill.getBaseSkill());
            }
            if ((!skill.isAdv() && !skill.isGrouped()) || skill.getAdvValue() != 0 || skill.isAdvanceable()) {
                finalRow = skill.isAdv() ? advItr++ : baseItr++;
                column = skill.isAdv() ? 4 : 0;

                JTextField nameField = skillsPanel.createTextField(finalRow, column++, skill.getName(), new Dimension(220, -1), false);
                nameField.setBackground(skill.isGrouped() ? ColorPalette.WHITE_BLUE : nameField.getBackground());
                nameField.setForeground(skill.isAdvanceable() ? ColorPalette.HALF_GREEN : Color.BLACK);
                nameField.setFont(nameField.getFont().deriveFont(skill.isGrouped() ? Font.ITALIC : Font.PLAIN));

                JTextField attributeField = skillsPanel.createTextField(finalRow, column++, skill.getAttrName(), new Dimension(30, -1), false);
                attributeField.setBackground(skill.isGrouped() ? ColorPalette.WHITE_BLUE : nameField.getBackground());
                attributeField.setForeground(skill.isAdvanceable() ? ColorPalette.HALF_GREEN : Color.BLACK);

                JIntegerField advField = skillsPanel.createIntegerField(finalRow, column++, skill.getAdvValue(), new Dimension(30, -1), false);
                advField.setBackground(skill.isGrouped() ? ColorPalette.WHITE_BLUE : nameField.getBackground());
                advField.setForeground(skill.isAdvanceable() ? ColorPalette.HALF_GREEN : Color.BLACK);

                JIntegerField totalField = skillsPanel.createIntegerField(finalRow, column++, skill.getTotalValue(), new Dimension(30, -1), false);
                totalField.setBackground(skill.isGrouped() ? ColorPalette.WHITE_BLUE : nameField.getBackground());
                totalField.setFont(new Font(totalField.getFont().getName(), Font.ITALIC + Font.BOLD, totalField.getFont().getSize() + 2));
                totalField.setForeground(skill.isAdvanceable() ? ColorPalette.HALF_GREEN : Color.BLACK);
            }
        }
        if (baseItr != 1) {
            skillsPanel.createJLabel(0, 0, 1, 4, "Basic skills");
        }
        if (advItr != 1) {
            skillsPanel.createJLabel(0, 4, 1, 4, "Advanced skills");
        }
        skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

        fill();
    }
    private void createMenu() {
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
        menu.add(i1);
        menu.add(i2);
        mb.add(menu);
        frame.setJMenuBar(mb);
    }
    private void fill() {
        raceField.setText(sheet.getRace().getName());
        sizeField.setText(sheet.getRace().getSize().name());

        classField.setText(sheet.getProfessionClass().getName());
        careerField.setText(sheet.getProfessionCareer().getName());
        professionField.setText(sheet.getProfession().getName());

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
