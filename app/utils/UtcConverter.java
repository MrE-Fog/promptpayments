package utils;

import java.util.Calendar;
import java.util.TimeZone;

public class UtcConverter {
    public static Calendar tryMakeUtcDate(String year, String month, String day) {
        if (year == null || year.isEmpty()) return null;
        if (month == null || month.isEmpty()) return null;
        if (day == null || day.isEmpty()) return null;
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setLenient(false);
            calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), 0, 0, 0);
            calendar.getTime(); // this throws if the year-month-day combination is invalid.
            return calendar;
        } catch (Exception ignored) {
            return null;
        }
    }
}
