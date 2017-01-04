package orchestrators;

import com.google.inject.Inject;
import components.*;
import models.*;
import play.mvc.Http;
import scala.Option;
import utils.TimeProvider;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by daniel.rothig on 06/10/2016.
 *
 * Orchestrator for FileReport
 */
public class FileReportOrchestrator {
    private CompaniesHouseCommunicator companiesHouseCommunicator;
    private ReportsRepository reportsRepository;
    private GovUkNotifyEmailer emailer;
    private TimeProvider timeProvider;

    private final String authorizedCallbackUri = "https://paymentdutyregister.herokuapp.com/FileReport/cb";


    @Inject
    FileReportOrchestrator(CompaniesHouseCommunicator companiesHouseCommunicator, ReportsRepository reportsRepository, GovUkNotifyEmailer emailer, TimeProvider timeProvider) {
        this.companiesHouseCommunicator = companiesHouseCommunicator;
        this.reportsRepository = reportsRepository;
        this.emailer = emailer;
        this.timeProvider = timeProvider;
    }

    public OrchestratorResult<FilingData> tryMakeReportFilingModel(String companiesHouseIdentifier) {
        OrchestratorResult<CompanySummary> company = getCompanySummary(companiesHouseIdentifier);
        if (!company.success()) {
            return OrchestratorResult.fromFailure(company.message());
        }

        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget(companiesHouseIdentifier);

        return OrchestratorResult.fromSucccess(new FilingData(
                rfm,
                company.get(),
                new UiDate(timeProvider.Now())
        ));
    }
    public OrchestratorResult<ValidatedFilingData> tryValidateReportFilingModel(ReportFilingModel model) {
        OrchestratorResult<CompanySummary> company = getCompanySummary(model.getTargetCompanyCompaniesHouseIdentifier());
        if (!company.success()) {
            return OrchestratorResult.fromFailure(company.message());
        }

        ReportFilingModelValidation validation = new ReportFilingModelValidationImpl(model, timeProvider.Now());

        return OrchestratorResult.fromSucccess(new ValidatedFilingData(
                model,
                validation,
                company.get(),
                new UiDate(timeProvider.Now())
        ));
    }

    public OrchestratorResult<FilingOutcome> tryFileReport(String oAuthToken, ReportFilingModel model, UrlMapper urlMapper) {
        OrchestratorResult<CompanySummary> company = getCompanySummary(model.getTargetCompanyCompaniesHouseIdentifier());
        if (!company.success()) {
            return OrchestratorResult.fromFailure(company.message());
        }

        RefreshTokenAndValue<Boolean> authorized;
        try {
            authorized = companiesHouseCommunicator.isInScope(model.getTargetCompanyCompaniesHouseIdentifier(), oAuthToken);
        } catch(IOException ignored) {
            authorized = new RefreshTokenAndValue<>(oAuthToken, false);
        }

        if (!authorized.value) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company", authorized.refreshToken);
        }

        Calendar now = timeProvider.Now();
        ReportSummary summary = reportsRepository.tryFileReport(model, company.get(), now);

        RefreshTokenAndValue<String> emailAddress = sendConfirmation(authorized.refreshToken, company.get(), summary, urlMapper.getUrlFromReportId(summary.Identifier));


        return OrchestratorResult.fromSucccess(new FilingOutcome(
                company.get(),
                summary.Identifier,
                emailAddress != null ? emailAddress.value : null
        ), emailAddress != null ? emailAddress.refreshToken : authorized.refreshToken);
    }

    public OrchestratorResult<PagedList<CompanySummaryWithAddress>> findRegisteredCompanies(String company, int page, int itemsPerPage) {
        try {
            return OrchestratorResult.fromSucccess(companiesHouseCommunicator.searchCompanies(company, page, itemsPerPage));
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Could not retrieve companies");
        }
    }

    public String getAuthorizationUri(String companiesHouseIdentifier) {
        try {
            return companiesHouseCommunicator.getAuthorizationUri(authorizedCallbackUri, companiesHouseIdentifier);
        } catch (IOException shouldNeverHappen) {
            return null;
        }
    }

    public OrchestratorResult<String> tryAuthorize(String code, String companiesHouseIdentifier) {
        try {
            String authToken = companiesHouseCommunicator.verifyAuthCode(code, authorizedCallbackUri, companiesHouseIdentifier);
            if (authToken != null) {
                //reportsRepository.linkAuthTokenToCompany(authToken, companiesHouseIdentifier);
                return OrchestratorResult.fromSucccess(authToken, authToken);
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
            return OrchestratorResult.fromFailure("Could not retrieve companies");
        }
    }

    public OrchestratorResult<CompanyModel> getCompanyModel(String company, int page, int itemsPerPage) {
        OrchestratorResult<CompanySummary> companySummary = getCompanySummary(company);
        if (!companySummary.success()) {
            return OrchestratorResult.fromFailure(companySummary.message());
        }

        PagedList<ReportSummary> reportSummaries = reportsRepository.getReportSummaries(company, page, itemsPerPage);
        return OrchestratorResult.fromSucccess(new CompanyModel(companySummary.get(), reportSummaries));
    }

    public OrchestratorResult<CompanySummaryAndReport> getReport(String companiesHouseIdentifier, int reportId) {
        OrchestratorResult<CompanySummary> companySummary = getCompanySummary(companiesHouseIdentifier);
        if (!companySummary.success()) {
            return OrchestratorResult.fromFailure(companySummary.message());
        }

        Option<ReportModel> model = reportsRepository.getReport(companiesHouseIdentifier, reportId);
        if(model.isEmpty()) {
            return OrchestratorResult.fromFailure("Could not find report");
        }

        return OrchestratorResult.fromSucccess(new CompanySummaryAndReport(companySummary.get(), model.get()));
    }


    private OrchestratorResult<CompanySummary> getCompanySummary(String companiesHouseIdentifier) {
        try {
            CompanySummary company = companiesHouseCommunicator.getCompany(companiesHouseIdentifier);
            if (company == null) {
                return OrchestratorResult.fromFailure("Unknown company");
            }
            return OrchestratorResult.fromSucccess(company);
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Cannot retrieve companies");
        }
    }


    private RefreshTokenAndValue<String> sendConfirmation(String auth, CompanySummary company, ReportSummary reportSummary, String url) {
        RefreshTokenAndValue<String> email = getUserEmail(auth);
        if (email == null || email.value == null) {
            return null;
        }

        if(emailer.sendConfirmationEmail(email.value, company, reportSummary, url)) {
            return email;
        } else {
            return null;
        }
    }

    private RefreshTokenAndValue<String> getUserEmail(String auth) {
        try {
            return companiesHouseCommunicator.getEmailAddress(auth);
        } catch (IOException e) {
            return null;
        }
    }

    public interface UrlMapper {
        public String getUrlFromReportId(int reportId);
    }
}
