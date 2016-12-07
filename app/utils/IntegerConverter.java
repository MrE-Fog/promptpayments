package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daniel.rothig on 07/12/2016.
 *
 * Turns strings that start as integers into integers.
 */
public class IntegerConverter {
    private static final Pattern pattern = Pattern.compile("^(-?[0-9]+)(\\.0+)?[^0-9]*$");

    /**
     * Turns string that start with integers into Integer objects
     * @param raw a string starting on an integer value
     * @return the converted integer, or null if there is no integer value at the start of the string
     */
    public static Integer tryConvertInt(String raw) {
        if (raw == null) return null;
        Matcher matcher = pattern.matcher(raw.trim());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return null;
        }
    }
}
