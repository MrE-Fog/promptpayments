package utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 11/10/2016.
 *
 * Completionist tests for DatePickerHelper
 */
public class DatePickerHelperTest {

    @Test
    public void test() throws Exception {
        DatePickerHelper datePickerHelper = new DatePickerHelper(new MockUtcTimeProvider(2017, 0, 1));

        assertEquals("2017", datePickerHelper.years.get(0).value);
        assertEquals(4, datePickerHelper.years.size());
        assertEquals("2014", datePickerHelper.years.get(3).value);

        assertEquals("1", datePickerHelper.days.get(0).value);
        assertEquals(31, datePickerHelper.days.size());
        assertEquals("31", datePickerHelper.days.get(30).value);

        assertEquals("0", datePickerHelper.months.get(0).value);
        assertEquals(12, datePickerHelper.months.size());
        assertEquals("11", datePickerHelper.months.get(11).value);
    }
}