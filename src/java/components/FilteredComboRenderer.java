package components;

import java.awt.Color;
import java.awt.Component;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class FilteredComboRenderer<T> extends DefaultListCellRenderer {
    private static final String HighLightTemplate = "<span style='background:yellow;'>$1</span>";
    private final JLabel searchLabel;
    private final Function<T, String> stringParser;
    private final Function<T, Color> colorParser;

    public FilteredComboRenderer(JLabel filterLabel, Function<T, String> stringParser, Function<T, Color> colorParser) {
        this.searchLabel = filterLabel;
        this.stringParser = stringParser;
        this.colorParser = colorParser;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value == null) {
            return this;
        }
        String text = stringParser.apply((T) value);
        text = highlightText(text, searchLabel.getText());
        this.setText(text);
        if (colorParser != null) {
            this.setForeground(colorParser.apply((T) value));
        }
        return this;
    }
    private String highlightText(String text, String textToHighlight) {
        if(textToHighlight.length()==0){
            return text;
        }

        try {
            text = text.replaceAll("(?i)(" + Pattern.quote(textToHighlight) + ")", HighLightTemplate);
        } catch (Exception e) {
            return text;
        }
        return "<html>" + text + "</html>";
    }
}