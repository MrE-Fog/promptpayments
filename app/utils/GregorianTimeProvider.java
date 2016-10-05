package utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by daniel.rothig on 05/10/2016.
 *
 */
public class GregorianTimeProvider implements TimeProvider{
    @Override
    public Calendar Now() {
        return new GregorianCalendar();
    }
}
