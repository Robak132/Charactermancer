package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import mappings.Profession;
import mappings.ProfessionCareer;
import mappings.ProfessionClass;
import mappings.Race;
import mappings.Subrace;
import org.apache.logging.log4j.LogManager;
import tools.Dice;

public class CharacterSheetMaker {
    public JPanel mainPanel;

    private final JFrame frame;
    private Main parent;
    private final Connection connection;
    private final CharacterSheet sheet;

    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JButton exitButton;

    private JComboBox<String> raceCombo;
    private JToggleButton raceButton;
    private JComboBox<String> subraceCombo;
    private JToggleButton subraceButton;

    private JComboBox<String> classCombo;
    private JToggleButton classButton;
    private JComboBox<String> careerCombo;
    private JToggleButton careerButton;
    private JComboBox<String> professionCombo;
    private JToggleButton professionButton;
    private JLabel errorLabel;
    private JButton refreshButton;

    // Visible Element Lists
    private final List<Race> visibleRaces = new ArrayList<>();
    private List<Subrace> visibleSubraces = new ArrayList<>();
    private final List<ProfessionClass> visibleClasses = new ArrayList<>();
    private List<ProfessionCareer> visibleCareers = new ArrayList<>();
    private List<Profession> visibleProfessions = new ArrayList<>();

    // Constraints
    private Race race;
    private Subrace subrace;
    private Profession profession;
    private ProfessionCareer professionCareer;
    private ProfessionClass professionClass;

    // Compatible Elements
    private List<Subrace> compatibleSubraces;
    private Map<Subrace, List<ProfessionCareer>> compatibleCareers;

    private boolean lock = false;

    public CharacterSheetMaker(JFrame frame, Main parent, Connection connection) {
        this.frame = frame;
        this.parent = parent;
        this.connection = connection;
        sheet = new CharacterSheet();

        buildRace();
        buildProfession();
        refreshAll();

        raceCombo.addActionListener(e -> {
            if (!lock) {
                raceButton.setSelected(true);
                race = visibleRaces.get(raceCombo.getSelectedIndex());
                refreshAll();
            }
        });
        raceButton.addActionListener(e -> {
            if (raceButton.isSelected()) {
                race = visibleRaces.get(raceCombo.getSelectedIndex());
            } else {
                race = null;
            }
            refreshAll();
        });
        subraceCombo.addActionListener(e -> {
            if (!lock) {
                subraceButton.setSelected(true);
                subrace = visibleSubraces.get(subraceCombo.getSelectedIndex());
                refreshAll();
            }
        });
        subraceButton.addActionListener(e -> {
            if (subraceButton.isSelected()) {
                subrace = visibleSubraces.get(subraceCombo.getSelectedIndex());
            } else {
                subrace = null;
            }
            refreshAll();
        });

        classCombo.addActionListener(e -> {
            if (!lock) {
                classButton.setSelected(true);
                professionClass = visibleClasses.get(classCombo.getSelectedIndex());
                refreshAll();
            }
        });
        classButton.addActionListener(e -> {
            if (classButton.isSelected()) {
                professionClass = visibleClasses.get(classCombo.getSelectedIndex());
            } else {
                professionClass = null;
            }
            refreshAll();
        });
        careerCombo.addActionListener(e -> {
            if (!lock) {
                careerButton.setSelected(true);
                professionCareer = visibleCareers.get(careerCombo.getSelectedIndex());
                refreshAll();
            }
        });
        careerButton.addActionListener(e -> {
            if (careerButton.isSelected()) {
                professionCareer = visibleCareers.get(careerCombo.getSelectedIndex());
            } else {
                professionCareer = null;
            }
            refreshAll();
        });
        professionCombo.addActionListener(e -> {
            if (!lock) {
                professionButton.setSelected(true);
                profession = visibleProfessions.get(professionCombo.getSelectedIndex());
                refreshAll();
            }
        });
        professionButton.addActionListener(e -> {
            if (professionButton.isSelected()) {
                profession = visibleProfessions.get(professionCombo.getSelectedIndex());
            } else {
                profession = null;
            }
            refreshAll();
        });

        refreshButton.addActionListener(e -> refreshAll());
        exitButton.addActionListener(e -> {
            this.frame.setContentPane(parent.mainPanel);
            this.frame.validate();
        });
    }
    private void refreshAll() {
        lock = true;
        logConstraints();
        try {
            errorLabel.setVisible(false);
            compatibleSubraces = findCompatibleSubraces(race);
            findCompatibleProfessions();
            applyAll();
        } catch (InvalidParametersException ex) {
            errorLabel.setText(ex.getMessage());
            errorLabel.setVisible(true);
        }
        lock = false;
    }

    private void buildRace() {
        visibleRaces.addAll(connection.getRaces());
        raceCombo.removeAllItems();
        visibleRaces.forEach(e -> raceCombo.addItem(e.getName()));
    }
    private void buildProfession() {
        visibleClasses.addAll(connection.getProfessionClasses());
        classCombo.removeAllItems();
        visibleClasses.forEach(e -> classCombo.addItem(e.getName()));

        refreshCareer();
        refreshProfession();
    }

    private void refreshSubrace() {
        visibleSubraces = visibleRaces.get(raceCombo.getSelectedIndex()).getSubraces();
        subraceCombo.removeAllItems();
        visibleSubraces.forEach(e -> subraceCombo.addItem(e.getName()));
    }
    private void refreshCareer() {
        visibleCareers = visibleClasses.get(classCombo.getSelectedIndex()).getCareers();
        careerCombo.removeAllItems();
        visibleCareers.forEach(e -> careerCombo.addItem(e.getName()));
    }
    private void refreshProfession() {
        visibleProfessions = visibleCareers.get(careerCombo.getSelectedIndex()).getProfessions();
        professionCombo.removeAllItems();
        visibleProfessions.forEach(e -> professionCombo.addItem(e.getName()));
    }

    private List<Subrace> findCompatibleSubraces(Race race) throws InvalidParametersException {
        List<Subrace> compatibleSubraces = connection.getSubraces(race);
        if (subrace != null) {
            if (!compatibleSubraces.contains(subrace)) {
                throw new InvalidParametersException("No subrace found");
            }
            compatibleSubraces.clear();
            compatibleSubraces.add(subrace);
        }

        return compatibleSubraces;
    }
    private void findCompatibleProfessions() throws InvalidParametersException {
        compatibleCareers = new HashMap<>();
        for (Subrace subrace : compatibleSubraces) {
            compatibleCareers.put(subrace, subrace.getBaseRace().getRaceCareers());
        }

        if (professionClass != null) {
            List<Subrace> newCompatibleSubraces = new ArrayList<>();
            compatibleCareers.clear();
            for (Subrace subrace : compatibleSubraces) {
                List<ProfessionCareer> intersection = new ArrayList<>(professionClass.getCareers());
                intersection.retainAll(subrace.getBaseRace().getRaceCareers());
                if (intersection.size() != 0) {
                    newCompatibleSubraces.add(subrace);
                    compatibleCareers.put(subrace, intersection);
                }
            }
            compatibleSubraces = newCompatibleSubraces;
            refreshCareer();
            refreshProfession();
        }
        if (professionCareer != null) {
            List<Subrace> newCompatibleSubraces = new ArrayList<>();
            for (Subrace subrace : compatibleSubraces) {
                if (subrace.getBaseRace().getRaceCareers().contains(professionCareer)) {
                    newCompatibleSubraces.add(subrace);
                }
            }
            compatibleSubraces = newCompatibleSubraces;
            refreshCareer();
            refreshProfession();
        }
        if (profession != null) {
            List<Subrace> newCompatibleSubraces = new ArrayList<>();
            for (Subrace subrace : compatibleSubraces) {
                if (subrace.getBaseRace().getRaceCareers().contains(profession.getProfessionCareer())) {
                    newCompatibleSubraces.add(subrace);
                }
            }
            compatibleSubraces = newCompatibleSubraces;
        }
    }
    private void applyAll() {
        Subrace selectedSubrace = (Subrace) Dice.randomItem(compatibleSubraces);

        raceCombo.setSelectedItem(selectedSubrace.getBaseRace().getName());
        refreshSubrace();
        subraceCombo.setSelectedItem(selectedSubrace.getName());

        ProfessionCareer selectedCareer = (ProfessionCareer) Dice.randomItem(compatibleCareers.get(selectedSubrace));
        Profession selectedProfession = (Profession) Dice.randomItem(selectedCareer.getProfessions());

        classCombo.setSelectedItem(selectedCareer.getProfessionClass().getName());
        refreshCareer();
        careerCombo.setSelectedItem(selectedCareer.getName());
        refreshProfession();
        professionCombo.setSelectedItem(selectedProfession.getName());
    }
    private void logConstraints() {
        LogManager.getLogger(getClass().getName()).info(String.format("Constraints: race = %s", race));
        LogManager.getLogger(getClass().getName()).info(String.format("Constraints: subrace = %s", subrace));
        LogManager.getLogger(getClass().getName()).info(String.format("Constraints: class = %s", professionClass));
        LogManager.getLogger(getClass().getName()).info(String.format("Constraints: career = %s", professionCareer));
        LogManager.getLogger(getClass().getName()).info(String.format("Constraints: profession = %s\n", profession));
    }
}

class InvalidParametersException extends Exception {
    public InvalidParametersException() {
        super();
    }
    public InvalidParametersException(String message) {
        super(message);
    }
}
