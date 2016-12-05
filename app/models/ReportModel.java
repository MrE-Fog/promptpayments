package models;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Represents a full payment report.
 */
public class ReportModel {
    public final ReportSummary Info;

    public final BigDecimal AverageTimeToPay;
    public final BigDecimal PercentInvoicesPaidBeyondAgreedTerms;
    public final BigDecimal PercentInvoicesWithin30Days;
    public final BigDecimal PercentInvoicesWithin60Days;
    public final BigDecimal PercentInvoicesBeyond60Days;

    public final Calendar StartDate;
    public final Calendar EndDate;

    public final String PaymentTerms;
    public final String MaximumContractPeriod;
    public final boolean PaymentTermsChanged;
    public final String PaymentTermsChangedComment;
    public final boolean PaymentTermsChangedNotified;
    public final String PaymentTermsChangedNotifiedComment;
    public final String PaymentTermsComment;

    public final String DisputeResolution;

    public final boolean OfferEInvoicing;
    public final boolean OfferSupplyChainFinance;
    public final boolean RetentionChargesInPolicy;
    public final boolean RetentionChargesInPast;

    public final boolean HasPaymentCodes;
    public final String PaymentCodes;

    public String getStartDateString() {
        return new UiDate(StartDate).ToDateString();
    }
    public String getEndDateString() {
        return new UiDate(EndDate).ToDateString();
    }

    public ReportModel(ReportSummary info, BigDecimal averageTimeToPay, BigDecimal percentInvoicesPaidBeyondAgreedTerms, BigDecimal percentInvoicesWithin30Days, BigDecimal percentInvoicesWithin60Days, BigDecimal percentInvoicesBeyond60Days, Calendar startDate, Calendar endDate, String paymentTerms, String maximumContractPeriod, boolean paymentTermsChanged, String paymentTermsChangedComment, boolean paymentTermsChangedNotified, String paymentTermsChangedNotifiedComment, String paymentTermsComment, String disputeResolution, boolean offerEInvoicing, boolean offerSupplyChainFinance, boolean retentionChargesInPolicy, boolean retentionChargesInPast, boolean hasPaymentCodes, String paymentCodes) {
        // todo: remove dates, as they are now in the summary

        Info = info;
        AverageTimeToPay = averageTimeToPay;
        PercentInvoicesPaidBeyondAgreedTerms = percentInvoicesPaidBeyondAgreedTerms;
        PercentInvoicesWithin30Days = percentInvoicesWithin30Days;
        PercentInvoicesWithin60Days = percentInvoicesWithin60Days;
        PercentInvoicesBeyond60Days = percentInvoicesBeyond60Days;
        StartDate = startDate;
        EndDate = endDate;
        PaymentTerms = paymentTerms;
        MaximumContractPeriod = maximumContractPeriod;
        PaymentTermsChanged = paymentTermsChanged;
        PaymentTermsChangedComment = paymentTermsChangedComment;
        PaymentTermsChangedNotified = paymentTermsChangedNotified;
        PaymentTermsChangedNotifiedComment = paymentTermsChangedNotifiedComment;
        PaymentTermsComment = paymentTermsComment;
        DisputeResolution = disputeResolution;
        OfferEInvoicing = offerEInvoicing;
        OfferSupplyChainFinance = offerSupplyChainFinance;
        RetentionChargesInPolicy = retentionChargesInPolicy;
        RetentionChargesInPast = retentionChargesInPast;
        HasPaymentCodes = hasPaymentCodes;
        PaymentCodes = paymentCodes;
    }
}

