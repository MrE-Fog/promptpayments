package orchestrators;

import com.google.inject.Inject;
import components.CompanyAccessAuthorizer;
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
    @Inject
    private CompanyAccessAuthorizer companyAccessAuthorizer;

    @Inject
    private ReportsRepository reportsRepository;

    @Inject
    private TimeProvider timeProvider;

    public FileReportOrchestrator() {
    }

    FileReportOrchestrator(CompanyAccessAuthorizer companyAccessAuthorizer, ReportsRepository reportsRepository, TimeProvider timeProvider) {
        this.companyAccessAuthorizer = companyAccessAuthorizer;
        this.reportsRepository = reportsRepository;
        this.timeProvider = timeProvider;
    }

    public OrchestratorResult<List<CompanySummary>> getCompaniesForUser(String token) {
        List<CompanySummary> companies = companyAccessAuthorizer.GetCompaniesForUser(token);
        return OrchestratorResult.fromSucccess(companies);
    }

    public OrchestratorResult<FilingData> tryMakeReportFilingModel(String token, String companiesHouseIdentifier) {
        Option<CompanyModel> company = reportsRepository.getCompanyByCompaniesHouseIdentifier(companiesHouseIdentifier);
        if (company.isEmpty()) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        ReportFilingModel reportFilingModel = companyAccessAuthorizer.TryMakeReportFilingModel(token, companiesHouseIdentifier);
        if (reportFilingModel == null) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        }

        return OrchestratorResult.fromSucccess(new FilingData(
                reportFilingModel,
                company.get().Info,
                new UiDate(timeProvider.Now())
        ));
    }

    public OrchestratorResult<FilingData> tryValidateReportFilingModel(String token, ReportFilingModel model) {
        Option<CompanyModel> company = reportsRepository.getCompanyByCompaniesHouseIdentifier(model.getTargetCompanyCompaniesHouseIdentifier());
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
        Option<CompanyModel> company = reportsRepository.getCompanyByCompaniesHouseIdentifier(model.getTargetCompanyCompaniesHouseIdentifier());
        if (company.isEmpty()) {
            return OrchestratorResult.fromFailure("Unknown company");
        }

        int i = companyAccessAuthorizer.TryFileReport(oAuthToken, model);
        if (i == -1) {
            return OrchestratorResult.fromFailure("You are not authorised to submit a filing for this company");
        }

        return OrchestratorResult.fromSucccess(i);
    }
}
