package main;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import mappings.Attribute;
import mappings.Profession;
import mappings.ProfessionCareer;
import mappings.ProfessionClass;
import mappings.Race;
import mappings.Race.Size;
import mappings.SkillSingle;
import mappings.Subrace;
import mappings.TalentSingle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CharacterSheet {
    public JPanel mainPanel;
    public Connection connection;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final int MOVE = 11;

    private int exp;
    private int freeExp;
    private boolean player;
    private int healthPoints;
    private Subrace subrace;
    private Profession profession;
    private Map<Integer, Attribute> attributes = new ConcurrentHashMap<>();
    private final Map<Integer, SkillSingle> skills = new ConcurrentHashMap<>();
    private final Map<Integer, TalentSingle> talents = new ConcurrentHashMap<>();

    private List<DateEntry> history = new ArrayList<>();

    public CharacterSheet(Connection connection) {
        this.connection = connection;
    }

    public Race getRace() {
        return subrace.getBaseRace();
    }
    public Subrace getSubrace() {
        return subrace;
    }
    public void setSubrace(Subrace subrace) {
        this.subrace = subrace;
    }

    public Size getSize() {
        return subrace.getBaseRace().getSize();
    }

    public Profession getProfession() {
        return profession;
    }
    public ProfessionCareer getProfessionCareer() {
        return profession.getProfessionCareer();
    }
    public ProfessionClass getProfessionClass() {
        return profession.getProfessionCareer().getProfessionClass();
    }
    public void setProfession(Profession prof) {
        this.profession = prof;
    }

    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }
    public Attribute getAttribute(int index) {
        return attributes.get(index);
    }
    public void setAttributes(Map<Integer, Attribute> attributes) {
        this.attributes = attributes;
    }

    public void addSkill(SkillSingle skill) {
        skills.put(skill.getID(), skill);
    }
    public Map<Integer, SkillSingle> getSkills() {
        return skills;
    }

    public void addTalent(TalentSingle talent) {
        talents.put(talent.getID(), talent);
    }
    public Map<Integer, TalentSingle> getTalents() {
        return talents;
    }

    public boolean isPlayer() {
        return player;
    }
    public void setPlayer(boolean player) {
        this.player = player;
    }

    public int getExp() {
        return exp;
    }
    public int getFreeExp() {
        return freeExp;
    }
    public void setFreeExp(int freeExp) {
        this.freeExp = freeExp;
    }
    public int getUsedExp() {
        int usedExp = 0;
        for (Attribute attribute : attributes.values()) {
            usedExp += calculateExp(attribute.getAdvValue(), 25);
        }
        for (SkillSingle skill : skills.values()) {
            usedExp += calculateExp(skill.getAdvValue(), 10);
        }
        for (TalentSingle talent : talents.values()) {
            usedExp += talent.getCurrentLvl() * talent.getCurrentLvl() * 50;
        }
        return usedExp - freeExp;
    }
    public int calculateExp(int advances, int baseCost) {
        if (advances==0) {
            return 0;
        }
        return baseCost + 5*((advances-1)/5) + calculateExp(advances-1, baseCost);
    }
    public void setExp(int exp) {
        pcs.firePropertyChange("exp", this.exp, exp);
        this.exp = exp;
    }
    public void addExp(int exp) {
        setExp(this.exp + exp);
    }

    public int getMaxHealthPoints() {
        if (!attributes.containsKey(3) || !attributes.containsKey(4) || !attributes.containsKey(9)) {
            return 0;
        }

        int value = (attributes.get(4).getTotalValue() / 10) * 2 + attributes.get(9).getTotalValue() / 10;
        if (subrace.getBaseRace().getSize() == Race.Size.NORMAL) {
            value += attributes.get(3).getTotalValue() / 10;
        }
        return value;
    }
    public int getHealthPoints() {
        return healthPoints;
    }
    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }
    public void resetHealthPoints() {
        this.healthPoints = getMaxHealthPoints();
    }

    public List<DateEntry> getHistory() {
        return history;
    }
    public void setHistory(List<DateEntry> history) {
        this.history = history;
    }
    public void addObserver(String propertyName, PropertyChangeListener l) {
        pcs.addPropertyChangeListener(propertyName, l);
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("exp", exp);
        jsonObject.put("free_exp", getUsedExp());
        jsonObject.put("player", true);

        JSONObject subraceObject = new JSONObject();
        subraceObject.put("ID", subrace.getID());
        subraceObject.put("race_ID", subrace.getBaseRace().getID());
        subraceObject.put("name", subrace.getName());
        jsonObject.put("subrace", subraceObject);

        JSONObject professionObject = new JSONObject();
        professionObject.put("ID", profession.getID());
        professionObject.put("name", profession.getName());
        jsonObject.put("profession", professionObject);

        JSONArray historyArray = new JSONArray();
        JSONObject entryJSON = new JSONObject();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        entryJSON.put("date", formatter.format(new Date(System.currentTimeMillis())));

        // Attributes
        JSONArray attributesArray = new JSONArray();
        for (Attribute attribute : attributes.values()) {
            JSONObject attributeJSON = new JSONObject();
            attributeJSON.put("ID", attribute.getID());
            attributeJSON.put("name", attribute.getName());
            attributeJSON.put("base_value", attribute.getBaseValue());
            attributeJSON.put("rnd_value", attribute.getRndValue());
            attributeJSON.put("adv_value", attribute.getAdvValue());
            attributesArray.put(attributeJSON);
        }
        entryJSON.put("attributes", attributesArray);

        // Skills
        JSONArray skillsArray = new JSONArray();
        for (SkillSingle skill : skills.values()) {
            if (skill.getAdvValue() > 0 || skill.isAdvanceable()) {
                JSONObject skillJSON = new JSONObject();
                skillJSON.put("ID", skill.getID());
                skillJSON.put("base_skill_ID", skill.getBaseSkill().getID());
                skillJSON.put("name", skill.getName());
                skillJSON.put("earning", skill.isEarning());
                skillJSON.put("advanceable", skill.isAdvanceable());
                skillJSON.put("adv_value", skill.getAdvValue());
                skillsArray.put(skillJSON);
            }
        }
        entryJSON.put("skills", skillsArray);

        // Talents
        JSONArray talentsArray = new JSONArray();
        for (TalentSingle talent : talents.values()) {
            JSONObject talentJSON = new JSONObject();
            talentJSON.put("ID", talent.getID());
            talentJSON.put("name", talent.getName());
            talentJSON.put("lvl", talent.getCurrentLvl());
            talentJSON.put("advanceable", talent.isAdvanceable());
            talentsArray.put(talentJSON);
        }
        entryJSON.put("talents", talentsArray);

        historyArray.put(entryJSON);
        jsonObject.put("history", historyArray);

        return jsonObject;
    }
    public void printJSON() {
        System.out.println(toJSON().toString(4));
    }
    public void saveJSON(String filename) {
        try (PrintWriter file = new PrintWriter(Paths.get(filename).toFile())) {
            file.println(toJSON().toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadJSON(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject jsonObject = new JSONObject(content);

            player = jsonObject.getBoolean("player");
            exp = jsonObject.getInt("exp");
            freeExp = jsonObject.getInt("free_exp");

            JSONObject subraceObject = jsonObject.getJSONObject("subrace");
            int ID = subraceObject.getInt("ID");
            String name = subraceObject.getString("name");
            subrace = connection.getSubrace(ID, name);
            if (subrace == null)
                throw new JSONException(String.format("Cannot load subrace: %s", name));

            JSONObject professionObject = jsonObject.getJSONObject("profession");
            ID = professionObject.getInt("ID");
            name = professionObject.getString("name");
            profession = connection.getProfession(ID, name);
            if (profession == null)
                throw new JSONException(String.format("Cannot load profession: %s", name));

            JSONArray historyArray = jsonObject.getJSONArray("history");
            for (int i = 0; i < historyArray.length(); i++) {
                JSONObject entryJSON = historyArray.getJSONObject(i);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date date = formatter.parse(entryJSON.getString("date"));
                DateEntry entry = new DateEntry(date);

                JSONArray attributesArray = entryJSON.getJSONArray("attributes");
                for (int j = 0; j < attributesArray.length(); j++) {
                    JSONObject object = attributesArray.getJSONObject(j);
                    ID = object.getInt("ID");
                    name = object.getString("name");
                    Attribute attribute = connection.getAttribute(ID, name);
                    if (attribute == null) {
                        throw new JSONException(String.format("Cannot load attribute: %s", name));
                    }
                    attribute.setBaseValue(object.getInt("base_value"));
                    attribute.setRndValue(object.getInt("rnd_value"));
                    attribute.setAdvValue(object.getInt("adv_value"));

                    attributes.put(ID, attribute);
                    if (attribute.getAdvValue() > 0) {
                        entry.add(attribute, 0, attribute.getAdvValue());
                    }
                }

                JSONArray skillsArray = entryJSON.getJSONArray("skills");
                for (int j = 0; j < skillsArray.length(); j++) {
                    JSONObject object = skillsArray.getJSONObject(j);
                    ID = object.getInt("ID");
                    name = object.getString("name");
                    SkillSingle skill = connection.getSkill(ID, name);
                    if (skill == null) {
                        throw new JSONException(String.format("Cannot load skill: %s", name));
                    }
                    skill.setAdvValue(object.getInt("adv_value"));
                    skill.setAdvanceable(object.getBoolean("advanceable"));
                    skill.setEarning(object.getBoolean("earning"));

                    skills.put(ID, skill);
                    if (skill.getAdvValue() > 0) {
                        entry.add(skill, 0, skill.getAdvValue());
                    }
                }

                JSONArray talentsArray = entryJSON.getJSONArray("talents");
                for (int j = 0; j < talentsArray.length(); j++) {
                    JSONObject object = talentsArray.getJSONObject(j);
                    ID = object.getInt("ID");
                    name = object.getString("name");
                    TalentSingle talent = connection.getTalent(ID, name);
                    if (talent == null) {
                        throw new JSONException(String.format("Cannot load talent: %s", name));
                    }
                    talent.setCurrentLvl(object.getInt("lvl"));
                    talent.setAdvanceable(object.getBoolean("advanceable"));

                    talents.put(ID, talent);
                    if (talent.getCurrentLvl() > 0) {
                        entry.add(talent, 0, talent.getCurrentLvl());
                    }
                }
                history.add(entry);
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String toHTML() throws IOException {
        String html = new String(Files.readAllBytes(Paths.get("src/resources/out&in/statblock_template.html")));
        html = fillHTML(html);
        return html;
    }
    public ByteArrayOutputStream toPDF(PageSize size) throws IOException {
        return convertHTMLToPDF(toHTML(), size);
    }
    public ByteArrayOutputStream convertHTMLToPDF(String html, PageSize size) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(size);
        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider(true, true, true);

        try {
            FontProgram fontProgram = FontProgramFactory.createFont("src/resources/Crimson Text-Regular.ttf");
            fontProvider.addFont(fontProgram);
            fontProgram = FontProgramFactory.createFont("src/resources/Crimson Text-Bold.ttf");
            fontProvider.addFont(fontProgram);
            properties.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(html, pdf, properties);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return outputStream;
    }

    public void savePDF(String filename) {
        try {
            ByteArrayOutputStream outputStream = convertHTMLToPDF(toHTML(), new PageSize(500, 250));
            outputStream.writeTo(new FileOutputStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveImage(String filename, PageSize size) {
        try {
            PDDocument document = PDDocument.load(new ByteArrayInputStream(toPDF(size).toByteArray()));
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 300);
            ImageIO.write(image, "JPEG", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveImage(String filename) {
        try {
            PDDocument document = PDDocument.load(new ByteArrayInputStream(toPDF(new PageSize(500, 250)).toByteArray()));
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 300);
            ImageIO.write(image, "JPEG", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String fillHTML(String template) {
        for (Map.Entry<Integer, Attribute> entry : attributes.entrySet()) {
            template = template.replace(String.format("$(ATTR_%d)", entry.getKey()), String.valueOf(entry.getValue().getTotalValue()));
        }
        template = template.replace("$(HP)", String.valueOf(getMaxHealthPoints()));
        return template;
    }
}
