package tabs;

import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.GridPanel;
import components.JIntegerField;
import components.SearchableComboBox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerListModel;
import main.CharacterGen;
import main.CharacterSheet;
import main.Connection;
import mappings.Skill;
import mappings.SkillGroup;
import mappings.SkillSingle;
import mappings.Talent;
import mappings.TalentGroup;
import mappings.TalentSingle;
import org.apache.logging.log4j.LogManager;
import tools.AbstractActionHelper;
import tools.ColorPalette;
import tools.Dice;
import tools.MultiLineTooltip;

public class RaceSkillTab {
    private CharacterSheet sheet;
    private Connection connection;
    private final PropertyChangeSupport observersManager = new PropertyChangeSupport(this);

    private GridPanel skillsPanel;
    private GridPanel talentsPanel;
    private JPanel mainPanel;
    private GridPanel randomTalentsPanel;
    private JPanel rollPanel;
    private JButton rollButton;
    private JButton rollOKButton;
    private JIntegerField rollResult;
    private JButton option1Button;
    private JIntegerField points3Field;
    private JIntegerField points5Field;

    private Map<Integer, Integer> raceskillPoints;

    private List<Skill> raceSkills = new ArrayList<>();
    private List<Talent> raceTalents = new ArrayList<>();
    private final List<Talent> randomTalents = new ArrayList<>();
    private final List<SkillSingle> visibleRaceSkills = new ArrayList<>();
    private final List<TalentSingle> visibleRaceTalents = new ArrayList<>();
    private final List<TalentSingle> visibleRandomTalents = new ArrayList<>();

    private final Dimension[] talentFieldDimensions = {
            new Dimension(200, -1),
            new Dimension(30, -1),
            new Dimension(30, -1),
            new Dimension(200, -1)
    };

    public RaceSkillTab() {
        // Needed for GUI Designer
    }
    public RaceSkillTab(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        initialise(parent, sheet, connection);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        this.sheet = sheet;
        this.connection = connection;

        raceskillPoints = new ConcurrentHashMap<>(Map.of(0, 0, 3, 3, 5, 3));
        observersManager.addPropertyChangeListener("points3", points3Field);
        observersManager.addPropertyChangeListener("points5", points5Field);

        raceSkills = sheet.getRace().getRaceSkills(sheet.getAttributes(), sheet.getProfession().getProfSkills());
        raceTalents = sheet.getRace().getRaceTalents(sheet.getAttributes(), sheet.getProfession().getProfTalents());

        createSkillTable(raceSkills);
        createTalentTable(raceTalents, talentFieldDimensions);
        createRandomTalentTable(talentFieldDimensions);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
        AbstractActionHelper.createActionMnemonic(mainPanel, stroke, () -> {
            rollSkills();
            rollTalents();
            rollRandomTalents(sheet.getSubrace().getRandomTalents());
        });
        rollButton.addActionListener(e -> rollRandomTalents());
        rollButton.setMnemonic(KeyEvent.VK_R);
        rollOKButton.addActionListener(e -> {
            // TODO: Manual entering
        });
        option1Button.addActionListener(e -> {
            sheet.addSkills(visibleRaceSkills, skill -> skill.getAdvValue() > 0);
            sheet.setTalentList(visibleRaceTalents);
            sheet.addTalents(visibleRandomTalents);

            System.out.print(sheet);

            skillsPanel.iterateThroughRows(0, (o, i) -> {
                try {
                    ((SearchableComboBox) o).setLocked(true);
                } catch (ClassCastException ignored) {}
            });
            skillsPanel.iterateThroughRows(2, (o, i) -> ((AdvancedSpinner) o).setEnabled(false));
            skillsPanel.iterateThroughRows(4, (o, i) -> {
                try {
                    ((SearchableComboBox) o).setLocked(true);
                } catch (ClassCastException ignored) {}
            });
            skillsPanel.iterateThroughRows(6, (o, i) -> ((AdvancedSpinner) o).setEnabled(false));
            option1Button.setEnabled(false);

            parent.moveToNextTab();
        });
        option1Button.setMnemonic(KeyEvent.VK_1);
    }

    private void createSkillTable(List<Skill> raceSkills) {
        List<Component> tabOrder = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;
        int row;
        int column;
        for (int i = 0; i < raceSkills.size(); i++) {
            Skill raceSkill = raceSkills.get(i);
            Color color = raceSkill.isAdvanceable() ? ColorPalette.HALF_GREEN : Color.black;

            if (raceSkill.isAdv()) {
                column = 4;
                row = advItr++;
            } else {
                column = 0;
                row = baseItr++;
            }
            int finalI = i;
            int finalRow = row;
            int finalColumn = column;

            SkillSingle activeSkill = skillsPanel.createComboIfNeeded(raceSkill, row, column++, color, visibleRaceSkills,
                    newSkill -> updateSkillRow(finalI, finalRow, finalColumn, color, newSkill));

            skillsPanel.createTextField(row, column++, activeSkill.getAttrName(), new Dimension(30, -1), false);

            AdvancedSpinner jSpinner = skillsPanel.createAdvancedSpinner(row, column++, new SpinnerListModel(Arrays.asList(0, 3, 5)), new Dimension(35, -1), true);
            jSpinner.addChangeListener(e -> {
                if (!jSpinner.isLocked()) {
                    skillSpinnerChange(finalI, finalRow, finalColumn, jSpinner, color);
                }
            });

            skillsPanel.createIntegerField(row, column++, activeSkill.getBaseSkill().getLinkedAttribute().getTotalValue(), new Dimension(30, -1), false);

            updateSkillRow(finalI, finalRow, finalColumn, color);
            tabOrder.add(jSpinner.getTextField());
        }
        if (baseItr != 1) {
            skillsPanel.createJLabel(0, 0, 1, 4, "Basic skills");
        }
        if (advItr != 1) {
            skillsPanel.createJLabel(0, 4, 1, 4, "Advanced skills");
        }
        skillsPanel.setFocusCycleRoot(true);
        skillsPanel.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(tabOrder));
        tabOrder.get(0).requestFocusInWindow();

        skillsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void createTalentTable(List<Talent> raceTalents, Dimension[] fieldDimensions) {
        talentsPanel.createJLabel(0,0,1,-1, "Talents");
        for (int i = 0; i < raceTalents.size(); i++) {
            Talent raceTalent = raceTalents.get(i);
            int finalI = i;
            int row = i + 1;
            int column = 0;

            TalentSingle activeTalent = talentsPanel.createComboIfNeeded(raceTalent, row, column++, visibleRaceTalents,
                    newTalent -> updateTalentRow(talentsPanel, finalI, row, visibleRaceTalents, newTalent));

            talentsPanel.createIntegerField(row, column++, activeTalent.getCurrentLvl(), fieldDimensions[column-1], false);

            talentsPanel.createIntegerField(row, column++, activeTalent.getMax(), fieldDimensions[column-1], false);

            JTextArea testArea = talentsPanel.createTextArea(row, column++, activeTalent.getBaseTalent().getTest(), fieldDimensions[column-1], false);
            testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

            String tooltip = activeTalent.getBaseTalent().getDesc();
            talentsPanel.createJLabel(row, column, new ImageIcon("src/resources/images/info.png"), MultiLineTooltip.splitToolTip(tooltip, 75, 10));
        }
        talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private void createRandomTalentTable(Dimension[] fieldDimensions) {
        if (sheet.getSubrace().getRandomTalents() != 0) {
            rollPanel.setVisible(true);
            for (int row=0; row < sheet.getSubrace().getRandomTalents(); row++) {
                int column = 0;

                randomTalentsPanel.createTextField(row, column++, "", fieldDimensions[column-1], false);

                randomTalentsPanel.createIntegerField(row, column++, 0, fieldDimensions[column-1], false);

                randomTalentsPanel.createIntegerField(row, column++, 0, fieldDimensions[column-1], false);

                JTextArea testArea = randomTalentsPanel.createTextArea(row, column++, "", fieldDimensions[column-1], false);
                testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

                randomTalentsPanel.createJLabel(row, column, new ImageIcon("src/resources/images/info.png"), "");
            }
            randomTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
        }
    }

    private void skillSpinnerChange(int idx, int row, int column, AdvancedSpinner jSpinner, Color color) {
        int last = visibleRaceSkills.get(idx).getAdvValue();
        int now = (int) jSpinner.getValue();

        if (last != now) {
            observersManager.firePropertyChange("points" + last, (int) raceskillPoints.get(last), raceskillPoints.get(last) + 1);
            observersManager.firePropertyChange("points" + now, (int) raceskillPoints.get(now), raceskillPoints.get(now) - 1);
            raceskillPoints.put(last, raceskillPoints.get(last) + 1);
            raceskillPoints.put(now, raceskillPoints.get(now) - 1);
            visibleRaceSkills.get(idx).setAdvValue(now);
            updateSkillRow(idx, row, column, color);
        }
        option1Button.setEnabled(raceskillPoints.get(3) == 0 && raceskillPoints.get(5) == 0);
        setSpinnerLocks();
    }
    private void setSpinnerLocks() {
        // Creating base list for models
        Set<Integer> newModel = new TreeSet<>();
        newModel.add(0);
        for (int value : new int[] {3, 5}) {
            if (raceskillPoints.get(value) != 0) {
                newModel.add(value);
            }
        }

        // Updating models (adding present value if needed)
        for (int column : new int[] {2, 6}) {
            skillsPanel.iterateThroughRows(column, (o, i) -> {
                AdvancedSpinner spinner = (AdvancedSpinner) o;
                Integer value = (Integer) spinner.getValue();
                Set<Integer> activeModel = new TreeSet<>(newModel);
                activeModel.add(value);

                spinner.setLocked(true);
                spinner.setModel(new SpinnerListModel(activeModel.toArray()));
                spinner.setValue(value);
                spinner.setHorizontalAlignment(JTextField.CENTER);
                spinner.setLocked(false);
            });
        }
    }

    private void updateSkillRow(int idx, int row, int column, Color color, SkillSingle newSkill) {
        boolean colorChange = newSkill.isAdv() && newSkill.getAdvValue() == 0;
        if (skillsPanel.getComponent(column, row) instanceof JTextField) {
            ((JTextField) skillsPanel.getComponent(column, row)).setText(newSkill.getName());
        } else {
            ((SearchableComboBox) skillsPanel.getComponent(column, row)).setSelectedItem(newSkill.getName());
        }
        skillsPanel.getComponent(column, row).setForeground(colorChange ? Color.RED : color);

        ((JTextField) skillsPanel.getComponent(column + 1, row)).setText(newSkill.getAttrName());
        ((AdvancedSpinner) skillsPanel.getComponent(column + 2, row)).setValue(newSkill.getAdvValue());
        ((JIntegerField) skillsPanel.getComponent(column + 3, row)).setValue(newSkill.getTotalValue());


        if (visibleRaceSkills.size() > idx) {
            visibleRaceSkills.set(idx, newSkill);
        }
    }
    private void updateSkillRow(int idx, int row, int column, Color color) {
        updateSkillRow(idx, row, column, color, visibleRaceSkills.get(idx));
    }

    private void updateTalentRow(GridPanel panel, int idx, int row, List<TalentSingle> talentList, TalentSingle newTalent) {
        if (panel.getComponent(0, row) instanceof JTextField) {
            ((JTextField) panel.getComponent(0, row)).setText(newTalent.getName());
        } else {
            ((SearchableComboBox) panel.getComponent(0, row)).setSelectedItem(newTalent.getName());
        }

        ((JIntegerField) panel.getComponent(1, row)).setValue(newTalent.getCurrentLvl());
        ((JIntegerField) panel.getComponent(2, row)).setValue(newTalent.getMax());
        ((JTextArea) panel.getComponent(3, row)).setText(newTalent.getBaseTalent().getTest());
        ((JLabel) panel.getComponent(4, row)).setToolTipText(MultiLineTooltip.splitToolTip(newTalent.getBaseTalent().getDesc()));

        if (talentList.size() > idx) {
            talentList.set(idx, newTalent);
        }
    }
    private void updateTalentRow(GridPanel panel, int idx, int row, List<TalentSingle> talentList) {
        updateTalentRow(panel, idx, row, talentList, talentList.get(idx));
    }

    private void rollSkills() {
        List<Integer> rollRange = Dice.randomIntegerList(raceSkills.size(), new int[] {3, 3, 3, 5, 5, 5});
        skillsPanel.iterateThroughRows(2, (o, idx) -> ((AdvancedSpinner) o).setValue(0));
        skillsPanel.iterateThroughRows(6, (o, idx) -> ((AdvancedSpinner) o).setValue(0));

        int baseItr = 1;
        int advItr = 1;
        int row;
        int column;
        for (int i=0;i<rollRange.size();i++) {
            if (raceSkills.get(i).isAdv()) {
                column = 4;
                row = advItr++;
            } else {
                column = 0;
                row = baseItr++;
            }

            if (raceSkills.get(i) instanceof SkillGroup) {
                visibleRaceSkills.set(i, ((SkillGroup) raceSkills.get(i)).getRndSkill());
            }
            ((AdvancedSpinner) skillsPanel.getComponent(column + 2, row)).setValue(rollRange.get(i));
        }
    }
    private void rollTalents() {
        for (int i = 0; i < raceTalents.size(); i++) {
            Talent talent = raceTalents.get(i);
            TalentSingle activeTalent;
            if (talent instanceof TalentSingle) {
                activeTalent = (TalentSingle) raceTalents.get(i);
            } else {
                activeTalent = (TalentSingle) ((TalentGroup) raceTalents.get(i)).getRndTalent();
            }
            updateTalentRow(talentsPanel, i, i + 1, visibleRaceTalents, activeTalent);
        }
    }
    private void rollRandomTalents(int iterations) {
        for (int i=0; i<iterations;i++) {
            if (rollButton.isEnabled()) {
                Object[] result = CharacterGen.getRandomTalent(connection, sheet.getAttributes());
                Talent rollTalent = (Talent) result[1];
                int rollResultNumeric = (int) result[0];

                while (randomTalents.contains(rollTalent)) {
                    LogManager.getLogger(getClass().getName())
                            .warn(String.format("%d doubled, changing to %d", rollResultNumeric, rollResultNumeric % 100 + 1));
                    rollResultNumeric = rollResultNumeric % 100 + 1;
                    rollTalent = connection.getRandomTalent(rollResultNumeric);
                }
                rollTalent.linkAttributeMap(sheet.getAttributes());
                rollTalent.setCurrentLvl(1);

                randomTalents.add(rollTalent);
                rollResult.setValue(rollResultNumeric);

                int row = randomTalents.size() - 1;
                JTextField textField = (JTextField) randomTalentsPanel.getComponent(0, row);
                if (rollTalent instanceof TalentSingle) {
                    textField.setText(rollTalent.getName());
                    visibleRandomTalents.add((TalentSingle) rollTalent);
                } else {
                    TalentGroup rollTalentGroup = (TalentGroup) rollTalent;
                    Dimension dimension = new Dimension(textField.getWidth(), -1);
                    SearchableComboBox searchableComboBox = randomTalentsPanel.createSearchableComboBox(row, 0, dimension, false);
                    randomTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

                    rollTalentGroup.getSingleTalents().forEach(e->searchableComboBox.addItem(e.getName()));
                    searchableComboBox.refresh();
                    searchableComboBox.setLocked(false);
                    searchableComboBox.setEditable(!rollTalentGroup.isLocked());
                    searchableComboBox.setToolTipText(MultiLineTooltip.splitToolTip(rollTalentGroup.getName()));
                    visibleRandomTalents.add(rollTalentGroup.getSingleTalents().get(0));

                    searchableComboBox.addActionListener(e -> updateTalentRow(randomTalentsPanel, row, row, visibleRandomTalents,
                            rollTalentGroup.getSingleTalents().get(searchableComboBox.getSelectedIndex())));
                }
                updateTalentRow(randomTalentsPanel, row, row, visibleRandomTalents);

                if (sheet.getSubrace().getRandomTalents() == randomTalents.size()) {
                    rollButton.setEnabled(false);
                    rollResult.setEditable(false);
                    rollOKButton.setEnabled(false);
                }
            }
        }
    }
    private void rollRandomTalents() {
        rollRandomTalents(1);
    }

    private void createUIComponents() {
        points3Field = new JIntegerField(3, "3: %d/3");
        points5Field = new JIntegerField(3, "5: %d/3");
    }
}
