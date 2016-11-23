package controllers;

import components.CompaniesHouseCommunicator;
import components.MockCompaniesHouseCommunicator;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;

/**
 * Created by daniel.rothig on 31/10/2016.
 */
public class CompaniesHouseMock extends Controller {

    private CompaniesHouseCommunicator communicator = new MockCompaniesHouseCommunicator();

    public Result getPage1(String chn) {return ok(views.html.conCh.p1.render(chn));}
    public Result postPage1(String chn) throws IOException {return ok(views.html.conCh.p2.render(chn, communicator.getCompany(chn).Name ));}
    public Result postPage2(String chn) {return redirect(routes.FileReport.loginCallback(chn, "code"));}
}
