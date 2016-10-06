package controllers;

import components.ReportsRepository;
import models.CompanySummary;
import models.ReportModel;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * View filed reports
 */
public class ViewReport extends PageController {

    @Inject
    private ReportsRepository reportsRepository;

    public Result view(String companiesHouseIdentifier, int reportId) {
        CompanySummary company = reportsRepository.getCompanyByCompaniesHouseIdentifier(companiesHouseIdentifier, 0, 25).get().Info;
        ReportModel report = reportsRepository.getReport(companiesHouseIdentifier, reportId).get();
        return ok(page(views.html.Reports.report.render(report, company)));
    }
}
