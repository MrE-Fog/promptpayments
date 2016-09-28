package controllers;

import play.mvc.*;
import views.html.*;

import play.data.FormFactory;

/**
 * Created by daniel.rothig on 27/09/2016.
 */
public class Home extends Controller {
    public Result index() {return ok(views.html.index.render()); }

}
