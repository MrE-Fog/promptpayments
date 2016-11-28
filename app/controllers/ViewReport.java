package controllers;

import components.CompaniesHouseCommunicator;
import components.ReportsRepository;
import models.CompanySummary;
import models.ReportModel;
import orchestrators.FileReportOrchestrator;
import orchestrators.OrchestratorResult;
import play.libs.F;
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
    private FileReportOrchestrator orchestrator;

    public Result view(String companiesHouseIdentifier, int reportId) {
        OrchestratorResult<F.Tuple<CompanySummary, ReportModel>> report = orchestrator.getReport(companiesHouseIdentifier, reportId);

        if (report.success()) {
            return ok(page(views.html.Reports.report.render(
                    report.get()._2,
                    report.get()._1 )));
        } else {
            return status(500, report.message());
        }
    }
}
