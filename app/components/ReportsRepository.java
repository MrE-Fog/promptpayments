package components;

import com.google.inject.ImplementedBy;
import models.*;
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
    CompanyModel getCompanyModel(CompanySummary companySummary, int page, int itemsPerPage);
    Option<ReportModel> getReport(String company, int reportId);

    int TryFileReport(ReportFilingModel rfm, CompanySummary company, Calendar filingDate);

    List<F.Tuple<CompanySummary, ReportModel>> ExportData(int months);

    PagedList<CompanySearchResult> getCompanySearchInfo(PagedList<CompanySummaryWithAddress> companySummaries);
}