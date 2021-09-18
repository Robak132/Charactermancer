package tabs;

import components.AdvancedSpinner;
import components.CustomFocusTraversalPolicy;
import components.GridPanel;
import components.JIntegerField;
import components.SearchableComboBox;
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
import javax.swing.JTextField;
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

    private int remainValue = 40;

    private List<Skill> profSkills = new ArrayList<>();
    private List<Talent> profTalents = new ArrayList<>();

    private final List<SkillSingle> visibleSkills = new ArrayList<>();
    private final List<TalentSingle> visibleTalents = new ArrayList<>();

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

        observersManager.addPropertyChangeListener("points", remainField);

        profSkills = new ArrayList<>(sheet.getProfession().getProfSkills().values());
        profSkills.forEach(e->e.linkAttributeMap(sheet.getAttributes()));
        profTalents = new ArrayList<>(sheet.getProfession().getProfTalents().values());
        profTalents.forEach(e->e.linkAttributeMap(sheet.getAttributes()));

        createSkillTable(profSkills);
//        createTalentTable(raceTalents, talentFieldDimensions);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
        AbstractActionHelper.createActionMnemonic(mainPanel, stroke, () -> {
//            rollSkills();
            rollTalents();
        });
        option1Button.addActionListener(e -> {
//            sheet.setSkillList(visibleRaceSkills);
//            sheet.setTalentList(visibleRaceTalents);

            sheet.ToJSON();
//            System.out.println(sheet);
        });
    }

    private void createSkillTable(List<Skill> raceSkills) {
        List<Component> tabOrder = new ArrayList<>();
        int baseItr = 1;
        int advItr = 1;
        for (int i = 0; i < raceSkills.size(); i++) {
            Skill raceSkill = raceSkills.get(i);
            int finalRow = raceSkill.isAdv() ? advItr++ : baseItr++;
            int column = raceSkill.isAdv() ? 4 : 0;
            int finalI = i;
            int finalColumn = column;

            SkillSingle activeSkill = skillsPanel.createComboIfNeeded(raceSkill, finalRow, column++, SkillSingle::getColor,
                    newSkill -> updateSkillRow(skillsPanel, visibleSkills, finalI, finalRow, finalColumn, newSkill));
            visibleSkills.add(activeSkill);

            skillsPanel.createTextField(finalRow, column++, activeSkill.getAttrName(), new Dimension(30, -1), false);

            SpinnerModel model = new SpinnerNumberModel(activeSkill.getAdvValue(), activeSkill.getAdvValue(), 10, 1);
            AdvancedSpinner jSpinner = skillsPanel.createAdvancedSpinner(finalRow, column++, model, new Dimension(35, -1), true);
            jSpinner.addChangeListener(e -> {
                if (!jSpinner.isLocked()) {
                    skillSpinnerChange(finalI, finalRow, finalColumn, jSpinner);
                }
            });

            skillsPanel.createIntegerField(finalRow, column++, activeSkill.getBaseSkill().getLinkedAttribute().getTotalValue(), new Dimension(30, -1), false);
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

            TalentSingle activeTalent = talentsPanel.createComboIfNeeded(raceTalent, row, column++, TalentSingle::getColor,
                    newTalent -> updateTalentRow(talentsPanel, finalI, row, 0, visibleTalents, newTalent));

            talentsPanel.createIntegerField(row, column++, activeTalent.getCurrentLvl(), fieldDimensions[column-1], false);

            talentsPanel.createIntegerField(row, column++, activeTalent.getMax(), fieldDimensions[column-1], false);

            JTextArea testArea = talentsPanel.createTextArea(row, column++, activeTalent.getBaseTalent().getTest(), fieldDimensions[column-1], false);
            testArea.setFont(testArea.getFont().deriveFont(Font.PLAIN, 10));

            String tooltip = activeTalent.getBaseTalent().getDesc();
            talentsPanel.createJLabel(row, column, new ImageIcon("src/resources/images/info.png"), MultiLineTooltip.splitToolTip(tooltip, 75, 10));
        }
        talentsPanel.build(GridPanel.ALIGNMENT_HORIZONTAL);
    }

    private void skillSpinnerChange(int idx, int row, int column, AdvancedSpinner jSpinner) {
        int last = visibleSkills.get(idx).getAdvValue();
        int now = (int) jSpinner.getValue();

        if (last != now) {
            observersManager.firePropertyChange("points", remainValue, remainValue+last-now);
            remainValue = remainValue+last-now;
            visibleSkills.get(idx).setAdvValue(now);
            updateSkillRow(skillsPanel, visibleSkills, idx, row, column);
        }
        option1Button.setEnabled(remainValue == 0);
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
        profSkills = new ArrayList<>(sheet.getProfession().getProfSkills().values());
        profSkills.forEach(e->e.linkAttributeMap(sheet.getAttributes()));

        List<SkillSingle> updatedSkills = new ArrayList<>();
        for (Skill skill : profSkills) {
            updatedSkills.add((SkillSingle) Dice.randomItem(skill.getSingleSkills()));
        }

        List<SkillSingle> lookupList = new ArrayList<>(updatedSkills);
        for (int i=0; i<40; i++) {
            int index = Dice.randomInt(0, lookupList.size()-1);
            int newValue = lookupList.get(index).getAdvValue()+1;
            lookupList.get(index).setAdvValue(newValue);
            if (newValue == 10) {
                lookupList.remove(index);
            }
        }

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
            updateSkillRow(skillsPanel, visibleSkills, i, row, column, updatedSkills.get(i));
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
