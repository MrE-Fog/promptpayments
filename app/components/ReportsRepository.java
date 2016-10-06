package components;

import com.google.inject.ImplementedBy;
import models.CompanyModel;
import models.CompanySummary;
import models.ReportFilingModel;
import models.ReportModel;
import play.libs.F;
import scala.Option;

import java.util.Calendar;
import java.util.List;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Orchestrates access to report data
 */
@ImplementedBy(JdbcReportsRepository.class)
public interface ReportsRepository {
    PagedList<CompanySummary> searchCompanies(String company, int page, int itemsPerPage);
    Option<CompanyModel> getCompanyByCompaniesHouseIdentifier(String identifier, int page, int itemsPerPage);
    Option<ReportModel> getReport(String company, int reportId);
    List<CompanySummary> getCompanySummaries(List<String> companiesHouseIdentifiers);

    int TryFileReport(ReportFilingModel rfm, Calendar filingDate);

    List<F.Tuple<CompanySummary, ReportModel>> ExportData(int months);
}