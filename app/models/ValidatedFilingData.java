package models;

/**
 * POCO to populate FileReport.reviewFiling()
 */
public class ValidatedFilingData {
    public final ReportFilingModel model;
    public final ReportFilingModelValidation validation;
    public final CompanySummary company;
    public final UiDate date;

    public ValidatedFilingData(ReportFilingModel model, ReportFilingModelValidation validation, CompanySummary company, UiDate date) {
        this.model = model;
        this.validation = validation;
        this.company = company;
        this.date = date;
    }
}
