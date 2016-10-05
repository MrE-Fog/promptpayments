package components;

import com.google.inject.Inject;
import models.CompanySummary;
import models.ReportFilingModel;
import models.ReportModel;
import play.data.Form;
import utils.TimeProvider;

import java.util.*;

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
        List<CompanySummary> rtn = new ArrayList<>();
        List<String> companiesHouseIdentifiers = companiesHouseCommunicator.RequestAuthorizedCompaniesForUser(oAuthToken);
        return reportsRepository.getCompanySummaries(companiesHouseIdentifiers);
    }

    public ReportFilingModel TryMakeReportFilingModel(String oAuthToken, String companiesHouseIdentifier) {
        if (!MayFileReport(oAuthToken, companiesHouseIdentifier)) {
            return null;
        }

        List<CompanySummary> matches = reportsRepository.getCompanySummaries(Collections.singletonList(companiesHouseIdentifier));
        return new ReportFilingModel(matches.get(0), timeProvider.Now().getTime());
    }

    public int TryFileReport(String bullshitToken, ReportFilingModel reportFilingModel) {
        if (!MayFileReport(bullshitToken, reportFilingModel.TargetCompanyCompaniesHouseIdentifier)) {
            return -1;
        }
        return reportsRepository.TryFileReport(reportFilingModel);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean MayFileReport(String oAuthToken, String companiesHouseIdentifier) {
        if (companiesHouseIdentifier == null || oAuthToken == null)
            return false;
        return companiesHouseCommunicator.RequestAuthorizedCompaniesForUser(oAuthToken).contains(companiesHouseIdentifier);
    }

}

