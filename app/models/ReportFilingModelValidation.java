package models;

/**
 * Created by daniel.rothig on 18/10/2016.
 *
 * Validation for ReportFilingModel
 */
public interface ReportFilingModelValidation {
    boolean isValid();

    FieldValidation validateAverageTimeToPay();

    FieldValidation validatePercentInvoicesPaidBeyondAgreedTerms();

    FieldValidation validatePercentInvoicesWithin30Days();

    FieldValidation validatePercentInvoicesWithin60Days();

    FieldValidation validatePercentInvoicesBeyond60Days();

    FieldValidation validateTimePercentages();

    FieldValidation validateStartDate();

    FieldValidation validateEndDate();

    FieldValidation validatePaymentTerms();

    FieldValidation validateMaximumContractPeriod();

    FieldValidation validatePaymentTermsChanged();

    FieldValidation validatePaymentTermsChangedComment();

    FieldValidation validatePaymentTermsChangedNotified();

    FieldValidation validatePaymentTermsChangedNotifiedComment();

    FieldValidation validatePaymentTermsComment();

    FieldValidation validateDisputeResolution();

    FieldValidation validateHasPaymentCodes();

    FieldValidation validatePaymentCodes();

    FieldValidation validateOfferEInvoicing();

    FieldValidation validateOfferSupplyChainFinance();

    FieldValidation validateRetentionChargesInPolicy();

    FieldValidation validateRetentionChargesInPast();
}
