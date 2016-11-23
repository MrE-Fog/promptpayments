package models;

public class CompanySearchResult extends CompanySummaryWithAddress {
    public int ReportCount;

    public CompanySearchResult(CompanySummaryWithAddress summary, int reportCount) {
        super(summary.Name, summary.CompaniesHouseIdentifier, summary.AddressLine);

        this.ReportCount = reportCount;
    }
}
