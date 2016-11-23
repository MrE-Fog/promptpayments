package controllers;

import components.ReportsRepository;
import models.CompanySummary;
import models.ReportModel;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * View filed reports
 */
public class ViewReport extends PageController {

    @Inject
    private ReportsRepository reportsRepository;

    public Result view(String companiesHouseIdentifier, int reportId) {

        CompanySummary company = reportsRepository.getCompanySummaries(Arrays.asList(new String[]{companiesHouseIdentifier}), 0, 25).get(0);
        ReportModel report = reportsRepository.getReport(companiesHouseIdentifier, reportId).get();
        return ok(page(views.html.Reports.report.render(report, company)));
    }
}
