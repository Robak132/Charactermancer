package charactergen;

import components.AdvancedSpinner;
import components.FilteredComboBox;
import components.GridPanel;
import components.JIntegerField;
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
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import main.CharacterSheet;
import main.Connection;
import mappings.Skill;
import mappings.SkillSingle;
import mappings.Talent;
import mappings.TalentGroup;
import mappings.TalentSingle;
import org.apache.logging.log4j.LogManager;
import tools.AbstractActionHelper;
import tools.Dice;
import tools.MultiLineTooltip;
import tools.SkillTab;
import tools.TalentTab;

class RaceSkillTab implements SkillTab, TalentTab {
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

    public RaceSkillTab() {
        // Needed for GUI Designer
    }
    public RaceSkillTab(CharacterGen parent, CharacterSheet sheet) {
        initialise(parent, sheet);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet) {
        this.sheet = sheet;
        this.connection = parent.getConnection();

        raceskillPoints = new ConcurrentHashMap<>(Map.of(0, 0, 3, 3, 5, 3));
        observersManager.addPropertyChangeListener("points3", points3Field);
        observersManager.addPropertyChangeListener("points5", points5Field);

        raceSkills = new ArrayList<>(sheet.getRace().getRaceSkills(sheet.getProfession().getProfSkills()).values());
        raceSkills.forEach(s -> s.linkAttributeMap(sheet.getAttributes()));
        raceTalents = new ArrayList<>(sheet.getRace().getRaceTalents(sheet.getProfession().getProfTalents()).values());
        raceTalents.forEach(s -> s.linkAttributeMap(sheet.getAttributes()));

        createSkillTable(skillsPanel, raceSkills, visibleRaceSkills, SkillSingle::getFullColor);
        createTalentTable(talentsPanel, raceTalents, visibleRaceTalents, TalentSingle::getColor);
        createRandomTalentTable();

        AbstractActionHelper.createActionMnemonic(mainPanel, KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK, () -> {
            rollSkills();
            rollTalents();
            rollRandomTalents(sheet.getSubrace().getRandomTalents());
        });

        rollButton.addActionListener(e -> rollRandomTalents());
        rollButton.setMnemonic(KeyEvent.VK_R);
        rollOKButton.addActionListener(e -> {
            int rollResultNumeric = rollResult.getValue();
            if (rollResultNumeric <= 0 || rollResultNumeric > 100) {
                return;
            }

            Talent rollTalent = connection.getRandomTalent(rollResultNumeric);
            if (randomTalents.contains(rollTalent)) {
                // TODO: Make "Roll doubled" dialog or do sth better
            } else {
                rollTalent.linkAttributeMap(sheet.getAttributes());
                rollTalent.setCurrentLvl(1);
                randomTalents.add(rollTalent);

                createRandomTalent(rollTalent, randomTalents.size() - 1);

                if (sheet.getSubrace().getRandomTalents() == randomTalents.size()) {
                    rollButton.setEnabled(false);
                    rollResult.setEditable(false);
                    rollOKButton.setEnabled(false);
                } else {
                    rollResult.setText("");
                }
            }
        });
        option1Button.addActionListener(e -> {
            visibleRaceSkills.forEach(sheet::addSkill);
            visibleRaceTalents.forEach(sheet::addTalent);
            visibleRandomTalents.forEach(sheet::addTalent);

            skillsPanel.iterateThroughRows(0, (o, i) -> {
                try {
                    ((FilteredComboBox<?>) o).setLocked(true);
                } catch (ClassCastException ignored) {}
            });
            skillsPanel.iterateThroughRows(2, (o, i) -> ((AdvancedSpinner) o).setEnabled(false));
            skillsPanel.iterateThroughRows(4, (o, i) -> {
                try {
                    ((FilteredComboBox<?>) o).setLocked(true);
                } catch (ClassCastException ignored) {}
            });
            skillsPanel.iterateThroughRows(6, (o, i) -> ((AdvancedSpinner) o).setEnabled(false));

            talentsPanel.iterateThroughRows(0, (o, i) -> {
                try {
                    ((FilteredComboBox<?>) o).setLocked(true);
                } catch (ClassCastException ignored) {}
            });
            option1Button.setEnabled(false);

            parent.moveToNextTab();
        });
        option1Button.setMnemonic(KeyEvent.VK_1);
    }

    @Override
    public SpinnerModel getSkillSpinnerModel(SkillSingle skill) {
        return new SpinnerListModel(Arrays.asList(0, 3, 5));
    }
    @Override
    public void skillSpinnerChange(int idx, int row, int column, AdvancedSpinner jSpinner) {
        int last = visibleRaceSkills.get(idx).getAdvValue();
        int now = (int) jSpinner.getValue();

        if (last != now) {
            observersManager.firePropertyChange("points" + last, (int) raceskillPoints.get(last), raceskillPoints.get(last) + 1);
            observersManager.firePropertyChange("points" + now, (int) raceskillPoints.get(now), raceskillPoints.get(now) - 1);
            raceskillPoints.put(last, raceskillPoints.get(last) + 1);
            raceskillPoints.put(now, raceskillPoints.get(now) - 1);
            visibleRaceSkills.get(idx).setAdvValue(now);
            updateSkillRow(skillsPanel, visibleRaceSkills, idx, row, column);
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

    @Override
    public SpinnerModel getTalentSpinnerModel(TalentSingle talent) {
        return new SpinnerNumberModel(1, 1, 1, 1);
    }

    private void createRandomTalentTable() {
        if (sheet.getSubrace().getRandomTalents() != 0) {
            rollPanel.setVisible(true);
            for (int row=0; row < sheet.getSubrace().getRandomTalents(); row++) {
                int column = 0;

                randomTalentsPanel.createTextField(row, column++, "", GridPanel.STANDARD_TEXT_FIELD, false);
                randomTalentsPanel.createAdvancedSpinner(row, column++, new SpinnerNumberModel(1, 1, 1, 1), GridPanel.STANDARD_INTEGER_FIELD, false);
                randomTalentsPanel.createIntegerField(row, column++, 0, GridPanel.STANDARD_INTEGER_FIELD, false);

                JTextArea testArea = randomTalentsPanel.createTextArea(row, column++, "", GridPanel.STANDARD_TEXT_FIELD, false);
                testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

                randomTalentsPanel.createJLabel(row, column, new ImageIcon("src/resources/images/info.png"), "");
            }
            randomTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
        }
    }
    private void createRandomTalent(Talent talent, int row) {
        JTextField textField = (JTextField) randomTalentsPanel.getComponent(0, row);
        if (talent instanceof TalentSingle) {
            textField.setText(talent.getName());
            visibleRandomTalents.add((TalentSingle) talent);
        } else {
            TalentGroup rollTalentGroup = (TalentGroup) talent;
            Dimension dimension = new Dimension(textField.getWidth(), -1);
            FilteredComboBox<TalentSingle> filteredComboBox = randomTalentsPanel.createFilteredComboBox(row, 0, dimension, TalentSingle::getName);
            randomTalentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);

            filteredComboBox.addItems(rollTalentGroup.getSingleTalents());
            filteredComboBox.setToolTipText(MultiLineTooltip.splitToolTip(rollTalentGroup.getName()));
            visibleRandomTalents.add(rollTalentGroup.getSingleTalents().get(0));

            filteredComboBox.addActionListener(e -> {
                if (!filteredComboBox.isEditing()) {
                    updateTalentRow(randomTalentsPanel, row, row, visibleRandomTalents, TalentSingle::getColor, (TalentSingle) filteredComboBox.getSelectedItem());
                }
            });
        }
        updateTalentRow(randomTalentsPanel, row, row, visibleRandomTalents, TalentSingle::getColor);
    }

    private void rollSkills() {
        List<Integer> values = Dice.randomIntPermutation(raceSkills.size(), new Integer[] {3, 3, 3, 5, 5, 5});
        skillsPanel.iterateThroughRows(2, (o, idx) -> ((AdvancedSpinner) o).setValue(0));
        skillsPanel.iterateThroughRows(6, (o, idx) -> ((AdvancedSpinner) o).setValue(0));

        int baseItr = 1;
        int advItr = 1;
        for (int i=0; i<raceSkills.size(); i++) {
            Skill raceSkill = raceSkills.get(i);
            int row = raceSkill.isAdv() ? advItr++ : baseItr++;
            int column = raceSkill.isAdv() ? 4 : 0;

            SkillSingle newSkill = Dice.randomItem(raceSkill.getSingleSkills());
            updateSkillRow(skillsPanel, visibleRaceSkills, i, row, column, newSkill);
            ((AdvancedSpinner) skillsPanel.getComponent(column + 2, row)).setValue(values.get(i));
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
            updateTalentRow(talentsPanel, i, i + 1, visibleRaceTalents, TalentSingle::getColor, activeTalent);
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

                createRandomTalent(rollTalent, randomTalents.size() - 1);

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
