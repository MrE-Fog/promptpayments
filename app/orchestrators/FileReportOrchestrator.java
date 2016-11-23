package orchestrators;

import com.google.inject.Inject;
import components.CompaniesHouseCommunicator;
import components.PagedList;
import components.ReportsRepository;
import models.*;
import play.mvc.Http;
import utils.TimeProvider;

import java.io.IOException;

/**
 * Created by daniel.rothig on 06/10/2016.
 *
 * Orchestrator for FileReport
 */
public class FileReportOrchestrator {
    private CompaniesHouseCommunicator companiesHouseCommunicator;
    private ReportsRepository reportsRepository;
    private TimeProvider timeProvider;

    @Inject
    FileReportOrchestrator(CompaniesHouseCommunicator companiesHouseCommunicator, ReportsRepository reportsRepository, TimeProvider timeProvider) {
        this.companiesHouseCommunicator = companiesHouseCommunicator;
        this.reportsRepository = reportsRepository;
        this.timeProvider = timeProvider;
    }

    public OrchestratorResult<FilingData> tryMakeReportFilingModel(String token, String companiesHouseIdentifier) {
        CompanySummary company = null;
        try {
            company = companiesHouseCommunicator.getCompany(companiesHouseIdentifier);
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Cannot retrieve companies");
        }
        if (company == null) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        if (!reportsRepository.mayFileForCompany(token,companiesHouseIdentifier)) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        }

        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget(companiesHouseIdentifier);

        return OrchestratorResult.fromSucccess(new FilingData(
                rfm,
                company,
                new UiDate(timeProvider.Now())
        ));
    }

    public OrchestratorResult<ValidatedFilingData> tryValidateReportFilingModel(ReportFilingModel model) {
        CompanySummary company = null;
        try {
            company = companiesHouseCommunicator.getCompany(model.getTargetCompanyCompaniesHouseIdentifier());
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Cannot retrieve companies");
        }
        if (company == null) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        ReportFilingModelValidation validation = new ReportFilingModelValidationImpl(model, timeProvider.Now());

        return OrchestratorResult.fromSucccess(new ValidatedFilingData(
                model,
                validation,
                company,
                new UiDate(timeProvider.Now())
        ));
    }

    public OrchestratorResult<Integer> tryFileReport(String oAuthToken, ReportFilingModel model) {
        CompanySummary company = null;
        try {
            company = companiesHouseCommunicator.getCompany(model.getTargetCompanyCompaniesHouseIdentifier());
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Cannot retrieve companies");
        }
        if (company == null) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        if (!reportsRepository.mayFileForCompany(oAuthToken, model.getTargetCompanyCompaniesHouseIdentifier())) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        }

        int i = reportsRepository.TryFileReport(model, company, timeProvider.Now());
        return OrchestratorResult.fromSucccess(i);
    }

    public OrchestratorResult<PagedList<CompanySummaryWithAddress>> findRegisteredCompanies(String company, int page, int itemsPerPage) {
        try {
            return OrchestratorResult.fromSucccess(companiesHouseCommunicator.searchCompanies(company, page, itemsPerPage));
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Internal error: Couldn't search companies");
        }
    }
    String authorizedCallbackUri = "https://paymentdutyregister.herokuapp.com/FileReport/cb";


    public String getAuthorizationUri(String companiesHouseIdentifier) {
        return companiesHouseCommunicator.getAuthorizationUri(authorizedCallbackUri, companiesHouseIdentifier);
    }

    public OrchestratorResult<Http.Cookie> tryAuthorize(String code, String companiesHouseIdentifier) {
        try {
            String authTokenCookieName = "auth";
            String authToken = companiesHouseCommunicator.verifyAuthCode(code, authorizedCallbackUri, companiesHouseIdentifier);
            if (authToken != null) {
                reportsRepository.linkAuthTokenToCompany(authToken, companiesHouseIdentifier);
                return OrchestratorResult.fromSucccess(Http.Cookie.builder(authTokenCookieName, authToken).build());
            }
            return OrchestratorResult.fromFailure("Could not authenticate user");

        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Could not authenticate user");
        }
    }

    public OrchestratorResult<PagedList<CompanySearchResult>> trySearchCompanies(String company, int page, int resultsPerPage) {
        try {
            PagedList<CompanySummaryWithAddress> companySummaries = companiesHouseCommunicator.searchCompanies(company, page, resultsPerPage);
            PagedList<CompanySearchResult> results = reportsRepository.getCompanySearchInfo(companySummaries);

            return OrchestratorResult.fromSucccess(results);
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Could not query companies");
        }
    }

    public OrchestratorResult<CompanyModel> getCompanyModel(String company, int page, int itemsPerPage) {
        try {
            CompanySummary companySummary = companiesHouseCommunicator.getCompany(company);
            CompanyModel companyModel = reportsRepository.getCompanyModel(companySummary, page, itemsPerPage);
            return OrchestratorResult.fromSucccess(companyModel);
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Could not retrieve companies");
        }
    }
}
