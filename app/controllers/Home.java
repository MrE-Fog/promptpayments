package controllers;

import com.google.inject.Inject;
import components.CsvDataExporter;
import play.mvc.Result;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Main controller
 */
public class Home extends PageController {
    @Inject
    private CsvDataExporter csvDataExporter;

    //public Result index() {return ok(views.html.Home.index.render()); }
    public Result index() {return ok(page(views.html.Home.index.render())); }


    public Result about() {return ok(page(views.html.Home.about.render())); }
    public Result export() {
        response().setHeader("Content-Disposition", "attachment; filename=\"paymentreports.csv\"");
        response().setHeader("Content-Type", "text/csv");
        return ok(csvDataExporter.GenerateCsv());
    }

    public Result accessData() {return ok(page(views.html.Home.accessData.render()));}
}


