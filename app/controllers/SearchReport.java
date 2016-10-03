package controllers;

import components.ReportsRepository;
import models.CompanyModel;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Array;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Search for reports
 */
public class SearchReport extends Controller {

    @Inject
    private ReportsRepository reportsRepository;

    public Result searchstart() {return ok(views.html.Reports.searchstart.render()); }

    public Result search() {return ok(views.html.Reports.search.render()); }

    public Result handleSearch() {
        String company = request().body().asFormUrlEncoded().get("companyname")[0];
        return ok(views.html.Reports.results.render(company, reportsRepository.searchCompanies(company)));
    }

    public Result company(String company) {
        CompanyModel companyModel = reportsRepository.getCompanyByCompaniesHouseIdentifier(company);
        return ok(views.html.Reports.company.render(companyModel));
    }
}
