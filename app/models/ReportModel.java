package models;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Represents a full payment report.
 */
public class ReportModel {
    public ReportSummary Info;
    public CompanySummary Company;

    public ReportModel(ReportSummary info, CompanySummary company) {
        Info = info;
        Company = company;
    }
}
