package controllers;

import com.google.inject.Inject;
import components.CsvDataExporter;
import models.CalculatorModel;
import play.mvc.Result;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Main controller
 */
public class Home extends PageController {
    @Inject
    private CsvDataExporter csvDataExporter;

    public Result index() {return ok(page(views.html.Home.index.render())); }

    public Result export() {
        response().setHeader("Content-Disposition", "attachment; filename=\"paymentreports.csv\"");
        response().setHeader("Content-Type", "text/csv");
        return ok(csvDataExporter.GenerateCsv());
    }

    public Result accessData() { return ok(page(views.html.Home.accessData.render())); }

    public Result ifGuide() {
        return ok(page(views.html.Home.ifGuide.render()));
    }

    public Result howGuide(int page) {
        return ok(page(views.html.Home.howGuide.render(page, new CalculatorModel())));
    }

    public Result calculatePeriod() {
        CalculatorModel model = new CalculatorModel(
             getPostParameter("start-year"),
             getPostParameter("start-month"),
             getPostParameter("start-day"),
             getPostParameter("end-year"),
             getPostParameter("end-month"),
             getPostParameter("end-day")
        );

        return ok(page(views.html.Home.howGuide.render(1, model)));
    }
}


