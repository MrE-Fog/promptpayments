package controllers;

import components.CompaniesHouseCommunicator;
import components.PagedList;
import components.ReportsRepository;
import models.CompanyModel;
import models.CompanySearchResult;
import models.CompanySummary;
import orchestrators.FileReportOrchestrator;
import orchestrators.OrchestratorResult;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Search for reports
 */
public class SearchReport extends PageController {

    @Inject
    private ReportsRepository reportsRepository;

    @Inject
    private FileReportOrchestrator orchestrator;

    public Result searchstart() {return ok(page(views.html.Reports.searchstart.render())); }

    public Result search() {return ok(page(views.html.Reports.search.render())); }

    public Result handleSearch(int page) {
        String company = request().body().asFormUrlEncoded().get("companyname")[0];
        OrchestratorResult<PagedList<CompanySearchResult>> companies = orchestrator.trySearchCompanies(company, page, 25);

        if (companies.success()) {
            return ok(page(views.html.Reports.results.render(company, companies.get())));
        } else {
            return status(500, companies.message());
        }
    }

    public Result company(String company, int page) {
        OrchestratorResult<CompanyModel> companyModel = orchestrator.getCompanyModel(company, page, 25);
        if (companyModel.success()) {
            return ok(page(views.html.Reports.company.render(companyModel.get())));
        } else {
            return status(500, companyModel.message());
        }
    }
}
