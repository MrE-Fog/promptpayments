package models;

import org.junit.Test;
import utils.ReflectiveObjectTester;
import utils.ReportModelExamples;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * Tests for ReportFilingModel
 */
public class ReportFilingModelTest {

    @Test
    public void computedGetters() throws Exception {
        ReportFilingModel rfm = ReportModelExamples.makeFullReportFilingModel("122");

        assertEquals(new BigDecimal("31.00"), rfm.getAverageTimeToPayAsDecimal());
        assertEquals(new BigDecimal("10.00"), rfm.getPercentInvoicesPaidBeyondAgreedTermsAsDecimal());
        assertEquals(new BigDecimal("80.00"), rfm.getPercentInvoicesWithin30DaysAsDecimal());
        assertEquals(new BigDecimal("15.00"), rfm.getPercentInvoicesWithin60DaysAsDecimal());
        assertEquals(new BigDecimal("5.00"), rfm.getPercentInvoicesBeyond60DaysAsDecimal());
        assertEquals("1 January 2016", new UiDate(rfm.getStartDate()).ToDateString());
        assertEquals("30 June 2016", new UiDate(rfm.getEndDate()).ToDateString());

        assertEquals("1 January 2016", rfm.getStartDateString());
        assertEquals("30 June 2016", rfm.getEndDateString());

    }

    @Test
    public void gettersAndSetters() throws Exception {
        ReportFilingModel one = ReportModelExamples.makeFullReportFilingModel("122");
        ReportFilingModel two = ReportModelExamples.makeDifferentFullReportFilingModel("123");

        ReflectiveObjectTester.assertSetAndGetAllFields(one, two, 19);
    }

    @Test
    public void ormConstructor() throws Exception {
        new ReportFilingModel();
        assertNotEquals(null, ReportFilingModel.class.getDeclaredConstructor().getAnnotation(Deprecated.class));
    }
}

