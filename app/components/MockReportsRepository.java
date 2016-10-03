package components;

import models.CompanyModel;
import models.CompanySummary;
import models.ReportModel;
import models.ReportSummary;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Repository without data backend. Utilised for development and test.
 */
final class MockReportsRepository implements ReportsRepository {


    @Override
    public List<CompanySummary> searchCompanies(String company) {


        ArrayList<CompanySummary> results = new ArrayList<>();
        results.add(new CompanySummary(company+"1", "1234567"));
        results.add(new CompanySummary(company+"2", "1234567"));
        results.add(new CompanySummary(company+"3", "1234567"));

        return results;
    }

    @Override
    public CompanyModel getCompanyByCompaniesHouseIdentifier(String identifier) {
        ArrayList<ReportSummary> reports = new ArrayList<>();
        reports.add(new ReportSummary(3, new GregorianCalendar(2016, 5, 1).getTime()));
        reports.add(new ReportSummary(2, new GregorianCalendar(2015, 12, 1).getTime()));
        reports.add(new ReportSummary(1, new GregorianCalendar(2015, 5, 1).getTime()));

        return new CompanyModel(
                new CompanySummary("SomeCompany", identifier),
                reports
        );
    }

    @Override
    public ReportModel getReport(String company, int reportId) {
        return new ReportModel(
                new ReportSummary(1, new GregorianCalendar(2016,5,1).getTime()),
                new CompanySummary("SomeCompany", company));
    }
}

