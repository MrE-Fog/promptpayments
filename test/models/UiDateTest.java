package models;

import org.junit.Test;
import utils.MockUtcTimeProvider;

import java.security.InvalidParameterException;
import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 06/10/2016.
 *
 * Tests for UiDate
 */
public class UiDateTest {

    @Test
    public void constructor_throwsIfLocalCalendar() throws Exception {
        try {
            new UiDate(new GregorianCalendar());
        } catch (InvalidParameterException e) {
            return;
        }

        fail("Shouldn't accept local calendar");
    }

    @Test
    public void constructor_succeedsIfUtcCalendar() throws Exception {
        new UiDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
    }

    @Test
    public void toFriendlyString() throws Exception {
        UiDate uiDate = new UiDate(new MockUtcTimeProvider(2016, 10, 30, 20).Now());
        assertEquals("November 2016", uiDate.ToFriendlyString());
    }

    @Test
    public void toFriendlyString_ConvertsToLocal() throws Exception {
        class TamperedUi extends UiDate {
            private TamperedUi(Calendar calendar) {
                super(calendar);
            }

            @Override
            protected TimeZone getLocalTimezone() {
                return TimeZone.getTimeZone("Asia/Calcutta");
            }
        }


        UiDate uiDate = new TamperedUi(new MockUtcTimeProvider(2016, 10, 30, 20).Now());
        assertEquals("December 2016", uiDate.ToFriendlyString());
    }

}