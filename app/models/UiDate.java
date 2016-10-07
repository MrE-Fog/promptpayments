package models;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by daniel.rothig on 06/10/2016.
 *
 * Nice UI formatter for dates
 */
public class UiDate {
    private static final DateFormat _friendlyFormatter = new SimpleDateFormat("MMMM yyyy");
    private static final DateFormat _dateFormatter = new SimpleDateFormat("d MMMM yyyy");

    private Calendar inner;

    public UiDate(Calendar calendar) {
        if (!calendar.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
            throw new InvalidParameterException("UiDate requires UTC calendar");
        }
        this.inner = calendar;
    }

    protected TimeZone getLocalTimezone() {
        return TimeZone.getDefault();
    }


    public String ToFriendlyString() {
        Calendar localCalendar = getLocalCalendar();

        return _friendlyFormatter.format(localCalendar.getTime());
    }

    public String ToDateString() {
        Calendar localCalendar = getLocalCalendar();
        return _dateFormatter.format(localCalendar.getTime());
    }

    private Calendar getLocalCalendar() {
        TimeZone localTimezone = getLocalTimezone();
        long newRaw = inner.getTime().getTime() + localTimezone.getRawOffset() - TimeZone.getTimeZone("UTC").getRawOffset();

        Calendar localCalendar = Calendar.getInstance(localTimezone);
        localCalendar.setTimeInMillis(newRaw);
        return localCalendar;
    }
}
