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

    public Result handleSearch(int page) {
        String company = request().body().asFormUrlEncoded().get("companyname")[0];
        return ok(page(views.html.Reports.results.render(company, reportsRepository.searchCompanies(company, page, 25))));
    }

    public Result company(String company, int page) {
        CompanyModel companyModel = reportsRepository.getCompanyByCompaniesHouseIdentifier(company, page, 25).get();
        return ok(page(views.html.Reports.company.render(companyModel)));
    }
}
