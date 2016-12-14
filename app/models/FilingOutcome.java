package models;

/**
 * Container object for the info to render the result of a filing attempt
 */
public class FilingOutcome {
    public final CompanySummary company;
    public final int reportId;
    public final String confirmationEmailRecipient;

    public FilingOutcome(CompanySummary company, int reportId, String confirmationEmailRecipient) {
        this.company = company;
        this.reportId = reportId;
        this.confirmationEmailRecipient = confirmationEmailRecipient;
    }
}
