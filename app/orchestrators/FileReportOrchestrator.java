package orchestrators;

import com.google.inject.Inject;
import components.CompaniesHouseCommunicator;
import components.PagedList;
import components.ReportsRepository;
import models.CompanySummary;
import models.FilingData;
import models.ReportFilingModel;
import utils.TimeProvider;
import models.UiDate;

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
        CompanySummary company = companiesHouseCommunicator.tryGetCompany(companiesHouseIdentifier);
        if (company == null) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        if (!companiesHouseCommunicator.mayFileForCompany(token,companiesHouseIdentifier)) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        }

        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget(companiesHouseIdentifier);

        return OrchestratorResult.fromSucccess(new FilingData(
                rfm,
                company,
                new UiDate(timeProvider.Now())
        ));
    }

    public OrchestratorResult<FilingData> tryValidateReportFilingModel(String token, ReportFilingModel model) {
        CompanySummary company = companiesHouseCommunicator.tryGetCompany(model.getTargetCompanyCompaniesHouseIdentifier());
        if (company == null) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        // Any form validation would go here.

        return OrchestratorResult.fromSucccess(new FilingData(
                model,
                company,
                new UiDate(timeProvider.Now())
        ));
    }

    public OrchestratorResult<Integer> tryFileReport(String oAuthToken, ReportFilingModel model) {
        CompanySummary company = companiesHouseCommunicator.tryGetCompany(model.getTargetCompanyCompaniesHouseIdentifier());
        if (company == null) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        if (!companiesHouseCommunicator.mayFileForCompany(oAuthToken, model.getTargetCompanyCompaniesHouseIdentifier())) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        }

        int i = reportsRepository.TryFileReport(model, company, timeProvider.Now());
        return OrchestratorResult.fromSucccess(i);
    }

    public OrchestratorResult<PagedList<CompanySummary>> findRegisteredCompanies(String company, int page, int itemsPerPage) {
        try {
            return OrchestratorResult.fromSucccess(companiesHouseCommunicator.searchCompanies(company, page, itemsPerPage));
        } catch (IOException e) {
            e.printStackTrace();
            return OrchestratorResult.fromFailure("Internal error: Couldn't search companies");
        }
    }
}
