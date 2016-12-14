package controllers;

import com.google.inject.Inject;
import models.*;
import orchestrators.FileReportOrchestrator;
import orchestrators.OrchestratorResult;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import views.html.FileReport.*;

/**
 * Created by daniel.rothig on 27/09/2016.
 *
 * Controller for submitting payment reports
 */
public class FileReport extends PageController {

    @Inject
    private FileReportOrchestrator fileReportOrchestrator;

    private Form<ReportFilingModel> reportForm;

    @Inject
    public FileReport(FormFactory formFactory) {
        reportForm = formFactory.form(ReportFilingModel.class);
    }


    public Result startForCompany(String company) {
        OrchestratorResult<CompanyModel> companyModel = fileReportOrchestrator.getCompanyModel(company, 0, 0);
        return renderOrchestratorResult(companyModel, x -> ok(page(start.render(x.Info))));
    }

    public Result signInInterstitial(String company) {
        return ok(page(signInInterstitial.render(company)));
    }

    public Result login(String companiesHouseIdentifier) {
        if (getPostParameter("account").equals("1")) {
            return redirect(fileReportOrchestrator.getAuthorizationUri(companiesHouseIdentifier));
        } else {
            OrchestratorResult<CompanyModel> companyModel = fileReportOrchestrator.getCompanyModel(companiesHouseIdentifier, 0, 0);
            return renderOrchestratorResult(companyModel, m -> ok(page(companiesHouseOptions.render(m.Info))));
        }
    }

    public Result companiesHouseOptionsResult(String companiesHouseIdentifier) {
        OrchestratorResult<CompanyModel> company = fileReportOrchestrator.getCompanyModel(companiesHouseIdentifier, 0, 0);
        return renderOrchestratorResult(company, c ->
                ok(page(companiesHouseAccount.render(c.Info, getPostParameter("nextstep").equals("0")))));
    }

    public Result loginCallback(String state, String code) {
        OrchestratorResult<Http.Cookie> orchestratorResult = fileReportOrchestrator.tryAuthorize(code, state);
        return renderOrchestratorResult(orchestratorResult,
        result -> {
            response().setCookie(result);
            return redirect(routes.FileReport.doFile(state));
        });
    }

    public Result doFile(String company) {
        String auth = request().cookie("auth").value();
        OrchestratorResult<FilingData> orchestratorResult = fileReportOrchestrator.tryMakeReportFilingModel(auth, company);
        return renderOrchestratorResult(orchestratorResult,
                model -> ok(page(file.render(
                        reportForm.fill(model.model),
                        new AllOkReportFilingModelValidation(),
                        model.company,
                        model.date
                ))));
    }


    public Result reviewFiling(boolean needsConfirmationReminder) {
        OrchestratorResult<ValidatedFilingData> orchestratorResult = fileReportOrchestrator.tryValidateReportFilingModel(reportForm.bindFromRequest(request()).get());
        return renderOrchestratorResult(orchestratorResult, data -> data.validation.isValid()
                ? ok(page(review.render(reportForm.fill(data.model), data.company, data.date, needsConfirmationReminder)))
                : ok(page(file.render(reportForm.fill(data.model), data.validation, data.company, data.date)))
        );
    }

    public Result submitFiling() {
        if (getPostParameter("revise") != null) {
            return editFiling();
        } else if (!"1".equals(getPostParameter("confirmed"))) {
            return reviewFiling(true);
        } else {
            return doSubmitFiling();
        }
    }

    private Result editFiling() {
        OrchestratorResult<ValidatedFilingData> data = fileReportOrchestrator.tryValidateReportFilingModel(reportForm.bindFromRequest(request()).get());
        return renderOrchestratorResult(data, d ->
                ok(page(file.render(reportForm.fill(d.model), d.validation, d.company, d.date))));
    }

    private Result doSubmitFiling() {
        String auth = request().cookie("auth").value();
        ReportFilingModel model = reportForm.bindFromRequest(request()).get();

        OrchestratorResult<FilingOutcome> outcome = fileReportOrchestrator.tryFileReport(auth, model, getReportUrlMapper(model));

        return renderOrchestratorResult(outcome, d ->
                ok(page(filingSuccess.render(d.company.CompaniesHouseIdentifier, d.reportId, d.confirmationEmailRecipient))));
    }

    private FileReportOrchestrator.UrlMapper getReportUrlMapper(ReportFilingModel model) {
        return reportId -> routes.SearchReport.view(model.getTargetCompanyCompaniesHouseIdentifier(), reportId).absoluteURL(request());
    }
}

