package utils;

import models.UiDate;
import org.junit.Test;

import java.time.Month;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;


public class UtcConverterTest {
    @Test
    public void tryMakeUtcDate1() throws Exception {
        assertEquals("30 June 2016", new UiDate(UtcConverter.tryMakeUtcDate("2016", "6", "30")).ToDateString());
    }

    @Test
    public void tryMakeUtcDate() throws Exception {
        assertDate("2016", "1", "1", 2016, Calendar.JANUARY, 1);
        assertDate("2016", "1", "1st", 2016, Calendar.JANUARY, 1);
        assertDate("2016", "1", "\t1st ", 2016, Calendar.JANUARY, 1);
        assertDate("2016", "jAn ", "1", 2016, Calendar.JANUARY, 1);
        assertDate("2016", "  January ", "1", 2016, Calendar.JANUARY, 1);
        assertDate(" 2016", "1", "1", 2016, Calendar.JANUARY, 1);
        assertDate("2016 ", "1", "1", 2016, Calendar.JANUARY, 1);
        assertDate("2016", " 1", "1", 2016, Calendar.JANUARY, 1);
        assertDate("2016", "1 ", "1", 2016, Calendar.JANUARY, 1);
        assertDate("2016", "1", "1 ", 2016, Calendar.JANUARY, 1);
        assertDate("2016", "1", " 1", 2016, Calendar.JANUARY, 1);
    }

    private void assertDate(String year, String month, String day, int expectedYear, int expectedMonth, int expectedDay) {
        Calendar calendar = UtcConverter.tryMakeUtcDate(year, month, day);

        assertEquals(TimeZone.getTimeZone("UTC"), calendar.getTimeZone());
        assertEquals(expectedYear, calendar.get(Calendar.YEAR));
        assertEquals(expectedMonth, calendar.get(Calendar.MONTH));
        assertEquals(expectedDay, calendar.get(Calendar.DATE));
    }

    @Test
    public void invalidDates() throws Exception {
        assertNull(UtcConverter.tryMakeUtcDate("2016", "2", "30"));
        assertNull(UtcConverter.tryMakeUtcDate("-1", "2", "30"));
        assertNull(UtcConverter.tryMakeUtcDate("", "1", "1"));
        assertNull(UtcConverter.tryMakeUtcDate("2016", "", "1"));
        assertNull(UtcConverter.tryMakeUtcDate("2016", "1", ""));
        assertNull(UtcConverter.tryMakeUtcDate(null, "1", "1"));
        assertNull(UtcConverter.tryMakeUtcDate("2016", null, "1"));
        assertNull(UtcConverter.tryMakeUtcDate("2016", "1", null));
        assertNull(UtcConverter.tryMakeUtcDate("2016", "13", "1"));
        assertNull(UtcConverter.tryMakeUtcDate("2016", "0", "1"));
        assertNull(UtcConverter.tryMakeUtcDate("2016", "1", "0"));
        assertNull(UtcConverter.tryMakeUtcDate("2016", "1", "32"));
        assertNull(UtcConverter.tryMakeUtcDate("Foo", "1", "1"));
        assertNull(UtcConverter.tryMakeUtcDate("2016", "1", "Monday"));
    }

    @Test
    public void constructor() throws Exception {
        new UtcConverter();
    }

}