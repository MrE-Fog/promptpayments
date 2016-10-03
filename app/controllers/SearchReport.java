package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Search for reports
 */
public class SearchReport extends Controller {
    public Result searchstart() {return ok(views.html.Reports.searchstart.render()); }

    public Result search() {return ok(views.html.Reports.search.render()); }

    public Result handleSearch() {
        String company = request().body().asFormUrlEncoded().get("companyname")[0];
        return ok(company);
    }
}
