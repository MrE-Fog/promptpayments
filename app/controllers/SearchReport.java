package controllers;

import components.ReportsRepository;
import models.CompanyModel;
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

    public Result searchstart() {return ok(page(views.html.Reports.searchstart.render())); }

    public Result search() {return ok(page(views.html.Reports.search.render())); }

    public Result handleSearch() {
        String company = request().body().asFormUrlEncoded().get("companyname")[0];
        return ok(page(views.html.Reports.results.render(company, reportsRepository.searchCompanies(company))));
    }

    public Result company(String company) {
        CompanyModel companyModel = reportsRepository.getCompanyByCompaniesHouseIdentifier(company).get();
        return ok(page(views.html.Reports.company.render(companyModel)));
    }
}
