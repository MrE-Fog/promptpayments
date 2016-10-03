package models;

import java.util.List;

/**
 * Created by daniel.rothig on 03/10/2016.
 * Represents data used to render a Company page
 *
 */
public class CompanyModel {
    public CompanySummary Info;
    public List<ReportSummary> ReportSummaries;

    public CompanyModel(CompanySummary info, List<ReportSummary> reportSummaries) {
        Info = info;
        ReportSummaries = reportSummaries;
    }
}

