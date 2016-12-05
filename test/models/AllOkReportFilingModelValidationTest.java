package models;

import org.junit.Test;
import utils.ReflectiveObjectTester;

import static org.junit.Assert.*;

public class AllOkReportFilingModelValidationTest {
    @Test
    public void isValid() throws Exception {
        AllOkReportFilingModelValidation ok = new AllOkReportFilingModelValidation();

        assertTrue(ok.isValid());

        assertEquals("the number of validation fields seems to have changed - make sure every field is tested below",23, AllOkReportFilingModelValidation.class.getDeclaredMethods().length);

        assertTrue(ok.validateStartDate().isOk());
        assertTrue(ok.validateEndDate().isOk());
        assertTrue(ok.validateAverageTimeToPay().isOk());
        assertTrue(ok.validatePercentInvoicesPaidBeyondAgreedTerms().isOk());
        assertTrue(ok.validatePercentInvoicesBeyond60Days().isOk());
        assertTrue(ok.validatePercentInvoicesWithin60Days().isOk());
        assertTrue(ok.validatePercentInvoicesWithin30Days().isOk());
        assertTrue(ok.validateTimePercentages().isOk());
        assertTrue(ok.validatePaymentTerms().isOk());
        assertTrue(ok.validateMaximumContractPeriod().isOk());
        assertTrue(ok.validatePaymentTermsChanged().isOk());
        assertTrue(ok.validatePaymentTermsChangedComment().isOk());
        assertTrue(ok.validatePaymentTermsChangedNotified().isOk());
        assertTrue(ok.validatePaymentTermsChangedNotifiedComment().isOk());
        assertTrue(ok.validatePaymentTermsComment().isOk());
        assertTrue(ok.validateHasPaymentCodes().isOk());
        assertTrue(ok.validatePaymentCodes().isOk());
        assertTrue(ok.validateDisputeResolution().isOk());
        assertTrue(ok.validateOfferEInvoicing().isOk());
        assertTrue(ok.validateOfferSupplyChainFinance().isOk());
        assertTrue(ok.validateRetentionChargesInPast().isOk());
        assertTrue(ok.validateRetentionChargesInPolicy().isOk());

    }

}