package orchestrators;

import com.google.inject.Inject;
import components.CompaniesHouseCommunicator;
import components.GovUkNotifyEmailer;
import components.PagedList;
import components.ReportsRepository;
import models.*;
import org.assertj.core.groups.Tuple;
import play.libs.F;
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

    @Inject
    FileReportOrchestrator(CompaniesHouseCommunicator companiesHouseCommunicator, ReportsRepository reportsRepository, GovUkNotifyEmailer emailer, TimeProvider timeProvider) {
        this.companiesHouseCommunicator = companiesHouseCommunicator;
        this.reportsRepository = reportsRepository;
        this.emailer = emailer;
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

        //if (!reportsRepository.mayFileForCompany(token,companiesHouseIdentifier)) {
        //    return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        //}

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

    public OrchestratorResult<ReportSummary> tryFileReport(String oAuthToken, ReportFilingModel model) {
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

        //if (!reportsRepository.mayFileForCompany(oAuthToken, model.getTargetCompanyCompaniesHouseIdentifier())) {
        //    return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        //}

        Calendar now = timeProvider.Now();
        int i = reportsRepository.TryFileReport(model, company, now);
        return OrchestratorResult.fromSucccess(new ReportSummary(
                i,
                now,
                model.getStartDate(),
                model.getEndDate()));
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
        try {
            CompanySummary companySummary = companiesHouseCommunicator.getCompany(company);
            CompanyModel companyModel = reportsRepository.getCompanyModel(companySummary, page, itemsPerPage);
            return OrchestratorResult.fromSucccess(companyModel);
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Could not retrieve companies");
        }
    }

    public OrchestratorResult<F.Tuple<CompanySummary, ReportModel>> getReport(String companiesHouseIdentifier, int reportId) {
        try {
            CompanySummary companySummary = companiesHouseCommunicator.getCompany(companiesHouseIdentifier);
            Option<ReportModel> model = reportsRepository.getReport(companiesHouseIdentifier, reportId);
            if(model.isEmpty()) {
                return OrchestratorResult.fromFailure("Could not find report");
            }
            return OrchestratorResult.fromSucccess(new F.Tuple<>(companySummary, model.get()));
        } catch (IOException e) {
            return OrchestratorResult.fromFailure("Could not find company");
        }
    }

    public OrchestratorResult<String> getUserEmail(String auth) {
        try {
            String emailAddress = companiesHouseCommunicator.getEmailAddress(auth);
            return emailAddress == null
                    ? OrchestratorResult.fromFailure("Could not determine email address")
                    : OrchestratorResult.fromSucccess(emailAddress);
        } catch (IOException e) {
            return OrchestratorResult.fromFailure("Could not connect to Companies House");
        }
    }

    public void sendConfirmation(String auth, String targetCompanyCompaniesHouseIdentifier, ReportSummary reportSummary, String url) {
        OrchestratorResult<String> email = getUserEmail(auth);
        if (!email.success()) {
            return;
        }

        try {
            CompanySummary company = companiesHouseCommunicator.getCompany(targetCompanyCompaniesHouseIdentifier);
            emailer.sendConfirmationEmail(email.get(), company, reportSummary, url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
