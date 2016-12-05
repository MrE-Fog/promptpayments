package models;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 28/11/2016.
 */
public class CalculatorModelTest {

    @Test
    public void getReportingPeriods() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2020", "1", "1", "2020", "12", "31").getReportingPeriods();

        assertEquals(2, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "1 January 2020", "30 June 2020", "30 July 2020");
        assertPeriod(reportingPeriods.get(1), "1 July 2020", "31 December 2020", "31 January 2021");

    }

    @Test
    public void getReportingPeriods_preCutoff() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2017", "1", "1", "2017", "12", "31").getReportingPeriods();

        assertEquals(0, reportingPeriods.size());
    }

    @Test
    public void getReportingPeriods_AndFebruary() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2017", "9", "1", "2018", "8", "31").getReportingPeriods();

        assertEquals(2, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "1 September 2017", "28 February 2018", "28 March 2018");
        assertPeriod(reportingPeriods.get(1), "1 March 2018", "31 August 2018", "30 September 2018");
    }

    @Test
    public void getReportingPeriods_9MonthYear() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2018", "1", "1", "2018", "9", "30").getReportingPeriods();

        assertEquals(1, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "1 January 2018", "30 September 2018", "30 October 2018");
    }

    @Test
    public void getReportingPeriods_15MonthYear() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2018", "1", "1", "2019", "3", "31").getReportingPeriods();

        assertEquals(2, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "1 January 2018", "30 June 2018", "30 July 2018");
        assertPeriod(reportingPeriods.get(1), "1 July 2018", "31 March 2019", "30 April 2019");
    }

    @Test
    public void getReportingPeriods_15MonthYearPlus() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2018", "1", "1", "2019", "4", "1").getReportingPeriods();

        assertEquals(3, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "1 January 2018", "30 June 2018", "30 July 2018");
        assertPeriod(reportingPeriods.get(1), "1 July 2018", "31 December 2018", "31 January 2019");
        assertPeriod(reportingPeriods.get(2), "1 January 2019", "1 April 2019", "1 May 2019");
    }

    @Test
    public void getReportingPeriods_19MonthYear() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2018", "1", "1", "2019", "9", "30").getReportingPeriods();

        assertEquals(3, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "1 January 2018", "30 June 2018", "30 July 2018");
        assertPeriod(reportingPeriods.get(1), "1 July 2018", "31 December 2018", "31 January 2019");
        assertPeriod(reportingPeriods.get(2), "1 January 2019", "30 September 2019", "30 October 2019");
    }

    @Test
    public void getReportingPeriods_19MonthYearPlus() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2018", "1", "1", "2019", "10", "1").getReportingPeriods();

        assertEquals(3, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "1 January 2018", "30 June 2018", "30 July 2018");
        assertPeriod(reportingPeriods.get(1), "1 July 2018", "31 December 2018", "31 January 2019");
        assertPeriod(reportingPeriods.get(2), "1 January 2019", "1 October 2019", "1 November 2019");
    }

    @Test
    public void getReportingPeriods_StartingMarch() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2018", "3", "1", "2019", "2", "28").getReportingPeriods();

        assertEquals(2, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "1 March 2018", "31 August 2018", "30 September 2018");
        assertPeriod(reportingPeriods.get(1), "1 September 2018", "28 February 2019", "28 March 2019");
    }

    @Test
    public void getReportingPeriods_24MonthYear_StartingEndOfMonthHittingEndOfFebruary() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2017", "8", "31", "2019", "8", "30").getReportingPeriods();

        assertEquals(3, reportingPeriods.size());

        assertPeriod(reportingPeriods.get(0), "31 August 2017", "27 February 2018", "27 March 2018");
        assertPeriod(reportingPeriods.get(1), "28 February 2018", "30 August 2018", "30 September 2018");
        assertPeriod(reportingPeriods.get(2), "31 August 2018", "30 August 2019", "30 September 2019");
    }

    @Test
    public void emptyConstructor() throws Exception {
        CalculatorModel model = new CalculatorModel();

        assertTrue(model.isEmpty());
        assertTrue(model.isValid());
    }

    @Test
    public void getReportingPeriods_forAges() throws Exception {
        List<CalculatorModel.ReportingPeriod> reportingPeriods = new CalculatorModel("2018", "1", "1", "3018", "1", "1").getReportingPeriods();
        assertEquals(0, reportingPeriods.size());
    }

    @Test
    public void getReportingPeriods_forBadDates() throws Exception {
        assertEquals(0, new CalculatorModel("2018", "1", "1", "2018", "1", "1").getReportingPeriods().size());
        assertEquals(0, new CalculatorModel("2018", "1", "1", "2017", "1", "1").getReportingPeriods().size());
        assertEquals(0, new CalculatorModel("2018", "2", "40", "2019", "1", "1").getReportingPeriods().size());
    }

    private void assertPeriod(CalculatorModel.ReportingPeriod p, String expectedStart, String expectedEnd, String expectedDeadline) {
        assertEquals("Start date", expectedStart, p.StartDate.ToDateString());
        assertEquals("End date", expectedEnd, p.EndDate.ToDateString());
        assertEquals("Deadline", expectedDeadline, p.FilingDeadline.ToDateString());
    }

}