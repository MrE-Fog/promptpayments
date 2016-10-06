package orchestrators;

import components.CompanyAccessAuthorizer;
import components.ReportsRepository;
import models.*;
import org.junit.Before;
import org.junit.Test;
import scala.Option;
import utils.MockUtcTimeProvider;
import components.PagedList;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by daniel.rothig on 06/10/2016.
 *
 * Bunch of mock-tests for FileReportOrchestratorTest
 */
public class FileReportOrchestratorTest {
    private ReportsRepository reportsRepository;
    private FileReportOrchestrator orchestrator;
    private CompanyAccessAuthorizer authorizer;

    private CompanyModel companyModel;

    @Before
    public void setUp() throws Exception {
        reportsRepository = mock(ReportsRepository.class);
        authorizer = mock(CompanyAccessAuthorizer.class);
        orchestrator = new FileReportOrchestrator(authorizer, reportsRepository, new MockUtcTimeProvider(2016,10,1));

        companyModel = new CompanyModel(new CompanySummary("test", "123"), new PagedList<>(new ArrayList<>(), 50,0,25));
    }

    @Test
    public void getCompaniesForUser() throws Exception {
        orchestrator.getCompaniesForUser("somestuff");
        verify(authorizer, times(1)).GetCompaniesForUser("somestuff");
    }

    @Test
    public void tryMakeReportFilingModel() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("123");

        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("123", 0, 0)).thenReturn(Option.apply(companyModel));
        when(authorizer.TryMakeReportFilingModel("somestuff", "123")).thenReturn(rfm);

        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "123");

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("123", 0, 0);
        verify(authorizer, times(1)).TryMakeReportFilingModel("somestuff", "123");

        assertTrue(filingData.success());
        assertEquals("test",filingData.get().company.Name);
        assertEquals("123",filingData.get().company.CompaniesHouseIdentifier);
        assertEquals("November 2016",filingData.get().date.ToFriendlyString());
        assertEquals("123",filingData.get().model.getTargetCompanyCompaniesHouseIdentifier());
    }

    @Test
    public void tryMakeReportFilingModel_nocompany() throws Exception {
        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("123", 0, 0)).thenReturn(Option.empty());
        when(authorizer.TryMakeReportFilingModel("somestuff", "123")).thenReturn(null);

        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "123");

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("123", 0, 0);

        assertFalse(filingData.success());
    }

    @Test
    public void tryMakeReportFilingModel_noauthority() throws Exception {
        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("123", 0, 0)).thenReturn(Option.apply(companyModel));
        when(authorizer.TryMakeReportFilingModel("somestuff", "123")).thenReturn(null);

        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "123");

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("123", 0, 0);
        verify(authorizer, times(1)).TryMakeReportFilingModel("somestuff", "123");

        assertFalse(filingData.success());
    }

    @Test
    public void tryValidateReportFilingModel() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("123");
        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("123", 0, 0)).thenReturn(Option.apply(companyModel));

        OrchestratorResult<FilingData> filingData = orchestrator.tryValidateReportFilingModel("somestuff", rfm);

        verify(reportsRepository,times(1)).getCompanyByCompaniesHouseIdentifier("123", 0, 0);

        assertTrue(filingData.success());
        assertEquals(rfm, filingData.get().model);
        assertEquals("test", filingData.get().company.Name);
        assertEquals("November 2016", filingData.get().date.ToFriendlyString());
    }

    @Test
    public void tryValidateReportFilingModel_nocompany() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("123");
        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("123", 0, 0)).thenReturn(Option.empty());
        OrchestratorResult<FilingData> filingData = orchestrator.tryValidateReportFilingModel("somestuff", rfm);

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("123", 0, 0);

        assertFalse(filingData.success());
    }

    @Test
    public void tryFileReport() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("123");

        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("123", 0, 0)).thenReturn(Option.apply(companyModel));
        when(authorizer.TryFileReport("somestuff", rfm)).thenReturn(42);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("123", 0, 0);
        verify(authorizer, times(1)).TryFileReport("somestuff", rfm);

        assertTrue(filingData.success());
        assertEquals(new Integer(42), filingData.get());
    }

    @Test
    public void tryFileReport_noCompany() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("123");

        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("123", 0, 0)).thenReturn(Option.empty());
        when(authorizer.TryFileReport("somestuff", rfm)).thenReturn(42);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("123", 0, 0);
        verify(authorizer, times(0)).TryFileReport("somestuff", rfm);

        assertFalse(filingData.success());
    }

    @Test
    public void tryFileReport_noauthority() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("123");

        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("123", 0, 0)).thenReturn(Option.apply(companyModel));
        when(authorizer.TryFileReport("somestuff", rfm)).thenReturn(-1);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("123", 0, 0);
        verify(authorizer, times(1)).TryFileReport("somestuff", rfm);

        assertFalse(filingData.success());
    }

}