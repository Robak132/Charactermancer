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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import main.CharacterGen;
import main.CharacterSheet;
import main.Connection;
import mappings.Skill;
import mappings.SkillSingle;
import mappings.Talent;
import mappings.TalentGroup;
import mappings.TalentSingle;
import tools.AbstractActionHelper;
import tools.ColorPalette;
import tools.MultiLineTooltip;

public class ProfSkillTab {
    private CharacterSheet sheet;
    private Connection connection;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private GridPanel skillsPanel;
    private GridPanel talentsPanel;
    private JPanel mainPanel;
    private JButton option1Button;
    private JIntegerField remainField;

    private List<Skill> profSkills = new ArrayList<>();
    private List<Talent> raceTalents = new ArrayList<>();
    private final List<SkillSingle> visibleRaceSkills = new ArrayList<>();
    private final List<TalentSingle> visibleRaceTalents = new ArrayList<>();

    private final Dimension[] talentFieldDimensions = {
            new Dimension(200, -1),
            new Dimension(30, -1),
            new Dimension(30, -1),
            new Dimension(200, -1)
    };

    public ProfSkillTab() {
        // Needed for GUI Designer
    }
    public ProfSkillTab(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        initialise(parent, sheet, connection);
    }

    public void initialise(CharacterGen parent, CharacterSheet sheet, Connection connection) {
        this.sheet = sheet;
        this.connection = connection;

        this.addObserver("points5", remainField);

        profSkills = sheet.getProfession().getProfSkills(sheet.getAttributes());
        raceTalents = sheet.getProfession().getProfTalents(sheet.getAttributes());

        createSkillTable(profSkills);
        createTalentTable(raceTalents, talentFieldDimensions);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
        AbstractActionHelper.createActionMnemonic(mainPanel, stroke, () -> {
            rollSkills();
            rollTalents();
        });
        option1Button.addActionListener(e -> {
            sheet.setSkillList(visibleRaceSkills);
            sheet.setTalentList(visibleRaceTalents);

            System.out.println(sheet);
        });
    }

    private void createSkillTable(List<Skill> raceSkills) {
        List<Component> tabOrder = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;
        int row;
        int column;
        for (int i = 0; i < raceSkills.size(); i++) {
            Skill raceSkill = raceSkills.get(i);
            Color color = raceSkill.isEarning() ? ColorPalette.BLUE : Color.black;

            if (raceSkill.isAdv()) {
                column = 4;
                row = advItr++;
                color = Color.red;
            } else {
                column = 0;
                row = baseItr++;
            }
            int finalI = i;
            int finalRow = row;
            int finalColumn = column;

            SkillSingle activeSkill = skillsPanel.createComboIfNeeded(raceSkill, row, column++, color, visibleRaceSkills,
                    newSkill -> updateSkillRow(finalI, finalRow, finalColumn, newSkill));

            skillsPanel.createTextField(row, column++, activeSkill.getAttrName(), new Dimension(30, -1), false);

            AdvancedSpinner jSpinner = skillsPanel.createAdvancedSpinner(row, column++,
                    new SpinnerNumberModel(activeSkill.getAdvValue(), activeSkill.getAdvValue(), 10, 1), new Dimension(35, -1), true);
            jSpinner.addChangeListener(e -> skillSpinnerChange(finalI, finalRow, finalColumn, jSpinner));

            skillsPanel.createIntegerField(row, column++, activeSkill.getBaseSkill().getLinkedAttribute().getTotalValue(), new Dimension(30, -1), false);

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
    private void skillSpinnerChange(int idx, int row, int column, AdvancedSpinner jSpinner) {
        int lastManual = visibleRaceSkills.get(idx).getAdvValue();
        int lastAuto = (int) jSpinner.getLastValue();
        int now = (int) jSpinner.getValue();

        if (lastManual != now) {
            // Manual change of Spinner
//            pcs.firePropertyChange("points" + lastManual, (int) raceskillPoints.get(lastManual), raceskillPoints.get(lastManual) + 1);
//            pcs.firePropertyChange("points" + now, (int) raceskillPoints.get(now), raceskillPoints.get(now) - 1);
//            raceskillPoints.put(lastManual, raceskillPoints.get(lastManual) + 1);
//            raceskillPoints.put(now, raceskillPoints.get(now) - 1);
            visibleRaceSkills.get(idx).setAdvValue(now);
            updateSkillRow(idx, row, column);
        } else if (lastAuto != now) {
            // Automatic change via updateAll()
//            pcs.firePropertyChange("points" + lastAuto, (int) raceskillPoints.get(lastAuto), raceskillPoints.get(lastAuto) + 1);
//            pcs.firePropertyChange("points" + now, (int) raceskillPoints.get(now), raceskillPoints.get(now) - 1);
//            raceskillPoints.put(lastAuto, raceskillPoints.get(lastAuto) + 1);
//            raceskillPoints.put(now, raceskillPoints.get(now) - 1);
        }
    }
    private void updateSkillRow(int idx, int row, int column, SkillSingle newSkill) {
        boolean colorChange = newSkill.isAdv() && newSkill.getAdvValue()==0;
        if (skillsPanel.getComponent(column, row) instanceof JTextField) {
            ((JTextField) skillsPanel.getComponent(column, row)).setText(newSkill.getName());
        } else {
            ((SearchableComboBox) skillsPanel.getComponent(column, row)).setSelectedItem(newSkill.getName());
        }
        skillsPanel.getComponent(column, row).setForeground(colorChange ? Color.RED : Color.BLACK);

        ((JTextField) skillsPanel.getComponent(column + 1, row)).setText(newSkill.getAttrName());
        ((AdvancedSpinner) skillsPanel.getComponent(column + 2, row)).setValue(newSkill.getAdvValue());
        ((JIntegerField) skillsPanel.getComponent(column + 3, row)).setValue(newSkill.getTotalValue());

        if (visibleRaceSkills.size() > idx) {
            visibleRaceSkills.set(idx, newSkill);
        }
    }
    private void updateSkillRow(int idx, int row, int column) {
        updateSkillRow(idx, row, column, visibleRaceSkills.get(idx));
    }

    private void createTalentTable(List<Talent> raceTalents, Dimension[] fieldDimensions) {
        talentsPanel.createJLabel(0,0,1,-1, "Talents");
        for (int i = 0; i < raceTalents.size(); i++) {
            Talent raceTalent = raceTalents.get(i);
            int finalI = i;
            int row = i + 1;
            int column = 0;

            TalentSingle activeTalent = talentsPanel.createComboIfNeeded(raceTalent, row, column++, visibleRaceTalents,
                    newTalent -> updateTalentRow(talentsPanel, finalI, row, 0, visibleRaceTalents, newTalent));

            talentsPanel.createIntegerField(row, column++, activeTalent.getCurrentLvl(), fieldDimensions[column-1], false);

            talentsPanel.createIntegerField(row, column++, activeTalent.getMax(), fieldDimensions[column-1], false);

            JTextArea testArea = talentsPanel.createTextArea(row, column++, activeTalent.getBaseTalent().getTest(), fieldDimensions[column-1], false);
            testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

            String tooltip = activeTalent.getBaseTalent().getDesc();
            talentsPanel.createJLabel(row, column, new ImageIcon("src/resources/images/info.png"), MultiLineTooltip.splitToolTip(tooltip, 75, 10));
        }
        talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }
    private TalentSingle createComboIfNeeded(Talent raceTalent, int idx, int row, int column) {
        TalentSingle activeTalent;
        if (raceTalent instanceof TalentSingle) {
            activeTalent = (TalentSingle) raceTalent;
            talentsPanel.createTextField(row, column, activeTalent.getName(), null, false);
        } else {
            TalentGroup talentGroup = (TalentGroup) raceTalent;
            activeTalent = talentGroup.getSingleTalents().get(0);
            SearchableComboBox talentNameCombo = talentsPanel.createSearchableComboBox(row, column, null, false);
            talentNameCombo.setToolTipText(MultiLineTooltip.splitToolTip(talentGroup.getName()));
            for (TalentSingle alternateTalent : talentGroup.getSingleTalents()) {
                talentNameCombo.addItem(alternateTalent.getName());
            }
            talentNameCombo.setPreferredSize(new Dimension(talentNameCombo.getSize().width, -1));
            talentNameCombo.refresh();
            talentNameCombo.addActionListener(e -> updateTalentRow(talentsPanel, idx, row, column, visibleRaceTalents,
                    talentGroup.getSingleTalents().get(talentNameCombo.getSelectedIndex())));
        }
        return activeTalent;
    }
    private void updateTalentRow(GridPanel panel, int idx, int row, int column, List<TalentSingle> talentList, TalentSingle newTalent) {
        if (panel.getComponent(column, row) instanceof JTextField) {
            ((JTextField) panel.getComponent(column, row)).setText(newTalent.getName());
        } else {
            ((SearchableComboBox) panel.getComponent(column, row)).setSelectedItem(newTalent.getName());
        }

        ((JIntegerField) panel.getComponent(column + 1, row)).setValue(newTalent.getCurrentLvl());
        ((JIntegerField) panel.getComponent(column + 2, row)).setValue(newTalent.getMax());
        ((JTextArea) panel.getComponent(column + 3, row)).setText(newTalent.getBaseTalent().getTest());
        ((JLabel) panel.getComponent(column + 4, row)).setToolTipText(MultiLineTooltip.splitToolTip(newTalent.getBaseTalent().getDesc()));

        if (talentList.size() > idx) {
            talentList.set(idx, newTalent);
        }
    }
    private void updateTalentRow(GridPanel panel, int idx, int row, int column, List<TalentSingle> talentList) {
        updateTalentRow(panel, idx, row, column, talentList, talentList.get(idx));
    }

    private void rollSkills() {
        List<SkillSingle> updatedSkills = CharacterGen.randomizeSkillsWithList(profSkills, new int[] {3, 3, 3, 5, 5, 5});
        int baseItr = 1;
        int advItr = 1;
        int row;
        int column;
        for (int i=0;i<updatedSkills.size();i++) {
            if (updatedSkills.get(i).isAdv()) {
                column = 4;
                row = advItr++;
            } else {
                column = 0;
                row = baseItr++;
            }
            updateSkillRow(i, row, column, updatedSkills.get(i));
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
            updateTalentRow(talentsPanel, i, i + 1, 0, visibleRaceTalents, activeTalent);
        }
    }

    private void addObserver(String propertyName, PropertyChangeListener l) {
        pcs.addPropertyChangeListener(propertyName, l);
    }
}
