package models;

public class AllOkReportFilingModelValidation implements ReportFilingModelValidation {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public FieldValidation validateAverageTimeToPay() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validatePercentInvoicesPaidBeyondAgreedTerms() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validatePercentInvoicesWithin30Days() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validatePercentInvoicesWithin60Days() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validatePercentInvoicesBeyond60Days() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateTimePercentages() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateStartDate() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateEndDate() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validatePaymentTerms() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateMaximumContractPeriod() { return FieldValidation.ok(); }

    @Override
    public FieldValidation validatePaymentTermsChanged() { return FieldValidation.ok(); }

    @Override
    public FieldValidation validatePaymentTermsChangedComment() { return FieldValidation.ok(); }

    @Override
    public FieldValidation validatePaymentTermsChangedNotified() { return FieldValidation.ok(); }

    @Override
    public FieldValidation validatePaymentTermsChangedNotifiedComment() { return FieldValidation.ok(); }

    @Override
    public FieldValidation validatePaymentTermsComment() { return FieldValidation.ok(); }

    @Override
    public FieldValidation validateDisputeResolution() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateHasPaymentCodes() { return FieldValidation.ok(); }

    @Override
    public FieldValidation validatePaymentCodes() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateOfferEInvoicing() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateOfferSupplyChainFinance() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateRetentionChargesInPolicy() {
        return FieldValidation.ok();
    }

    @Override
    public FieldValidation validateRetentionChargesInPast() {
        return FieldValidation.ok();
    }
}
