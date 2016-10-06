package utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by daniel.rothig on 05/10/2016.
 *
 */
public class UtcTimeProvider implements TimeProvider{
    @Override
    public Calendar Now() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }
}
