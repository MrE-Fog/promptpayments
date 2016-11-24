package models;

import utils.DecimalConverter;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Model to be populated by web form
 */
public class ReportFilingModel {
    private String TargetCompanyCompaniesHouseIdentifier;

    private String AverageTimeToPay;
    private String PercentInvoicesPaidBeyondAgreedTerms;
    private String PercentInvoicesWithin30Days;
    private String PercentInvoicesWithin60Days;
    private String PercentInvoicesBeyond60Days;

    private String StartDate_year;
    private String StartDate_month;
    private String StartDate_day;

    private String EndDate_year;
    private String EndDate_month;
    private String EndDate_day;

    private String PaymentTerms;
    private String DisputeResolution;
    private String PaymentCodes;

    private Boolean OfferEInvoicing = null;
    private Boolean OfferSupplyChainFinance = null;
    private Boolean RetentionChargesInPolicy = null;
    private Boolean RetentionChargesInPast = null;

    /* Formatted getters */

    public BigDecimal getAverageTimeToPayAsDecimal() {
        return DecimalConverter.getBigDecimal(AverageTimeToPay);
    }

    public BigDecimal getPercentInvoicesPaidBeyondAgreedTermsAsDecimal() {
        return DecimalConverter.getBigDecimal(PercentInvoicesPaidBeyondAgreedTerms);
    }

    public BigDecimal getPercentInvoicesWithin30DaysAsDecimal() {
        return DecimalConverter.getBigDecimal(PercentInvoicesWithin30Days);
    }

    public BigDecimal getPercentInvoicesWithin60DaysAsDecimal() {
        return DecimalConverter.getBigDecimal(PercentInvoicesWithin60Days);
    }

    public BigDecimal getPercentInvoicesBeyond60DaysAsDecimal() {
        return DecimalConverter.getBigDecimal(PercentInvoicesBeyond60Days);
    }

    public Calendar getStartDate() {
        return tryMakeUtcDate(StartDate_year, StartDate_month, StartDate_day);
    }

    public Calendar getEndDate() {
        return tryMakeUtcDate(EndDate_year, EndDate_month, EndDate_day);
    }

    public String getStartDateString() {
        return new UiDate(getStartDate()).ToDateString();
    }

    public String getEndDateString() {
        return new UiDate(getEndDate()).ToDateString();
    }


    /**
     * @deprecated ORM ONLY, use MakeEmptyModelForTarget() instead.
     */
    @Deprecated
    public ReportFilingModel() {

    }

    public static ReportFilingModel MakeEmptyModelForTarget(String targetCompanyCompaniesHouseIdentifier) {
        ReportFilingModel rtn = new ReportFilingModel();
        rtn.TargetCompanyCompaniesHouseIdentifier = targetCompanyCompaniesHouseIdentifier;
        return rtn;
    }

    public static ReportFilingModel makeReportFilingModel(String targetCompanyCompaniesHouseIdentifier, double averageTimeToPay, double percentInvoicesPaidBeyondAgreedTerms, double percentInvoicesWithin30Days, double percentInvoicesWithin60Days, double percentInvoicesBeyond60Days, String startDate_year, String startDate_month, String startDate_day, String endDate_year, String endDate_month, String endDate_day, String paymentTerms, String disputeResolution, String paymentCodes, Boolean offerEInvoicing, Boolean offerSupplyChainFinance, Boolean retentionChargesInPolicy, Boolean retentionChargesInPast) {
        ReportFilingModel rfm = new ReportFilingModel();

        rfm.TargetCompanyCompaniesHouseIdentifier = targetCompanyCompaniesHouseIdentifier;
        rfm.AverageTimeToPay = "" + averageTimeToPay;
        rfm.PercentInvoicesPaidBeyondAgreedTerms = "" + percentInvoicesPaidBeyondAgreedTerms;
        rfm.PercentInvoicesWithin30Days = "" + percentInvoicesWithin30Days;
        rfm.PercentInvoicesWithin60Days = "" + percentInvoicesWithin60Days;
        rfm.PercentInvoicesBeyond60Days = "" + percentInvoicesBeyond60Days;
        rfm.StartDate_year = startDate_year;
        rfm.StartDate_month = startDate_month;
        rfm.StartDate_day = startDate_day;
        rfm.EndDate_year = endDate_year;
        rfm.EndDate_month = endDate_month;
        rfm.EndDate_day = endDate_day;
        rfm.PaymentTerms = paymentTerms;
        rfm.DisputeResolution = disputeResolution;
        rfm.PaymentCodes = paymentCodes;
        rfm.OfferEInvoicing = offerEInvoicing;
        rfm.OfferSupplyChainFinance = offerSupplyChainFinance;
        rfm.RetentionChargesInPolicy = retentionChargesInPolicy;
        rfm.RetentionChargesInPast = retentionChargesInPast;

        return rfm;
    }

    /* Getters and setters */

    public String getTargetCompanyCompaniesHouseIdentifier() {
        return TargetCompanyCompaniesHouseIdentifier;
    }

    public void setTargetCompanyCompaniesHouseIdentifier(String targetCompanyCompaniesHouseIdentifier) {
        TargetCompanyCompaniesHouseIdentifier = targetCompanyCompaniesHouseIdentifier;
    }

    public String getStartDate_year() {
        return StartDate_year;
    }

    public void setStartDate_year(String startDate_year) {
        StartDate_year = startDate_year;
    }

    public String getStartDate_month() {
        return StartDate_month;
    }

    public void setStartDate_month(String startDate_month) {
        StartDate_month = startDate_month;
    }

    public String getStartDate_day() {
        return StartDate_day;
    }

    public void setStartDate_day(String startDate_day) {
        StartDate_day = startDate_day;
    }

    public String getEndDate_year() {
        return EndDate_year;
    }

    public void setEndDate_year(String endDate_year) {
        EndDate_year = endDate_year;
    }

    public String getEndDate_month() {
        return EndDate_month;
    }

    public void setEndDate_month(String endDate_month) {
        EndDate_month = endDate_month;
    }

    public String getEndDate_day() {
        return EndDate_day;
    }

    public void setEndDate_day(String endDate_day) {
        EndDate_day = endDate_day;
    }

    public String getAverageTimeToPay() {
        return AverageTimeToPay;
    }

    public void setAverageTimeToPay(String averageTimeToPay) {
        AverageTimeToPay = averageTimeToPay;
    }

    public String getPercentInvoicesPaidBeyondAgreedTerms() {
        return PercentInvoicesPaidBeyondAgreedTerms;
    }

    public void setPercentInvoicesPaidBeyondAgreedTerms(String percentInvoicesPaidBeyondAgreedTerms) {
        PercentInvoicesPaidBeyondAgreedTerms = percentInvoicesPaidBeyondAgreedTerms;
    }

    public String getPercentInvoicesWithin30Days() {
        return PercentInvoicesWithin30Days;
    }

    public void setPercentInvoicesWithin30Days(String percentInvoicesWithin30Days) {
        PercentInvoicesWithin30Days = percentInvoicesWithin30Days;
    }

    public String getPercentInvoicesWithin60Days() {
        return PercentInvoicesWithin60Days;
    }

    public void setPercentInvoicesWithin60Days(String percentInvoicesWithin60Days) {
        PercentInvoicesWithin60Days = percentInvoicesWithin60Days;
    }

    public String getPercentInvoicesBeyond60Days() {
        return PercentInvoicesBeyond60Days;
    }

    public void setPercentInvoicesBeyond60Days(String percentInvoicesBeyond60Days) {
        PercentInvoicesBeyond60Days = percentInvoicesBeyond60Days;
    }

    public String getPaymentTerms() {
        return PaymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        PaymentTerms = paymentTerms;
    }

    public String getDisputeResolution() {
        return DisputeResolution;
    }

    public void setDisputeResolution(String disputeResolution) {
        DisputeResolution = disputeResolution;
    }

    public String getPaymentCodes() {
        return PaymentCodes;
    }

    public void setPaymentCodes(String paymentCodes) {
        PaymentCodes = paymentCodes;
    }

    public Boolean isOfferEInvoicing() {
        return OfferEInvoicing;
    }

    public void setOfferEInvoicing(Boolean offerEInvoicing) {
        OfferEInvoicing = offerEInvoicing;
    }

    public Boolean isOfferSupplyChainFinance() {
        return OfferSupplyChainFinance;
    }

    public void setOfferSupplyChainFinance(Boolean offerSupplyChainFinance) {
        OfferSupplyChainFinance = offerSupplyChainFinance;
    }

    public Boolean isRetentionChargesInPolicy() {
        return RetentionChargesInPolicy;
    }

    public void setRetentionChargesInPolicy(Boolean retentionChargesInPolicy) {
        RetentionChargesInPolicy = retentionChargesInPolicy;
    }

    public Boolean isRetentionChargesInPast() {
        return RetentionChargesInPast;
    }

    public void setRetentionChargesInPast(Boolean retentionChargesInPast) {
        RetentionChargesInPast = retentionChargesInPast;
    }

    private Calendar tryMakeUtcDate(String year, String month, String day) {
        if (year == null || year.isEmpty()) return null;
        if (month == null || month.isEmpty()) return null;
        if (day == null || day.isEmpty()) return null;
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setLenient(false);
            calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
            calendar.getTime(); // this throws if the year-month-day combination is invalid.
            return calendar;
        } catch (Exception ignored) {
            return null;
        }
    }
}

