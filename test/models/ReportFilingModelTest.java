package models;

import org.junit.Test;
import utils.ReportModelExamples;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
        assertEquals(new BigDecimal( "5.00"), rfm.getPercentInvoicesBeyond60DaysAsDecimal());
        assertEquals("1 January 2016", new UiDate(rfm.getStartDate()).ToDateString());
        assertEquals("30 June 2016", new UiDate(rfm.getEndDate()).ToDateString());

        assertEquals("1 January 2016", rfm.getStartDateString());
        assertEquals("30 June 2016", rfm.getEndDateString());

    }

    @Test
    public void gettersAndSetters() throws Exception {
        ReportFilingModel one = ReportModelExamples.makeFullReportFilingModel("122");
        ReportFilingModel two = ReportModelExamples.makeDifferentFullReportFilingModel("123");

        for (Field field: ReportFilingModel.class.getDeclaredFields()) {

            Method getter;
            try {
                getter = ReportFilingModel.class.getMethod("get"+field.getName());
            } catch (NoSuchMethodException e){
                getter = ReportFilingModel.class.getMethod("is"+field.getName());
            }

            Method setter = ReportFilingModel.class.getMethod("set"+field.getName(), field.getType());

            assertNotEquals(getter.invoke(one), getter.invoke(two));

            setter.invoke(one, getter.invoke(two));
            assertEquals(getter.invoke(one), getter.invoke(two));
        }
    }
}