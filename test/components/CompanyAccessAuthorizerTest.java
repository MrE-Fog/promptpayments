package components;

import models.CompanySummary;
import models.ReportFilingModel;
import models.ReportModel;
import org.junit.Before;
import org.junit.Test;
import scala.Option;
import utils.MockUtcTimeProvider;
import utils.TimeProvider;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by daniel.rothig on 05/10/2016.
 *
 *
 */
public class CompanyAccessAuthorizerTest {

    private CompanyAccessAuthorizer companyAccessAuthorizer;
    private ReportsRepository reportsRepository;

    private Calendar now = new MockUtcTimeProvider(2016,10,1).Now();

    @Before
    public void setUp() throws Exception {
        TimeProvider mock = mock(TimeProvider.class);
        when(mock.Now()).thenReturn(now);
        reportsRepository = MockRepositoryCreator.CreateMockReportsRepository(mock);
        companyAccessAuthorizer = new CompanyAccessAuthorizer(
                reportsRepository,
                new MockCompaniesHouseCommunicator(),
                mock);
    }

    @Test
    public void getCompaniesForUser() throws Exception {
        List<CompanySummary> companies = companyAccessAuthorizer.GetCompaniesForUser("faketoken");
        assertEquals(1, companies.size());
        assertEquals("Eigencode Ltd.", companies.get(0).Name);
    }

    @Test
    public void tryMakeReportFilingModel() throws Exception {
        ReportFilingModel rfm = companyAccessAuthorizer.TryMakeReportFilingModel("faketoken", "122");
        assertEquals("122",rfm.getTargetCompanyCompaniesHouseIdentifier());
    }
    @Test
    public void tryMakeReportFilingModel_unauthorised() throws Exception {
        ReportFilingModel rfm = companyAccessAuthorizer.TryMakeReportFilingModel("faketoken", "120");
        assertEquals(null, rfm);
    }

    @Test
    public void tryFileReport() throws Exception {
        ReportFilingModel rfm = companyAccessAuthorizer.TryMakeReportFilingModel("faketoken", "122");
        rfm.setNumberOne(1);
        rfm.setNumberTwo(2);
        rfm.setNumberThree(3);

        int identifier = companyAccessAuthorizer.TryFileReport("faketoken", rfm);
        Option<ReportModel> report = reportsRepository.getReport("122", identifier);

        assertTrue(report.nonEmpty());
        assertEquals(TimeZone.getTimeZone("UTC"), report.get().Info.ExactDate().getTimeZone());
        assertEquals(now, report.get().Info.ExactDate());
    }

    @Test
    public void tryFileReport_Unauthorised() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("120");

        int reportsCountBefore = reportsRepository.ExportData(9999).size();
        int identifier = companyAccessAuthorizer.TryFileReport("faketoken", rfm);
        int reportsCountAfter = reportsRepository.ExportData(9999).size();

        assertEquals(-1, identifier);
        assertEquals(reportsCountBefore, reportsCountAfter);
    }

}