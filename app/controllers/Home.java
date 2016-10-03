package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Main controller
 */
public class Home extends Controller {
    public Result index() {return ok(views.html.Home.index.render()); }
    public Result about() {return ok(views.html.Home.about.render()); }
}
