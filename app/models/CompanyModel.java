package models;

import components.PagedList;

/**
 * Created by daniel.rothig on 03/10/2016.
 * Represents data used to render a Company page
 *
 */
public class CompanyModel {
    public CompanySummary Info;
    public PagedList<ReportSummary> ReportSummaries;

    public CompanyModel(CompanySummary info, PagedList<ReportSummary> reportSummaries) {
        Info = info;
        ReportSummaries = reportSummaries;
    }
}

