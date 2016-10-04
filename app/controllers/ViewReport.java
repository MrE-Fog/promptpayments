package controllers;

import components.ReportsRepository;
import models.CompanyModel;
import models.CompanySummary;
import models.ReportModel;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * View filed reports
 */
public class ViewReport extends Controller {

    @Inject
    private ReportsRepository reportsRepository;

    public Result view(String companiesHouseIdentifier, int reportId) {
        CompanySummary company = reportsRepository.getCompanyByCompaniesHouseIdentifier(companiesHouseIdentifier).Info;
        ReportModel report = reportsRepository.getReport(companiesHouseIdentifier, reportId);
        return ok(views.html.Reports.report.render(report, company));
    }
}
