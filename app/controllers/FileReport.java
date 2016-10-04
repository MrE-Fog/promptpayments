package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import components.CompanyAccessAuthorizer;
import components.ReportsRepository;
import models.ReportFilingModel;
import models.ReportModel;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Controller for submitting payment reports
 */
public class FileReport extends Controller {

    @Inject
    private CompanyAccessAuthorizer companyAccessAuthorizer;

    @Inject
    private ReportsRepository reportsRepository;

    private Form<ReportFilingModel> reportForm;

    @Inject
    public FileReport(FormFactory formFactory) {
        reportForm = formFactory.form(ReportFilingModel.class);
    }


    public Result index() {return ok(views.html.FileReport.index.render()); }

    public Result page(int page) {
        switch (page) {
            case 1: return ok(views.html.FileReport.page1.render());
            case 2: return ok(views.html.FileReport.page2.render(companyAccessAuthorizer.GetCompaniesForUser("bullshitToken")));
            default: return status(404);
        }
    }

    public Result file(String companiesHouseIdentifier) {
        ReportFilingModel model = companyAccessAuthorizer.TryMakeReportFilingModel("bullshitToken", companiesHouseIdentifier);
        if (model == null) {
            return status(401);
        } else {
            return ok(views.html.FileReport.file.render(reportForm.fill(model)));
        }
    }

    public Result reviewFiling() {
        return ok(views.html.FileReport.review.render(reportForm.bindFromRequest(request())));
    }

    public Result submitFiling() {
        ReportFilingModel model = reportForm.bindFromRequest(request()).get();
        int resultingId = companyAccessAuthorizer.TryFileReport("bullshitToken", model);
        if (resultingId < 0) {
            return status(401);
        } else {
            return redirect(routes.ViewReport.view(model.TargetCompanyCompaniesHouseIdentifier, resultingId));
        }
    }
}
