package orchestrators;

import com.google.inject.Inject;
import components.CompaniesHouseCommunicator;
import components.GovUkNotifyEmailer;
import components.PagedList;
import components.ReportsRepository;
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

    public OrchestratorResult<FilingData> tryMakeReportFilingModel(String token, String companiesHouseIdentifier) {
        OrchestratorResult<CompanySummary> company = getCompanySummary(companiesHouseIdentifier);
        if (!company.success()) {
            return OrchestratorResult.fromFailure(company.message());
        }

        //if (!reportsRepository.mayFileForCompany(token,companiesHouseIdentifier)) {
        //    return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        //}

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

        //if (!reportsRepository.mayFileForCompany(oAuthToken, model.getTargetCompanyCompaniesHouseIdentifier())) {
        //    return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        //}

        Calendar now = timeProvider.Now();
        ReportSummary summary = reportsRepository.tryFileReport(model, company.get(), now);

        String emailAddress = sendConfirmation(oAuthToken, company.get(), summary, urlMapper.getUrlFromReportId(summary.Identifier));

        return OrchestratorResult.fromSucccess(new FilingOutcome(
                company.get(),
                summary.Identifier,
                emailAddress
        ));
    }

    public OrchestratorResult<PagedList<CompanySummaryWithAddress>> findRegisteredCompanies(String company, int page, int itemsPerPage) {
        try {
            return OrchestratorResult.fromSucccess(companiesHouseCommunicator.searchCompanies(company, page, itemsPerPage));
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Internal error: Couldn't search companies");
        }
    }

    public String getAuthorizationUri(String companiesHouseIdentifier) {
        return companiesHouseCommunicator.getAuthorizationUri(authorizedCallbackUri, companiesHouseIdentifier);
    }

    public OrchestratorResult<Http.Cookie> tryAuthorize(String code, String companiesHouseIdentifier) {
        try {
            String authTokenCookieName = "auth";
            String authToken = companiesHouseCommunicator.verifyAuthCode(code, authorizedCallbackUri, companiesHouseIdentifier);
            if (authToken != null) {
                //reportsRepository.linkAuthTokenToCompany(authToken, companiesHouseIdentifier);
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


    private String sendConfirmation(String auth, CompanySummary company, ReportSummary reportSummary, String url) {
        String email = getUserEmail(auth);
        if (email == null) {
            return null;
        }

        if(emailer.sendConfirmationEmail(email, company, reportSummary, url)) {
            return email;
        } else {
            return null;
        }
    }

    private String getUserEmail(String auth) {
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
