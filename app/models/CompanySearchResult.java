package models;

/**
 * Container object that holds enough information about a company to render a search result.
 */
public class CompanySearchResult extends CompanySummaryWithAddress {
    public int ReportCount;

    public CompanySearchResult(CompanySummaryWithAddress summary, int reportCount) {
        super(summary.Name, summary.CompaniesHouseIdentifier, summary.AddressLine);

        this.ReportCount = reportCount;
    }
}
