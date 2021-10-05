package main;

import com.intellij.uiDesigner.core.GridConstraints;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import mappings.Attribute;
import mappings.Profession;
import mappings.Race;
import mappings.Skill;
import mappings.SkillBase;
import mappings.SkillSingle;
import mappings.Subrace;
import mappings.Talent;
import mappings.TalentSingle;
import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import tabs.TalentTab;
import tools.ColorPalette;
import tools.Dice;
import tools.MultiLineTooltip;

public class CharacterSheetBrowser implements TalentTab {
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
    private JIntegerField usedExpField;
    private JIntegerField freeExpField;
    private JIntegerField earnedExpField;
    private GridPanel logPanel;
    private GridPanel talentsPanel;

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
            try {
                sheet.loadJSON("src/resources/test.json");
            } catch (JSONException e) {
                LogManager.getLogger(getClass().getName()).error(e.getMessage());
            }
        }

        createAttributePanel();

        Map<Integer, SkillSingle> skillsMap = connection.getSingleSkills();
        sheet.getSkills().values().forEach(skill -> skillsMap.put(skill.getID(), skill));
        List<SkillSingle> skillsList = skillsMap.values().stream().sorted(Comparator.comparing(Skill::getName)).collect(Collectors.toList());

        createSkillsPanel(skillsList);
        createTalentsPanel();
        createExpPanel();
        createLogPanel();
        fill();
    }

    private void createAttributePanel() {
        int fullHeight = sheet.isPlayer() ? 3 : 1;
        attributesPanel.createJLabel(0, 0, sheet.getAttribute(Attribute.MOVE).getName());
        JIntegerField attrMov = attributesPanel.createIntegerField(1, 0, fullHeight, 1, sheet.getAttribute(Attribute.MOVE).getBaseValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
        Font font = sheet.isPlayer() ? attrMov.getFont().deriveFont(Font.ITALIC + Font.BOLD, attrMov.getFont().getSize() + 2) : attrMov.getFont();
        attrMov.setFont(font);

        for (int i = 1; i < sheet.getAttributes().size(); i++) {
            int row = 0;
            Attribute attribute = sheet.getAttribute(i);
            attributesPanel.createJLabel(row++, i, attribute.getName());
            int addedValue = attribute.getBaseValue() + attribute.getRndValue();

            if (sheet.isPlayer()) {
                attributesPanel.createIntegerField(row++, i, 1, 1, addedValue, GridPanel.STANDARD_INTEGER_FIELD, false);
                attributesPanel.createIntegerField(row++, i, attribute.getAdvValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
            }
            JIntegerField sumAttr = attributesPanel.createIntegerField(row++, i, 1, 1, attribute.getTotalValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
            sumAttr.setFont(font);
        }
        attributesPanel.createJLabel(0, 12, "HP");
        JIntegerField attrHP = attributesPanel.createIntegerField(1, 12, fullHeight, 1, sheet.getMaxHealthPoints(), GridPanel.STANDARD_INTEGER_FIELD, false);
        attrHP.setFont(font);

        attributesPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void createSkillsPanel(List<SkillSingle> skillsList) {
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

                JTextField attributeField = skillsPanel.createTextField(finalRow, column++, skill.getAttrName(), GridPanel.STANDARD_INTEGER_FIELD, false);
                attributeField.setBackground(ColorPalette.WHITE_BLUE);
                attributeField.setFont(nameField.getFont().deriveFont(Font.BOLD));

                if (!skill.isAdv()) {
                    JIntegerField totalField = skillsPanel.createIntegerField(finalRow, column++, 1, 2, skill.getBaseSkill().getValue(),
                            GridPanel.STANDARD_INTEGER_FIELD, false);
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

                JTextField attributeField = skillsPanel.createTextField(finalRow, column++, skill.getAttrName(), GridPanel.STANDARD_INTEGER_FIELD, false);
                attributeField.setBackground(skill.isGrouped() ? ColorPalette.WHITE_BLUE : nameField.getBackground());
                attributeField.setForeground(skill.isAdvanceable() ? ColorPalette.HALF_GREEN : Color.BLACK);

                JIntegerField advField = skillsPanel.createIntegerField(finalRow, column++, skill.getAdvValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
                advField.setBackground(skill.isGrouped() ? ColorPalette.WHITE_BLUE : nameField.getBackground());
                advField.setForeground(skill.isAdvanceable() ? ColorPalette.HALF_GREEN : Color.BLACK);

                JIntegerField totalField = skillsPanel.createIntegerField(finalRow, column++, skill.getTotalValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
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
    }
    private void createTalentsPanel() {
        List<TalentSingle> talents = sheet.getTalents().values().stream().sorted(
                Comparator.comparing(TalentSingle::isAdvanceable).thenComparing(Talent::getName)).collect(Collectors.toList());
        talents.forEach(e->e.linkAttributeMap(sheet.getAttributes()));

        for (int row = 0; row < talents.size(); row++) {
            TalentSingle talent = talents.get(row);
            int column = 0;

            JTextField nameField = talentsPanel.createTextField(row, column++, talent.getName(), GridPanel.STANDARD_TEXT_FIELD, false);
            nameField.setForeground(talent.getColor());

            JIntegerField lvlField = talentsPanel.createIntegerField(row, column++, talent.getCurrentLvl(), GridPanel.STANDARD_INTEGER_FIELD, false);
            String format = String.format("/%d", talent.getMax());
            lvlField.setFormat("%d" + format);
            lvlField.setForeground(talent.getColor());

            JTextArea testArea = talentsPanel.createTextArea(row, column++, talent.getBaseTalent().getTest(), GridPanel.STANDARD_TEXT_FIELD, false);
            testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));
            testArea.setForeground(talent.getColor());

            String tooltip = talent.getBaseTalent().getDesc();
            talentsPanel.createJLabel(row, column, new ImageIcon("src/resources/images/info.png"), MultiLineTooltip.splitToolTip(tooltip, 75, 10));
        }
        talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void createExpPanel() {
        int usedExp = sheet.getUsedExp();
        usedExpField.setValue(usedExp);
        earnedExpField.setValue(sheet.getExp());
        freeExpField.setValue(sheet.getExp() - usedExp);
    }
    private void createLogPanel() {
        List<DateEntry> history = sheet.getHistory();
        int row = 0;
        for (DateEntry dateEntry : history) {
            int column = 0;
            JLabel dateLabel = logPanel.createJLabel(row++, column, 1, -1, dateEntry.getDate("dd-MM-yyyy HH:mm"));
            dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD, dateLabel.getFont().getSize()+2));

            if (dateEntry.getAttributes().size() > 0) {
                logPanel.createJLabel(row++, column, 1, -1, "Attributes");
                row = createLogSection(row, column, dateEntry.getAttributes());
            }
            if (dateEntry.getSkills().size() > 0) {
                logPanel.createJLabel(row++, column, 1, -1, "Skills");
                row = createLogSection(row, column, dateEntry.getSkills());
            }
            if (dateEntry.getTalents().size() > 0) {
                logPanel.createJLabel(row++, column, 1, -1, "Talents");
                row = createLogSection(row, column, dateEntry.getTalents());
            }
            logPanel.add(new JButton(), new GridConstraints(row, column, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));

        }
        logPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private int createLogSection(int row, int column, List<?> list) {
        for (int i = 0; i < list.size(); i++) {
            int shift = i % 2 == 0 ? 0 : 3;
            Entry<?> entry = (Entry<?>) list.get(i);
            logPanel.createTextField(row, column + shift, entry.getName(), new Dimension(-1, -1), false);

            String format = String.format("->%d", entry.getNow());
            JIntegerField lvlField = logPanel.createIntegerField(row, column + shift + 1, entry.getBefore(), new Dimension(50, -1), false);
            lvlField.setFormat("%d" + format);

            row = i % 2 == 1 || i == list.size() - 1 ? row + 1 : row;
        }
        return row;
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

    @Override
    public SpinnerModel getTalentSpinnerModel(TalentSingle talent) {
        return new SpinnerNumberModel(1,1,1,1);
    }
}
