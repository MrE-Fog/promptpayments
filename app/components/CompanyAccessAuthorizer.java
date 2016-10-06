package components;

import com.google.inject.Inject;
import models.CompanySummary;
import models.ReportFilingModel;
import utils.TimeProvider;

import java.util.Collections;
import java.util.List;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Determines which companies a user may file for
 */
public class CompanyAccessAuthorizer {
    private ReportsRepository reportsRepository;

    private MockCompaniesHouseCommunicator companiesHouseCommunicator;

    private TimeProvider timeProvider;

    @Inject
    @SuppressWarnings("WeakerAccess")
    public CompanyAccessAuthorizer(ReportsRepository reportsRepository, MockCompaniesHouseCommunicator companiesHouseCommunicator, TimeProvider timeProvider) {
        this.reportsRepository = reportsRepository;
        this.companiesHouseCommunicator = companiesHouseCommunicator;
        this.timeProvider = timeProvider;
    }

    public List<CompanySummary> GetCompaniesForUser(String oAuthToken) {
        List<String> companiesHouseIdentifiers = companiesHouseCommunicator.RequestAuthorizedCompaniesForUser(oAuthToken);
        return reportsRepository.getCompanySummaries(companiesHouseIdentifiers);
    }

    public ReportFilingModel TryMakeReportFilingModel(String oAuthToken, String companiesHouseIdentifier) {
        if (!MayFileReport(oAuthToken, companiesHouseIdentifier)) {
            return null;
        }

        List<CompanySummary> matches = reportsRepository.getCompanySummaries(Collections.singletonList(companiesHouseIdentifier));
        ReportFilingModel rtn = new ReportFilingModel();
        rtn.setTargetCompanyCompaniesHouseIdentifier(matches.get(0).CompaniesHouseIdentifier);
        return rtn;
    }

    public int TryFileReport(String oAuthToken, ReportFilingModel reportFilingModel) {
        if (!MayFileReport(oAuthToken, reportFilingModel.getTargetCompanyCompaniesHouseIdentifier())) {
            return -1;
        }
        return reportsRepository.TryFileReport(reportFilingModel, timeProvider.Now());
    }

    private boolean MayFileReport(String oAuthToken, String companiesHouseIdentifier) {
        return companiesHouseIdentifier != null && oAuthToken != null
                && companiesHouseCommunicator.RequestAuthorizedCompaniesForUser(oAuthToken).contains(companiesHouseIdentifier);
    }

}

