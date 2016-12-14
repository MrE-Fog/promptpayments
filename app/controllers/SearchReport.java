package controllers;

import components.CompaniesHouseCommunicator;
import components.PagedList;
import components.ReportsRepository;
import models.*;
import orchestrators.FileReportOrchestrator;
import orchestrators.OrchestratorResult;
import play.libs.F;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Search for reports
 */
public class SearchReport extends PageController {

    @Inject
    private FileReportOrchestrator orchestrator;

    public Result search(boolean intentToFile) {
        return ok(page(views.html.Reports.results.render(intentToFile, "", new PagedList<>(new ArrayList<>(),0,0,0))));
    }

    public Result handleSearch(boolean intentToFile, int page) {
        String company = getPostParameter("companyname");
        OrchestratorResult<PagedList<CompanySearchResult>> companies = orchestrator.trySearchCompanies(company, page, 25);

        return renderOrchestratorResult(companies, d ->
                ok(page(views.html.Reports.results.render(intentToFile, company, d))));
    }

    public Result company(String company, int page) {
        OrchestratorResult<CompanyModel> companyModel = orchestrator.getCompanyModel(company, page, 25);
        return renderOrchestratorResult(companyModel, d ->
                ok(page(views.html.Reports.company.render(d))));
    }

    public Result view(String companiesHouseIdentifier, int reportId) {
        OrchestratorResult<CompanySummaryAndReport> report = orchestrator.getReport(companiesHouseIdentifier, reportId);
        return renderOrchestratorResult(report, d ->
            ok(page(views.html.Reports.report.render(d.report, d.company))));
    }
}
