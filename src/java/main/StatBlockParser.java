package main;

import components.FilteredComboBox;
import components.GridPanel;
import components.JIntegerField;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import mappings.Attribute;
import mappings.Profession;
import mappings.Race;
import mappings.Skill;

public class StatBlockParser {
    public JPanel mainPanel;
    private JTextField nameField;
    private JTextField classField;
    private JTextField raceField;
    private JTextField sizeField;
    private JTextField careerField;
    private JTextField professionField;
    private GridPanel attributesPanel;
    private JButton exitButton;
    private FilteredComboBox<Profession> professionComboBox;
    private FilteredComboBox<Race> raceComboBox;
    private JTextArea skillArea;
    private JButton submitButton;
    private JTextArea talentArea;
    private GridPanel skillsPanel;

    private final JFrame frame;
    private final Main previousScreen;
    private final Connection connection;
    private final CharacterSheet sheet;

    public StatBlockParser(JFrame _frame, CharacterSheet _sheet, Main _screen, Connection _connection) {
        this.frame = _frame;
        this.previousScreen = _screen;
        this.connection = _connection;
        this.sheet = _sheet;

        exitButton.addActionListener(e -> {
            this.frame.setContentPane(previousScreen.mainPanel);
            this.frame.validate();
        });
        exitButton.setMnemonic(KeyEvent.VK_E);

        submitButton.addActionListener(e -> {
            String text = skillArea.getText();
            String filteredString = text.replace(" ", "")
                    .replace("\n", "")
                    .replace("{", "(")
                    .replace("}", ")");
            String[] skills = filteredString.split(",");
            List<String> unknowedNames = new ArrayList<>();
            if (!Objects.equals(skills[0], "")) {
                for (String processedString : skills) {
                    String[] splitString = processedString.toUpperCase().split("(?<=\\D)(?=\\d)");
                    String name = splitString[0];
                    int number = Integer.parseInt(splitString[1]);
                    Skill skill = connection.getSkill(name);
                    if (skill == null) {
                        unknowedNames.add(name);
                    }
                }
            }
            unknowedNames.forEach(System.out::println);
        });
        submitButton.setMnemonic(KeyEvent.VK_S);

        raceComboBox.addItems(connection.getRaces());
        raceComboBox.setListRenderer(Race::getName);

        professionComboBox.addItems(connection.getProfessions());
        professionComboBox.setListRenderer(Profession::getName);

        Map<Integer, Attribute> attributes = connection.getAttributes(0).stream().collect(Collectors.toMap(Attribute::getID, Function.identity()));
        attributesPanel.createJLabel(0, 0, attributes.get(Attribute.MOVE).getName());
        attributesPanel.createIntegerField(1, 0, 3, 1, null, GridPanel.STANDARD_INTEGER_FIELD, true);

        for (int i = 1; i < attributes.values().size(); i++) {
            attributesPanel.createJLabel(0, i, attributes.get(i).getName());
            JIntegerField sumAttr = attributesPanel.createIntegerField(3, i, 1, 1, null, GridPanel.STANDARD_INTEGER_FIELD, true);
        }
        attributesPanel.createJLabel(0, 12, "HP");
        JIntegerField attrHP = attributesPanel.createIntegerField(1, 12, 3, 1, null, GridPanel.STANDARD_INTEGER_FIELD, true);
        attributesPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
}
