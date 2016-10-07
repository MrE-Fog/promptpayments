package utils;

import java.util.Calendar;
import java.util.TimeZone;

public class MockUtcTimeProvider implements TimeProvider {

    private int year;
    private int month;
    private int day;
    private int hour;


    public MockUtcTimeProvider(int year, int month, int day) {
        this(year, month, day, 0);
    }

    public MockUtcTimeProvider(int year, int month, int day, int hour) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
    }

    @Override
    public Calendar Now() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(year, month, day, hour,0,0);
        return cal;
    }
}
