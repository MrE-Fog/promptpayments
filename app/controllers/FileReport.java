package controllers;

import com.google.inject.Inject;
import models.FilingData;
import models.ReportFilingModel;
import orchestrators.FileReportOrchestrator;
import orchestrators.OrchestratorResult;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Result;
import utils.DatePickerHelper;
import utils.TimeProvider;
import utils.common.SelectOption;

import java.util.Arrays;
import java.util.List;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Controller for submitting payment reports
 */
public class FileReport extends PageController {

    @Inject
    private FileReportOrchestrator fileReportOrchestrator;

    @Inject
    private TimeProvider timeProvider;

    private Form<ReportFilingModel> reportForm;

    @Inject
    public FileReport(FormFactory formFactory) {
        reportForm = formFactory.form(ReportFilingModel.class);
    }


    public Result index() {return ok(page(views.html.FileReport.index.render())); }

    public Result login() {
        return ok(page(views.html.FileReport.login.render()));
    }

    public Result companies(int page) {
        return ok(page(views.html.FileReport.companies.render(fileReportOrchestrator.getCompaniesForUser("bullshitToken", page, 25).get())));
    }

    public Result file(String companiesHouseIdentifier) {
        OrchestratorResult<FilingData> data = fileReportOrchestrator.tryMakeReportFilingModel("bullshitToken", companiesHouseIdentifier);
        if (data.success()) {
            return ok(page(views.html.FileReport.file.render(reportForm.fill(data.get().model), data.get().company, data.get().date, new DatePickerHelper(timeProvider))));
        } else {
            return status(401, data.message());
        }
    }

    public Result reviewFiling() {
        OrchestratorResult<FilingData> data = fileReportOrchestrator.tryValidateReportFilingModel("bullshitToken", reportForm.bindFromRequest(request()).get());
        if (data.success()) {
            return ok(page(views.html.FileReport.review.render(reportForm.fill(data.get().model), data.get().company, data.get().date)));
        } else {
            return status(401, data.message());
        }
    }

    public Result editFiling() {
        OrchestratorResult<FilingData> data = fileReportOrchestrator.tryValidateReportFilingModel("bullshitToken", reportForm.bindFromRequest(request()).get());
        if (data.success()) {
            return ok(page(views.html.FileReport.file.render(reportForm.fill(data.get().model), data.get().company, data.get().date, new DatePickerHelper(timeProvider))));
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

