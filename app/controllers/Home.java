package controllers;

import play.mvc.*;
import views.html.*;

import play.data.FormFactory;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Main controller
 */
public class Home extends Controller {
    public Result index() {return ok(views.html.Home.index.render()); }

    public Result page(int page) {
        switch (page) {
            case 1: return ok(views.html.Home.page1.render());
            default: return status(404);
        }
    }
}
