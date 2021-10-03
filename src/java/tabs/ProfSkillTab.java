package tabs;

import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.FilteredComboBox;
import components.GridPanel;
import components.JIntegerField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
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
import tools.Dice;
import tools.MultiLineTooltip;

public class ProfSkillTab extends SkillTab {
    private CharacterSheet sheet;
    private Connection connection;
    private final PropertyChangeSupport observersManager = new PropertyChangeSupport(this);

    private GridPanel skillsPanel;
    private GridPanel talentsPanel;
    private JPanel mainPanel;
    private JButton option1Button;
    private JIntegerField remainField;

    private int remainSkillPoints = 40;
    private int remainTalentPoints = 1;

    private List<Skill> profSkills = new ArrayList<>();
    private List<Talent> profTalents = new ArrayList<>();

    private final List<SkillSingle> visibleSkills = new ArrayList<>();
    private final List<TalentSingle> visibleTalents = new ArrayList<>();

    private final Dimension[] talentFieldDimensions = {
            new Dimension(200, -1),
            GridPanel.STANDARD_INTEGER_FIELD,
            GridPanel.STANDARD_INTEGER_FIELD,
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

        observersManager.addPropertyChangeListener("points", remainField);

        profSkills = new ArrayList<>(sheet.getProfession().getProfSkills().values());
        profSkills.forEach(e->e.linkAttributeMap(sheet.getAttributes()));
        profSkills.forEach(Skill::updateMinimalValue);
        profTalents = new ArrayList<>(sheet.getProfession().getProfTalents().values());
        profTalents.forEach(e->e.linkAttributeMap(sheet.getAttributes()));

        createSkillTable(profSkills);
        createTalentTable(profTalents, talentFieldDimensions);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
        AbstractActionHelper.createActionMnemonic(mainPanel, stroke, () -> {
            rollSkills();
//            rollTalents();
        });
        option1Button.addActionListener(e -> {
            visibleSkills.forEach(sheet::addSkill);
            visibleTalents.forEach(sheet::addTalent);
            sheet.saveJSON("src/resources/test.json");
        });
    }

    private void createSkillTable(List<Skill> profSkills) {
        List<Component> tabOrder = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;
        for (int i = 0; i < profSkills.size(); i++) {
            Skill raceSkill = profSkills.get(i);
            int finalRow = raceSkill.isAdv() ? advItr++ : baseItr++;
            int column = raceSkill.isAdv() ? 4 : 0;
            int finalI = i;
            int finalColumn = column;

            SkillSingle activeSkill = skillsPanel.createComboIfNeeded(raceSkill, finalRow, column++, SkillSingle::getColor,
                    newSkill -> updateSkillRow(skillsPanel, visibleSkills, finalI, finalRow, finalColumn, newSkill));
            visibleSkills.add(activeSkill);

            skillsPanel.createTextField(finalRow, column++, activeSkill.getAttrName(), GridPanel.STANDARD_INTEGER_FIELD, false);

            SpinnerModel model = new SpinnerNumberModel(activeSkill.getAdvValue(), activeSkill.getAdvValue(), activeSkill.getAdvValue()+10, 1);
            AdvancedSpinner jSpinner = skillsPanel.createAdvancedSpinner(finalRow, column++, model, new Dimension(35, -1), true);
            jSpinner.addChangeListener(e -> {
                if (!jSpinner.isLocked()) {
                    skillSpinnerChange(finalI, finalRow, finalColumn, jSpinner);
                }
            });

            skillsPanel.createIntegerField(finalRow, column++, activeSkill.getBaseSkill().getLinkedAttribute().getTotalValue(), GridPanel.STANDARD_INTEGER_FIELD, false);
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
    private void createTalentTable(List<Talent> profTalents, Dimension[] fieldDimensions) {
        talentsPanel.createJLabel(0,0,1,-1, "Talents");
        for (int i = 0; i < profTalents.size(); i++) {
            Talent raceTalent = profTalents.get(i);
            int finalI = i;
            int row = i + 1;
            int column = 0;

            TalentSingle activeTalent = talentsPanel.createComboIfNeeded(raceTalent, row, column++, t -> Color.BLACK, newTalent ->
                    updateTalentRow(talentsPanel, finalI, row, 0, visibleTalents, newTalent));
            visibleTalents.add(activeTalent);

            int currentLvl = activeTalent.getCurrentLvl();
            SpinnerModel model = new SpinnerNumberModel(currentLvl, 0, 1, 1);
            AdvancedSpinner spinner = talentsPanel.createAdvancedSpinner(row, column++, model, GridPanel.STANDARD_INTEGER_FIELD, currentLvl==0);
            spinner.addChangeListener(e->talentSpinnerChange(finalI, 1, spinner));

            talentsPanel.createIntegerField(row, column++, activeTalent.getMax(), fieldDimensions[column-1], false);

            JTextArea testArea = talentsPanel.createTextArea(row, column++, activeTalent.getBaseTalent().getTest(), fieldDimensions[column-1], false);
            testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

            String tooltip = activeTalent.getBaseTalent().getDesc();
            talentsPanel.createJLabel(row, column++, new ImageIcon("src/resources/images/info.png"), MultiLineTooltip.splitToolTip(tooltip, 75, 10));
        }
        talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }

    private void skillSpinnerChange(int idx, int row, int column, AdvancedSpinner jSpinner) {
        int last = visibleSkills.get(idx).getAdvValue();
        int now = (int) jSpinner.getValue();

        if (last != now) {
            observersManager.firePropertyChange("points", remainSkillPoints, remainSkillPoints +last-now);
            remainSkillPoints = remainSkillPoints + last - now;
            visibleSkills.get(idx).setAdvValue(now);
            updateSkillRow(skillsPanel, visibleSkills, idx, row, column);
        }
        option1Button.setEnabled(remainSkillPoints == 0);
    }
    private void talentSpinnerChange(int idx, int column, AdvancedSpinner spinner) {
        int last = visibleTalents.get(idx).getCurrentLvl();
        int now = (int) spinner.getValue();

        if (last != now) {
            remainTalentPoints = remainTalentPoints + last - now;
            visibleTalents.get(idx).setCurrentLvl(now);

            talentsPanel.iterateThroughRows(column, (o, i) -> {
                SpinnerNumberModel model = (SpinnerNumberModel) ((AdvancedSpinner) o).getModel();
                model.setMaximum(Math.max((int) model.getValue(), remainTalentPoints));
            });
        }
    }

    private void updateTalentRow(GridPanel panel, int idx, int row, int column, List<TalentSingle> talentList, TalentSingle newTalent) {
        Component nameField = panel.getComponent(column, row);
        if (nameField instanceof FilteredComboBox) {
            ((FilteredComboBox<?>) nameField).setSelectedItem(newTalent);
        }

        ((AdvancedSpinner) panel.getComponent(column + 1, row)).setValue(newTalent.getCurrentLvl());
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
        int[] values = Dice.randomInts(profSkills.size(), 0, 10, 40);

        int baseItr = 1;
        int advItr = 1;
        for (int i=0; i<profSkills.size(); i++) {
            Skill profSkill = profSkills.get(i);
            int row = profSkill.isAdv() ? advItr++ : baseItr++;
            int column = profSkill.isAdv() ? 4 : 0;

            SkillSingle newSkill = Dice.randomItem(profSkills.get(i).getSingleSkills());
            updateSkillRow(skillsPanel, visibleSkills, i, row, column, newSkill);
            ((AdvancedSpinner) skillsPanel.getComponent(column + 2, row)).setValue(newSkill.getMinimalValue()+ values[i]);
        }
    }
    private void rollTalents() {
        for (int i = 0; i < profTalents.size(); i++) {
            Talent talent = profTalents.get(i);
            TalentSingle activeTalent;
            if (talent instanceof TalentSingle) {
                activeTalent = (TalentSingle) profTalents.get(i);
            } else {
                activeTalent = (TalentSingle) ((TalentGroup) profTalents.get(i)).getRndTalent();
            }
            updateTalentRow(talentsPanel, i, i + 1, 0, visibleTalents, activeTalent);
        }
    }

    private void createUIComponents() {
        remainField = new JIntegerField(40, "%d/40");
    }
}
