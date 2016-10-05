package components;

import models.CompanySummary;
import models.ReportFilingModel;
import models.ReportModel;
import org.junit.Before;
import org.junit.Test;
import scala.Option;
import utils.GregorianTimeProvider;
import utils.TimeProvider;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;

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

    @Before
    public void setUp() throws Exception {
        TimeProvider mock = mock(TimeProvider.class);
        when(mock.Now()).thenReturn(new GregorianCalendar(2016,10,1));
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
        assertEquals("Eigencode Ltd.",rfm.getTargetCompanyName());
        assertEquals("November 2016",rfm.FilingDate);
    }
    @Test
    public void tryMakeReportFilingModel_unauthorised() throws Exception {
        ReportFilingModel rfm = companyAccessAuthorizer.TryMakeReportFilingModel("faketoken", "120");
        assertEquals(null, rfm);
    }

    @Test
    public void tryFileReport() throws Exception {
        ReportFilingModel rfm = companyAccessAuthorizer.TryMakeReportFilingModel("faketoken", "122");
        rfm.NumberOne = new BigDecimal(1.0);
        rfm.NumberTwo = new BigDecimal(2.0);
        rfm.NumberThree = new BigDecimal(3.0);

        int identifier = companyAccessAuthorizer.TryFileReport("faketoken", rfm);
        Option<ReportModel> report = reportsRepository.getReport("122", identifier);

        assertTrue(report.nonEmpty());
    }

    @Test
    public void tryFileReport_Unauthorised() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel(new CompanySummary("Nicecorp", "120"), new GregorianTimeProvider().Now().getTime());

        int reportsCountBefore = reportsRepository.ExportData(9999).size();
        int identifier = companyAccessAuthorizer.TryFileReport("faketoken", rfm);
        int reportsCountAfter =reportsRepository.ExportData(9999).size();

        assertEquals(-1, identifier);
        assertEquals(reportsCountBefore, reportsCountAfter);
    }

}