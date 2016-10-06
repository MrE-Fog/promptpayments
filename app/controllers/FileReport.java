package controllers;

import com.google.inject.Inject;
import models.FilingData;
import models.ReportFilingModel;
import orchestrators.FileReportOrchestrator;
import orchestrators.OrchestratorResult;
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
    private FileReportOrchestrator fileReportOrchestrator;


    private Form<ReportFilingModel> reportForm;

    @Inject
    public FileReport(FormFactory formFactory) {
        reportForm = formFactory.form(ReportFilingModel.class);
    }


    public Result index() {return ok(views.html.FileReport.index.render()); }

    public Result page(int page) {
        switch (page) {
            case 1: return ok(views.html.FileReport.page1.render());
            case 2: return ok(views.html.FileReport.page2.render(fileReportOrchestrator.getCompaniesForUser("bullshitToken").get()));
            default: return status(404);
        }
    }

    public Result file(String companiesHouseIdentifier) {
        OrchestratorResult<FilingData> data = fileReportOrchestrator.tryMakeReportFilingModel("bullshitToken", companiesHouseIdentifier);
        if (data.success()) {
            return ok(views.html.FileReport.file.render(reportForm.fill(data.get().model), data.get().company, data.get().date));
        } else {
            return status(401, data.message());
        }
    }

    public Result reviewFiling() {
        OrchestratorResult<FilingData> data = fileReportOrchestrator.tryValidateReportFilingModel("bullshitToken", reportForm.bindFromRequest(request()).get());
        if (data.success()) {
            return ok(views.html.FileReport.review.render(reportForm.fill(data.get().model), data.get().company, data.get().date));
        } else {
            return status(401, data.message());
        }
    }

    public Result submitFiling() {
        ReportFilingModel model = reportForm.bindFromRequest(request()).get();
        OrchestratorResult<Integer> resultingId = fileReportOrchestrator.tryFileReport("bullshitToken", model);
        if (resultingId.success()) {
            return redirect(routes.ViewReport.view(model.getTargetCompanyCompaniesHouseIdentifier(), resultingId.get()));
        } else {
            return status(401, resultingId.message());
        }
    }
}

