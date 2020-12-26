import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LanguagePack {
    int language_code;
    Map<String, String[]> map;

    public int getCode() {
        return language_code;
    }
    public void setCode(int language_code) {
        this.language_code = language_code;
    }

    public LanguagePack(int _language_code) {
        language_code = _language_code;
        map = new HashMap<>();
        Path pathToFile = Paths.get("./src/main/resources/languagepack.csv");
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)) {
            String line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(";;");
                String index = attributes[0];
                attributes = Arrays.copyOfRange(attributes, 1, attributes.length);

                map.put(index, attributes);
                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.print("Language Pack not loaded.");
        }
    }

    public String localise(String code) {
        try {
            String[] record = map.get(code);
            return record[language_code];
        } catch (Exception ex) {
            System.out.print("Invalid code");
            return code;
        }
    }
}