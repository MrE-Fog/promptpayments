package models;

public class CompanySummaryAndReport {
    public final CompanySummary company;
    public final ReportModel report;

    public CompanySummaryAndReport(CompanySummary company, ReportModel report) {
        this.company = company;
        this.report = report;
    }
}
