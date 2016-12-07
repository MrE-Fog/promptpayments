package utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtcConverter {

    private static Map<String, Integer> monthMap = null;

    public static Calendar tryMakeUtcDate(String year, String month, String day) {
        if (year == null || year.isEmpty()) return null;
        if (month == null || month.isEmpty()) return null;
        if (day == null || day.isEmpty()) return null;
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setLenient(false);
            calendar.set(IntegerConverter.tryConvertInt(year), getMonthInt(month), IntegerConverter.tryConvertInt(day), 0, 0, 0);
            calendar.set(Calendar.MILLISECOND,0);
            calendar.getTime(); // this throws if the year-month-day combination is invalid.
            return calendar;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static int getMonthInt(String month) {
        String sanitisedMonth = month.trim().toLowerCase();

        if(monthMap == null) initialiseMonthMap();
        if(monthMap.containsKey(sanitisedMonth)) {
            return monthMap.get(sanitisedMonth);
        } else {
            return IntegerConverter.tryConvertInt(sanitisedMonth) - 1;
        }
    }

    private static void initialiseMonthMap() {
        monthMap = new HashMap<>();
        monthMap.put("january", 0);
        monthMap.put("jan", 0);
        monthMap.put("february", 1);
        monthMap.put("feb", 1);
        monthMap.put("march", 2);
        monthMap.put("mar", 2);
        monthMap.put("april", 3);
        monthMap.put("apr", 3);
        monthMap.put("may", 4);
        monthMap.put("june", 5);
        monthMap.put("jun", 5);
        monthMap.put("july", 6);
        monthMap.put("jul", 6);
        monthMap.put("august", 7);
        monthMap.put("aug", 7);
        monthMap.put("september", 8);
        monthMap.put("sep", 8);
        monthMap.put("sept", 8);
        monthMap.put("october", 9);
        monthMap.put("oct", 9);
        monthMap.put("november", 10);
        monthMap.put("nov", 10);
        monthMap.put("december", 11);
        monthMap.put("dec", 11);
    }
}
