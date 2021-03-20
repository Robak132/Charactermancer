package tools;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public abstract class MultiLineTooltip {
    private static final int DIALOG_TOOLTIP_MAX_SIZE = 75;

    private MultiLineTooltip() {
    }

    public static String splitToolTip(final String toolTip) {
        return splitToolTip(toolTip, DIALOG_TOOLTIP_MAX_SIZE);
    }
    public static String splitToolTip(final String toolTip, Font font) {
        return splitToolTip(toolTip, DIALOG_TOOLTIP_MAX_SIZE, 0, font);
    }
    public static String splitToolTip(final String toolTip, int desiredLength) {
        return splitToolTip(toolTip, desiredLength, 0);
    }
    public static String splitToolTip(final String toolTip, int desiredLength, int threshold) {
        return splitToolTip(toolTip, desiredLength, threshold, new Font("Verdana", Font.PLAIN, 10));

    }
    public static String splitToolTip(final String toolTip, int desiredLength, int threshold, Font font) {
        List<String> words = new ArrayList<>();
        List<int[]> lengths = new ArrayList<>();
        String[] splitWords = toolTip.split(" ", -1);
        int j=0;
        while (j < splitWords.length) {
            String word = splitWords[j];
            while (j < splitWords.length - 1 && splitWords[j].length() == 1) {
                word += " " + splitWords[j+1];
                j++;
            }
            words.add(word);
            if (font == null) {
                lengths.add(new int[] {word.length()});
//                System.out.printf("%s: %d\n", word, word.length());
            } else {
                lengths.add(new int[] {word.length(), getRealLength(word, font)});
//                System.out.printf("%s: %d, %d\n", word, word.length(), getRealLength(word, font));
            }
            j++;
        }

        List<String> parts = null;
        double minValue = Double.POSITIVE_INFINITY;
        for (int i = desiredLength - threshold; i <= desiredLength + threshold; i++) {
            Object[] result = getParts(i, font, words, lengths);
            List<String> activeParts = (List<String>) result[0];
            double diagnosticValue = Toolbox.varFloat((List<Float>) result[1]);
            diagnosticValue += Math.abs(i-desiredLength);

            if (parts == null) {
                parts = activeParts;
            }

            if (diagnosticValue < minValue) {
                minValue = diagnosticValue;
                parts = activeParts;
            }
//            System.out.printf("%d: %f\n", i, diagnosticValue);
        }


        StringBuilder  sb = new StringBuilder("<html><p><font face=\\\"Verdana\\\">");
        for (int i = 0; i < parts.size() - 1; i++) {
            sb.append(parts.get(i)).append("<br>");
        }
        sb.append(parts.get(parts.size() - 1));
        sb.append(("</font></p></html>"));
        return sb.toString();
    }

    private static Object[] getParts(int desiredLength, Font font, List<String> words, List<int[]> lengths) {
        //IDEA: Maybe use DynamicMatrix2D
        List<String> parts = new ArrayList<>();
        List<Float> diagnostics = new ArrayList<>();

        int lineLenghtMultiplier = 1;
        int index = 0;
        if (font != null) {
            lineLenghtMultiplier = getRealLength("a", font);
            index = 1;
        }

        float activeLineLength = desiredLength * lineLenghtMultiplier;
        String line = "";
        for (int i = 0; i < words.size(); i++) {
            if (activeLineLength - lengths.get(i)[index] >= -0.04 * desiredLength * lineLenghtMultiplier) {
                activeLineLength -= lengths.get(i)[index];
                line += words.get(i) + " ";
            }
            else {
                System.out.printf("%s\t\t%f (%d)\n", line.strip(), activeLineLength, -lengths.get(i)[index]);
                activeLineLength = desiredLength * lineLenghtMultiplier;
                diagnostics.add(activeLineLength);
                parts.add(line.strip());
                line = words.get(i) + " ";
            }
        }
        parts.add(line.strip());
        System.out.printf("%s\n\n", line.strip());

        Object[] returns = new Object[2];
        returns[0] = parts;
        returns[1] = diagnostics;

        return returns;
    }

    public static int optimise(String toolTip, int desiredLength, int threshold, Font font) {
        float min = Float.POSITIVE_INFINITY;
        int newDesiredLength = desiredLength;

        for (int i=desiredLength+threshold; i>desiredLength-threshold; i--) {
            int rowLength = getRealLength("a", font) * i;
            List<Integer> errorArray = new ArrayList<>();

            for (String word : toolTip.split(" ")) {
                if (rowLength - getRealLength(word + " ", font) >= 0) {
                    rowLength -= getRealLength(word + " ", font);
                }
                else {
                    errorArray.add(rowLength);
                    rowLength = getRealLength("a", font) * i;
                }
            }
            float error = Toolbox.avg(errorArray);
            error *= error;
            error += Math.abs(desiredLength-i);

            if (min > error) {
                min = error;
                newDesiredLength = i;
            }

            System.out.printf("%d - %f\n", i, error);
        }
        return newDesiredLength;
    }

    private static int getRealLength(String text, Font font) {
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
        return (int)(font.getStringBounds(text, frc).getWidth());
    }
}