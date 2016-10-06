package models;

/**
 * POCO to populate FileReport.file() and FileReport.reviewFiling()
 */
public class FilingData {
    public final ReportFilingModel model;
    public final CompanySummary company;
    public final UiDate date;

    public FilingData(ReportFilingModel model, CompanySummary company, UiDate date) {
        this.model = model;
        this.company = company;
        this.date = date;
    }
}
