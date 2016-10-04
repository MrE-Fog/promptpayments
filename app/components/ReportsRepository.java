package components;

import com.google.inject.ImplementedBy;
import models.CompanyModel;
import models.CompanySummary;
import models.ReportFilingModel;
import models.ReportModel;

import java.util.List;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Orchestrates access to report data
 */
@ImplementedBy(JdbcReportsRepository.class)
public interface ReportsRepository {
    List<CompanySummary> searchCompanies(String company);
    CompanyModel getCompanyByCompaniesHouseIdentifier(String identifier);
    ReportModel getReport(String company, int reportId);
    List<CompanySummary> getCompanySummaries(List<String> companiesHouseIdentifiers);

    int TryFileReport(ReportFilingModel reportFilingModel);
}
