package components;

import com.google.inject.Inject;
import models.CompanySummary;
import models.ReportFilingModel;
import models.ReportModel;
import play.data.Form;

import java.util.*;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Determines which companies a user may file for
 */
public class CompanyAccessAuthorizer {
    @Inject
    private ReportsRepository reportsRepository;

    @Inject
    private MockCompaniesHouseCommunicator companiesHouseCommunicator;

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
        return new ReportFilingModel(matches.get(0), new Date());
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

