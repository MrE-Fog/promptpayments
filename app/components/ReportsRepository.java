package components;

import com.google.inject.ImplementedBy;
import models.CompanyModel;
import models.CompanySummary;
import models.ReportModel;

import java.util.List;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Orchestrates access to report data
 */
@ImplementedBy(MockReportsRepository.class)
public interface ReportsRepository {
    List<CompanySummary> searchCompanies(String company);
    CompanyModel getCompanyByCompaniesHouseIdentifier(String identifier);
    ReportModel getReport(String company, int reportId);
}
