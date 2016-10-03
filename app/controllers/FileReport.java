package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Controller for submitting payment reports
 */
public class FileReport extends Controller {
    public Result index() {return ok(views.html.FileReport.index.render()); }

    public Result page(int page) {
        switch (page) {
            case 1: return ok(views.html.FileReport.page1.render());
            default: return status(404);
        }
    }
}
