package orchestrators;

import components.MockCompaniesHouseCommunicator;
import components.ReportsRepository;
import models.*;
import org.junit.Before;
import org.junit.Test;
import scala.Option;
import utils.MockUtcTimeProvider;
import components.PagedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    private MockCompaniesHouseCommunicator communicator;

    private CompanyModel companyModel;
    private CompanyModel noAuthorityCompanyModel;

    @Before
    public void setUp() throws Exception {
        reportsRepository = mock(ReportsRepository.class);
        communicator = new MockCompaniesHouseCommunicator();
        orchestrator = new FileReportOrchestrator(communicator, reportsRepository, new MockUtcTimeProvider(2016,10,1));

        companyModel = new CompanyModel(new CompanySummary("test", "122"), new PagedList<>(new ArrayList<>(), 50,0,25));
        noAuthorityCompanyModel = new CompanyModel(new CompanySummary("test", "120"), new PagedList<>(new ArrayList<>(), 50,0,25));
    }

    @Test
    public void getCompaniesForUser() throws Exception {
        PagedList<CompanySummary> rtn = new PagedList<>(new ArrayList<>(), 0, 0, 0);

        when(reportsRepository.getCompanySummaries(Collections.singletonList("122"), 2, 25)).thenReturn(rtn);

        OrchestratorResult<PagedList<CompanySummary>> result = orchestrator.getCompaniesForUser("somestuff", 2, 25);

        verify(reportsRepository, times(1)).getCompanySummaries(Collections.singletonList("122"), 2, 25);
        assertTrue(result.success());
        assertEquals(rtn, result.get());
    }

    @Test
    public void tryMakeReportFilingModel() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("122");

        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("122", 0, 0)).thenReturn(Option.apply(companyModel));
        
        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "122");

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("122", 0, 0);

        assertTrue(filingData.success());
        assertEquals("test",filingData.get().company.Name);
        assertEquals("122",filingData.get().company.CompaniesHouseIdentifier);
        assertEquals("November 2016",filingData.get().date.ToFriendlyString());
        assertEquals("122",filingData.get().model.getTargetCompanyCompaniesHouseIdentifier());
    }

    @Test
    public void tryMakeReportFilingModel_nocompany() throws Exception {
        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("122", 0, 0)).thenReturn(Option.empty());
        
        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "122");

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("122", 0, 0);

        assertFalse(filingData.success());
    }

    @Test
    public void tryMakeReportFilingModel_noauthority() throws Exception {
        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("120", 0, 0)).thenReturn(Option.apply(noAuthorityCompanyModel));
        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "120");
        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("120", 0, 0);
        assertFalse(filingData.success());
    }

    @Test
    public void tryValidateReportFilingModel() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("122");
        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("122", 0, 0)).thenReturn(Option.apply(companyModel));

        OrchestratorResult<FilingData> filingData = orchestrator.tryValidateReportFilingModel("somestuff", rfm);

        verify(reportsRepository,times(1)).getCompanyByCompaniesHouseIdentifier("122", 0, 0);

        assertTrue(filingData.success());
        assertEquals(rfm, filingData.get().model);
        assertEquals("test", filingData.get().company.Name);
        assertEquals("November 2016", filingData.get().date.ToFriendlyString());
    }

    @Test
    public void tryValidateReportFilingModel_nocompany() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("122");
        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("122", 0, 0)).thenReturn(Option.empty());
        OrchestratorResult<FilingData> filingData = orchestrator.tryValidateReportFilingModel("somestuff", rfm);

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("122", 0, 0);

        assertFalse(filingData.success());
    }

    @Test
    public void tryFileReport() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("122");

        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("122", 0, 0)).thenReturn(Option.apply(companyModel));
        when(reportsRepository.TryFileReport(eq(rfm), any())).thenReturn(42);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("122", 0, 0);
        verify(reportsRepository, times(1)).TryFileReport(eq(rfm), any());

        assertTrue(filingData.success());
        assertEquals(new Integer(42), filingData.get());
    }

    @Test
    public void tryFileReport_noCompany() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("122");

        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("122", 0, 0)).thenReturn(Option.empty());
        when(reportsRepository.TryFileReport(eq(rfm), any())).thenReturn(-1);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("122", 0, 0);
        verify(reportsRepository, times(0)).TryFileReport(any(), any());

        assertFalse(filingData.success());
    }

    @Test
    public void tryFileReport_noauthority() throws Exception {
        ReportFilingModel rfm = new ReportFilingModel();
        rfm.setTargetCompanyCompaniesHouseIdentifier("120");

        when(reportsRepository.getCompanyByCompaniesHouseIdentifier("120", 0, 0)).thenReturn(Option.apply(noAuthorityCompanyModel));

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(reportsRepository, times(1)).getCompanyByCompaniesHouseIdentifier("120", 0, 0);
        verify(reportsRepository, times(0)).TryFileReport(any(), any());

        assertFalse(filingData.success());
    }

}