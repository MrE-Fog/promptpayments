package orchestrators;

import com.google.inject.Inject;
import components.MockCompaniesHouseCommunicator;
import components.PagedList;
import components.ReportsRepository;
import models.CompanyModel;
import models.CompanySummary;
import models.FilingData;
import models.ReportFilingModel;
import scala.Option;
import utils.TimeProvider;
import models.UiDate;

import java.util.List;

/**
 * Created by daniel.rothig on 06/10/2016.
 *
 * Orchestrator for FileReport
 */
public class FileReportOrchestrator {

    private MockCompaniesHouseCommunicator companiesHouseCommunicator;
    private ReportsRepository reportsRepository;
    private TimeProvider timeProvider;

    @Inject
    FileReportOrchestrator(MockCompaniesHouseCommunicator companiesHouseCommunicator, ReportsRepository reportsRepository, TimeProvider timeProvider) {
        this.companiesHouseCommunicator = companiesHouseCommunicator;
        this.reportsRepository = reportsRepository;
        this.timeProvider = timeProvider;
    }

    public OrchestratorResult<PagedList<CompanySummary>> getCompaniesForUser(String token, int page, int itemsPerPage) {
        List<String> companyIdentifiers = companiesHouseCommunicator.RequestAuthorizedCompaniesForUser(token);
        PagedList<CompanySummary> companies = reportsRepository.getCompanySummaries(companyIdentifiers, page, itemsPerPage);
        return OrchestratorResult.fromSucccess(companies);
    }

    public OrchestratorResult<FilingData> tryMakeReportFilingModel(String token, String companiesHouseIdentifier) {
        Option<CompanyModel> company = reportsRepository.getCompanyByCompaniesHouseIdentifier(companiesHouseIdentifier, 0, 0);
        if (company.isEmpty()) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        if (!MayFileReport(token,companiesHouseIdentifier)) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        }

        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget(companiesHouseIdentifier);

        return OrchestratorResult.fromSucccess(new FilingData(
                rfm,
                company.get().Info,
                new UiDate(timeProvider.Now())
        ));
    }

    public OrchestratorResult<FilingData> tryValidateReportFilingModel(String token, ReportFilingModel model) {
        Option<CompanyModel> company = reportsRepository.getCompanyByCompaniesHouseIdentifier(model.getTargetCompanyCompaniesHouseIdentifier(), 0, 0);
        if (company.isEmpty()) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        // Any form validation would go here.

        return OrchestratorResult.fromSucccess(new FilingData(
                model,
                company.get().Info,
                new UiDate(timeProvider.Now())
        ));
    }

    public OrchestratorResult<Integer> tryFileReport(String oAuthToken, ReportFilingModel model) {
        Option<CompanyModel> company = reportsRepository.getCompanyByCompaniesHouseIdentifier(model.getTargetCompanyCompaniesHouseIdentifier(), 0, 0);
        if (company.isEmpty()) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        if (!MayFileReport(oAuthToken, model.getTargetCompanyCompaniesHouseIdentifier())) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        }

        int i = reportsRepository.TryFileReport(model, timeProvider.Now());
        return OrchestratorResult.fromSucccess(i);
    }

    private boolean MayFileReport(String oAuthToken, String companiesHouseIdentifier) {
        return companiesHouseIdentifier != null && oAuthToken != null
                && companiesHouseCommunicator.RequestAuthorizedCompaniesForUser(oAuthToken).contains(companiesHouseIdentifier);
    }
}
