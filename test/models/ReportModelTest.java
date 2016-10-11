package models;

import org.junit.Test;
import utils.MockUtcTimeProvider;
import utils.ReflectiveObjectTester;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * Tests for ReportModel
 */
public class ReportModelTest {

    private Calendar filingDate  = new MockUtcTimeProvider(2016, 6, 1).Now();
    private Calendar start = new MockUtcTimeProvider(2016, 0, 1).Now();
    private Calendar end = new MockUtcTimeProvider(2016, 5, 30).Now();

    @Test
    public void computedGetters() throws Exception {
        ReportModel reportModel = getReportModel(filingDate, start, end);

        assertEquals("1 January 2016", reportModel.getStartDateString());
        assertEquals("30 June 2016", reportModel.getEndDateString());
    }

    @Test
    public void throwsWhenNonUtc_1() throws Exception {
        try {
            Calendar badFilingDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            badFilingDate.set(2016,6,1);
            getReportModel(badFilingDate, start, end);
        } catch (InvalidParameterException e) {
            return;
        }

        fail();
    }

    @Test
    public void throwsWhenNonUtc_2() throws Exception {
        try {
            Calendar badStartDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            badStartDate.set(2016,0,1);
            getReportModel(filingDate, badStartDate, end);
        } catch (InvalidParameterException e) {
            return;
        }

        fail();
    }

    @Test
    public void throwsWhenNonUtc_3() throws Exception {
        try {
            Calendar badEndDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            badEndDate.set(2016,5,30);
            getReportModel(filingDate, start, badEndDate);
        } catch (InvalidParameterException e) {
            return;
        }

        fail();
    }

    @Test
    public void isGoodImmutablePoco() throws Exception {
        ReflectiveObjectTester.assertGoodImmutablePoco(ReportModel.class);
    }

    private static ReportModel getReportModel(Calendar filingDate, Calendar start, Calendar end) {
        return new ReportModel(
                new ReportSummary(1, filingDate),
                null, null, null, null, null,
                start,
                end,
                null, null, false, false, false, false, null);
    }
}
