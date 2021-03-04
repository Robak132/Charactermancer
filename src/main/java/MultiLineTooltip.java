import java.util.ArrayList;
import java.util.List;

/** A helper class to split strings into a certain length,
 * formatted with html {@literal<br>} tags for multi-line tool tips.
 * Based on the MultiLineToolTips class posted by
 * <a href="https://stackoverflow.com/users/1480018/paul-taylor">Paul Taylor</a>
 * on <a href="https://stackoverflow.com/a/13503677/9567822">Stack Overflow</a>
 * @author <a href="https://stackoverflow.com/users/9567822/andrew-lemaitre?tab=profile">Andrew LeMaitre</a>
 */
public abstract class MultiLineTooltip {

    /** Private constructor for utility class. */
    private MultiLineTooltip() {
    }

    /** Default max length of the tool tip when split with {@link #splitToolTip(String)}. */
    private static final int DIALOG_TOOLTIP_MAX_SIZE = 75;

    /** A function that splits a string into sections of {@value #DIALOG_TOOLTIP_MAX_SIZE} characters or less.
     * If you want the lines to be shorter or longer call {@link #splitToolTip(String, int)}.
     * @param toolTip The tool tip string to be split
     * @return the tool tip string with HTML formatting to break it into sections of the correct length
     */
    public static String splitToolTip(final String toolTip) {
        return splitToolTip(toolTip, DIALOG_TOOLTIP_MAX_SIZE);
    }

    /**  An overloaded function that splits a tool tip string into sections of a specified length.
     * @param toolTip The tool tip string to be split
     * @param desiredLength The maximum length of the tool tip per line
     * @return The tool tip string with HTML formatting to break it into sections of the correct length
     */
    public static String splitToolTip(final String toolTip, final int desiredLength) {
        if (toolTip.length() <= desiredLength) {
            return toolTip;
        }

        List<String>  parts = new ArrayList<>();
        int stringPosition = 0;

        while (stringPosition < toolTip.length()) {
            if (stringPosition + desiredLength < toolTip.length()) {
                String tipSubstring = toolTip.substring(stringPosition, stringPosition + desiredLength);
                int lastSpace = tipSubstring.lastIndexOf(' ');
                if (lastSpace == -1 || lastSpace == 0) {
                    parts.add(toolTip.substring(stringPosition, stringPosition + desiredLength));
                    stringPosition += desiredLength;
                } else {
                    parts.add(toolTip.substring(stringPosition, stringPosition + lastSpace));
                    stringPosition += lastSpace;
                }
            } else {
                parts.add(toolTip.substring(stringPosition));
                break;
            }
        }

        StringBuilder  sb = new StringBuilder("<html>");
        for (int i = 0; i < parts.size() - 1; i++) {
            sb.append(parts.get(i)).append("<br>");
        }
        sb.append(parts.get(parts.size() - 1));
        sb.append(("</html>"));
        return sb.toString();
    }
}