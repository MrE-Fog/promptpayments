package components;

import models.CompanyModel;
import models.CompanySummary;
import models.ReportFilingModel;
import models.ReportModel;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.libs.F;
import utils.TimeProvider;
import views.html.Reports.company;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class JdbcReportsRepositoryTest {

    private JdbcReportsRepository jdbcReportsRepository;
    @Before
    public void setUp() throws Exception {
        TimeProvider timeProvider = mock(TimeProvider.class);
        when(timeProvider.Now()).thenReturn(new GregorianCalendar(2016, 10, 1));

        MockRepositoryCreator.CreateMockReportsRepository(timeProvider);
    }

    @Test
    public void searchCompanies_CaseInsenstivie() throws Exception {

        List<CompanySummary> result1 = jdbcReportsRepository.searchCompanies("cookies");
        List<CompanySummary> result2 = jdbcReportsRepository.searchCompanies("CoOkIeS");

        assertTrue(result1.size() == 1);
        assertTrue(result2.size() == 1);
        //List<CompanySummary> result1 = jdbcReportsRepository.searchCompanies("co");
        //Capture t = captureExecution(repo -> repo.searchCompanies("matchstring"), new ArrayList<>());

        //assertTrue("Should contain '%matchstring%': " + t.params[0], "%matchstring%".equals(t.params[0]));
        //assertTrue("Should perform case-insensitive comparison': " + t.sql, t.sql.contains("LOWER(Name) LIKE LOWER(?)"));
        //assertTrue("Should sort alphabetically': " + t.sql, t.sql.contains("ORDER BY Name"));
    }

    @Test
    public void searchCompanies_TruncatesSurroundingWhitespace() throws Exception {
        List<CompanySummary> result1 = jdbcReportsRepository.searchCompanies("    Cookies");
        List<CompanySummary> result2 = jdbcReportsRepository.searchCompanies("\tCookies\t");

        assertTrue(result1.size() == 1);
        assertTrue(result2.size() == 1);
    }
    @Test
    public void searchCompanies_SearchesWithinString() throws Exception {
        List<CompanySummary> result = jdbcReportsRepository.searchCompanies("   co");
        assertTrue(result.size() == 3);

        List<String> names = result.stream().map(x -> x.Name).collect(Collectors.toList());
        assertTrue(names.contains("Nicecorp"));
        assertTrue(names.contains("Eigencode Ltd."));
        assertTrue(names.contains("Cookies Ltd."));
    }

    @Test
    public void searchCompanies_Alphabetic() throws Exception {
        List<CompanySummary> result = jdbcReportsRepository.searchCompanies("   co");
        List<CompanySummary> sorted = new ArrayList<>(result);
        sorted.sort((a, b) -> a.Name.compareTo(b.Name));

        assertEquals(result.get(0), sorted.get(0));
        assertEquals(result.get(1), sorted.get(1));
        assertEquals(result.get(2), sorted.get(2));
    }


    @Test
    public void getCompanyByCompaniesHouseIdentifier() throws Exception {
        CompanyModel company = jdbcReportsRepository.getCompanyByCompaniesHouseIdentifier("122").get();

        assertEquals("Eigencode Ltd.", company.Info.Name);
        assertEquals(1, company.ReportSummaries.size());
        assertEquals("May 2016", company.ReportSummaries.get(0).UiDateString());
    }

    @Test
    public void getCompanyByCompaniesHouseIdentifier_DoesntExist() throws Exception {
        assertTrue(jdbcReportsRepository.getCompanyByCompaniesHouseIdentifier("124").isEmpty());
    }


    @Test
    public void getReport() throws Exception {
        ReportModel report = jdbcReportsRepository.getReport("120", 1).get();

        assertEquals("February 2010", report.Info.UiDateString());
        assertEquals(1, report.Info.Identifier);
    }

    @Test
    public void getReport_DoesntExist() throws Exception {
        assertTrue(jdbcReportsRepository.getReport("124", 10).isEmpty());
    }

    @Test
    public void getCompanySummaries() throws Exception {
        List<String> identifiers = new ArrayList<>();
        identifiers.add("120");
        identifiers.add("122");
        identifiers.add("124"); //doesn't exist

        List<CompanySummary> companySummaries = jdbcReportsRepository.getCompanySummaries(identifiers);
        List<String> resultIdentifiers = companySummaries.stream().map(x -> x.CompaniesHouseIdentifier).collect(Collectors.toList());

        assertEquals(2, companySummaries.size());
        assertTrue(resultIdentifiers.contains("120"));
        assertTrue(resultIdentifiers.contains("122"));
    }

    @Test
    public void tryFileReport() throws Exception {

        ReportFilingModel rfm = new ReportFilingModel(new CompanySummary("Eigencode Ltd.", "122"), new GregorianCalendar().getTime());
        rfm.NumberOne = new BigDecimal("1.0");
        rfm.NumberTwo = new BigDecimal("2.0");
        rfm.NumberThree = new BigDecimal("3.0");

        String expectedUiDateString = rfm.FilingDateUiString();
        int result = jdbcReportsRepository.TryFileReport(rfm);

        ReportModel report = jdbcReportsRepository.getReport("122", result).get();

        assertEquals(report.Info.Identifier, result);
        assertEquals(report.Info.UiDateString(), expectedUiDateString);
        assertEquals(report.NumberOne, rfm.NumberOne);
        assertEquals(report.NumberTwo, rfm.NumberTwo);
        assertEquals(report.NumberThree, rfm.NumberThree);
    }

    @Test
    public void tryFileReport_WhenCompanyUnknown() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel(new CompanySummary("Doesnt exist Ltd.", "124"), new GregorianCalendar().getTime());
        int result = jdbcReportsRepository.TryFileReport(rfm);
        assertEquals(-1 , result);
    }

    @Test
    public void exportData() throws Exception {
        List<F.Tuple<CompanySummary, ReportModel>> data = jdbcReportsRepository.ExportData(24);

        assertEquals("Should return all reports except oldest one: " + data.size(), 6, data.size());
    }
}

class MockRepositoryCreator {
    static ReportsRepository CreateMockReportsRepository(TimeProvider timeProvider) throws SQLException {
        Database testDb = Databases.inMemory("test", "jdbc:h2:mem:playtest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", new HashMap<>());

        JdbcReportsRepository jdbcReportsRepository = new JdbcReportsRepository(new JdbcCommunicator(testDb), timeProvider);

        String fakeData =
                "DELETE FROM Report;\n" +
                        "DELETE FROM Company;\n" +
                        "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Nicecorp', '120');\n" +
                        "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Cookies Ltd.', '121');\n" +
                        "INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Eigencode Ltd.', '122');\n" +
                        "\n" +
                        "INSERT INTO Report(Identifier, CompaniesHouseIdentifier, FilingDate) VALUES (1, '120', '2010-02-01');\n" +
                        "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2015-02-01');\n" +
                        "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2015-08-01');\n" +
                        "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2016-02-01');\n" +
                        "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('121', '2015-07-01');\n" +
                        "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('121', '2016-01-01');\n" +
                        "INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('122', '2016-05-01');";
        testDb.getConnection().createStatement().execute(fakeData);

        return jdbcReportsRepository;
    }
}