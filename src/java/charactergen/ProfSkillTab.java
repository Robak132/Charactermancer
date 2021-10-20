package charactergen;

import components.AdvancedSpinner;
import components.GridPanel;
import components.JIntegerField;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import main.CharacterSheet;
import main.Connection;
import mappings.Skill;
import mappings.SkillSingle;
import mappings.Talent;
import mappings.TalentGroup;
import mappings.TalentSingle;
import tools.AbstractActionHelper;
import tools.Dice;
import tools.SkillTab;
import tools.TalentTab;

class ProfSkillTab implements SkillTab, TalentTab {
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

        createSkillTable(skillsPanel, profSkills, visibleSkills, SkillSingle::getColor);
        createTalentTable(talentsPanel, profTalents, visibleTalents);

        AbstractActionHelper.createActionMnemonic(mainPanel, KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK, () -> {
            rollSkills();
//            rollTalents(TalentSingle::getColor);
        });
        option1Button.addActionListener(e -> {
            visibleSkills.forEach(sheet::addSkill);
            visibleTalents.forEach(sheet::addTalent);
            sheet.saveJSON("src/resources/test.json");
        });
    }

    @Override
    public SpinnerModel getSkillSpinnerModel(SkillSingle activeSkill) {
        return new SpinnerNumberModel(activeSkill.getAdvValue(), activeSkill.getAdvValue(), activeSkill.getAdvValue()+10, 1);
    }
    @Override
    public void skillSpinnerChange(int idx, int row, int column, AdvancedSpinner jSpinner) {
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

    @Override
    public SpinnerModel getTalentSpinnerModel(TalentSingle talent) {
        return new SpinnerNumberModel(talent.getCurrentLvl(), 0, 1, 1);
    }
    @Override
    public void talentSpinnerChange(int idx, int row, int column, AdvancedSpinner spinner) {
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
    private void rollTalents(Function<TalentSingle, Color> colorFunction) {
        for (int i = 0; i < profTalents.size(); i++) {
            Talent talent = profTalents.get(i);
            TalentSingle activeTalent;
            if (talent instanceof TalentSingle) {
                activeTalent = (TalentSingle) profTalents.get(i);
            } else {
                activeTalent = (TalentSingle) ((TalentGroup) profTalents.get(i)).getRndTalent();
            }
            updateTalentRow(talentsPanel, i, i + 1, visibleTalents, colorFunction, activeTalent);
        }
    }

    private void createUIComponents() {
        remainField = new JIntegerField(40, "%d/40");
    }

}
