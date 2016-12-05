package models;

import org.junit.Before;
import org.junit.Test;
import play.libs.F;
import utils.ReportModelExamples;
import utils.UtcConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class ReportFilingModelValidationImplTest {
    private ReportFilingModel model;
    private ReportFilingModelValidationImpl impl;

    @Before
    public void setUp() throws Exception {
        model = ReportModelExamples.makeFullReportFilingModel("123");
        impl = new ReportFilingModelValidationImpl(model, UtcConverter.tryMakeUtcDate("2019","1","1"));
    }

    @Test
    public void constructorThrowsIfNowNotUtc() throws Exception {
        try {
            Calendar badNow = new GregorianCalendar();
            badNow.setTimeZone(TimeZone.getTimeZone("GMT"));

            new ReportFilingModelValidationImpl(model, badNow);
        } catch (InvalidParameterException ignored) {
            return; //ok
        }
        fail("Constructor should throw");
    }

    @Test
    public void isValid() throws Exception {
        assertTrue(impl.isValid());
    }



    @Test
    public <T> void validateAverageTimeToPay() throws Exception {
        testField("AverageTimeToPay", String.class,
                new String[] {"", "blue", "-1", "1.3", "x1", "1x"},
                new String[] {"0", "1", "9999"}, true);
    }



    @Test
    public void validatePercentInvoicesPaidBeyondAgreedTerms() throws Exception {
        testPercentageMetric(true, "PercentInvoicesPaidBeyondAgreedTerms");
    }

    private void testPercentageMetric(boolean globalTest, String fieldName) throws Exception {
        testField(fieldName, String.class,
                new String[] {"", "blue", "-1", "1.3", "x1", "1x", "101"},
                new String[] {"0", "1", "99", "100"}, globalTest);
    }

    @Test
    public void validatePercentInvoicesWithin30Days() throws Exception {
        testPercentageMetric(false, "PercentInvoicesWithin30Days");
    }

    @Test
    public void validatePercentInvoicesWithin60Days() throws Exception {
        testPercentageMetric(false, "PercentInvoicesWithin60Days");
    }

    @Test
    public void validatePercentInvoicesBeyond60Days() throws Exception {
        testPercentageMetric(false, "PercentInvoicesBeyond60Days");
    }

    @Test
    public void validateTimePercentages() throws Exception {
        model.setPercentInvoicesWithin30Days("83");
        assertTrue(impl.validatePercentInvoicesWithin30Days().isOk());
        assertFalse(impl.isValid());
        assertFalse(impl.validateTimePercentages().isOk());

        for (int i = 0; i < 3; i++) {
            String[] values = new String[] {"101", "99", "0"};
            model.setPercentInvoicesWithin30Days(values[i%3]);
            model.setPercentInvoicesWithin60Days(values[(i+1)%3]);
            model.setPercentInvoicesBeyond60Days(values[(i+2)%3]);
            assertTrue(impl.validateTimePercentages().isOk());
        }
    }

    @Test
    public void validateStartDate() throws Exception {
        testDateFields("StartDate", "2012", "1", "1", true);
        testDateFields("StartDate", "2019", "1", "1", true);
        testDateFields("StartDate", "2019", "1", "3", false);
        testDateFields("StartDate", "2012", "2", "31", false);

    }

    @Test
    public void validateEndDate() throws Exception {
        testDateFields("EndDate", "2018", "1", "1", true);
        testDateFields("EndDate", "2019", "1", "1", true);
        testDateFields("EndDate", "2019", "1", "3", false);
        testDateFields("EndDate", "2016", "1", "1", true);
        testDateFields("EndDate", "2015", "12", "31", false);
        testDateFields("EndDate", "2018", "2", "31", false);
    }

    @Test
    public void validatePaymentTerms() throws Exception {
        testRequiredText("PaymentTerms");
    }

    @Test
    public void validateMaximumContractPeriod() throws Exception {
        testRequiredText("MaximumContractPeriod");
    }

    @Test
    public void validatePaymentTermsChanged() throws Exception {
        testRequiredBoolean("PaymentTermsChanged");
    }



    @Test
    public void validatePaymentTermsChangedComment() throws Exception {
        model.setPaymentTermsChanged(true);
        testRequiredText("PaymentTermsChangedComment");

        // not required if PaymentTermsChanged = false
        model.setPaymentTermsChanged(false);
        model.setPaymentTermsChangedComment(null);
        testUnrequiredText("PaymentTermsChangedComment");
    }

    @Test
    public void validatePaymentTermsChangedNotified() throws Exception {
        model.setPaymentTermsChanged(true);
        testRequiredBoolean("PaymentTermsChangedNotified");

        //not required if PaymentTermsChanged =false
        model.setPaymentTermsChanged(false);
        model.setPaymentTermsChangedNotified(null);
        assertTrue(impl.validatePaymentTermsChangedNotified().isOk());
        assertTrue(impl.isValid());
    }

    @Test
    public void validatePaymentTermsChangedNotifiedComment() throws Exception {
        String fieldName = "PaymentTermsChangedNotifiedComment";

        model.setPaymentTermsChanged(true);
        model.setPaymentTermsChangedNotified(true);
        testRequiredText(fieldName);

        // not required if PaymentTermsChangedNotified = false
        model.setPaymentTermsChangedNotified(false);
        testUnrequiredText(fieldName);
        model.setPaymentTermsChangedNotified(true);

        // not required if PaymentTermsChanged = false
        model.setPaymentTermsChanged(false);
        testUnrequiredText(fieldName);
    }

    @Test
    public void validatePaymentTermsComment() throws Exception {
        testUnrequiredText("PaymentTermsComment");
    }

    @Test
    public void validateDisputeResolution() throws Exception {
        testRequiredText("DisputeResolution");
    }

    @Test
    public void validateHasPaymentCodes() throws Exception {
        testRequiredBoolean("HasPaymentCodes");

    }

    @Test
    public void validatePaymentCodes() throws Exception {
        model.setHasPaymentCodes(true);
        testRequiredText("PaymentCodes");

        model.setHasPaymentCodes(false);
        testUnrequiredText("PaymentCodes");
    }

    @Test
    public void validateOfferEInvoicing() throws Exception {
        testRequiredBoolean("OfferEInvoicing");
    }

    @Test
    public void validateOfferSupplyChainFinance() throws Exception {
        testRequiredBoolean("OfferSupplyChainFinance");
    }

    @Test
    public void validateRetentionChargesInPolicy() throws Exception {
        testRequiredBoolean("RetentionChargesInPolicy");
    }

    @Test
    public void validateRetentionChargesInPast() throws Exception {
        testRequiredBoolean("RetentionChargesInPast");
    }

    private <T> void testField(String fieldName, Class<T> clazz, T[] invalidInputs, T[] validInputs, boolean globalValid) throws Exception {
        Method setter = ReportFilingModel.class.getMethod("set" + fieldName, clazz);
        Method validator = ReportFilingModelValidationImpl.class.getMethod("validate" + fieldName);

        for(T x: invalidInputs) {
            setter.invoke(model, x);
            FieldValidation v = (FieldValidation) validator.invoke(impl);

            if (globalValid) assertFalse(String.format("Field %s should not be allowed to be %s", fieldName, x), impl.isValid());
            assertFalse(String.format("Field %s should not be allowed to be %s", fieldName, x), v.isOk());
        }

        for (T x: validInputs) {
            setter.invoke(model, x);
            FieldValidation v = (FieldValidation) validator.invoke(impl);

            if (globalValid) assertTrue(String.format("Field %s should be allowed to be %s", fieldName, x), impl.isValid());
            assertTrue(String.format("Field %s should be allowed to be %s", fieldName, x), v.isOk());
        }
    }

    private <T> void testDateFields(String fieldName, String year, String month, String day, boolean valid) throws Exception {
        Method setter_year = ReportFilingModel.class.getMethod("set" + fieldName +"_year", String.class);
        Method setter_month = ReportFilingModel.class.getMethod("set" + fieldName +"_month", String.class);
        Method setter_day = ReportFilingModel.class.getMethod("set" + fieldName +"_day", String.class);
        Method validator = ReportFilingModelValidationImpl.class.getMethod("validate" + fieldName);

        setter_year.invoke(model, String.valueOf(year));
        setter_month.invoke(model, String.valueOf(month));
        setter_day.invoke(model, String.valueOf(day));

        FieldValidation v = (FieldValidation) validator.invoke(impl);

        assertEquals(valid, v.isOk());
    }


    private void testRequiredText(String fieldName) throws Exception {
        testField(fieldName, String.class,
                new String[] {"", null},
                new String[] {"some required string"},
                true);
    }

    private void testUnrequiredText(String fieldName) throws Exception {
        testField(fieldName, String.class,
                new String[] {},
                new String[] {"", null, "foo"},
                true);
    }
    private void testRequiredBoolean(String fieldName) throws Exception {
        testField(fieldName, Boolean.class,
                new Boolean[] {null},
                new Boolean[] {true, false},
                true);
    }

}