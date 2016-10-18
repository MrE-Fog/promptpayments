package orchestrators;

import components.CompaniesHouseCommunicator;
import components.ReportsRepository;
import models.*;
import org.junit.Before;
import org.junit.Test;
import utils.MockUtcTimeProvider;
import components.PagedList;

import java.io.IOException;
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
    private CompaniesHouseCommunicator communicator;

    private CompanyModel companyModel;
    private CompanyModel noAuthorityCompanyModel;

    @Before
    public void setUp() throws Exception {
        reportsRepository = mock(ReportsRepository.class);
        communicator = mock(CompaniesHouseCommunicator.class);
        orchestrator = new FileReportOrchestrator(communicator, reportsRepository, new MockUtcTimeProvider(2016,10,1));

        companyModel = new CompanyModel(new CompanySummary("test", "122"), new PagedList<>(new ArrayList<>(), 50,0,25));
        noAuthorityCompanyModel = new CompanyModel(new CompanySummary("test", "120"), new PagedList<>(new ArrayList<>(), 50,0,25));
    }

    @Test
    public void findRegisteredCompanies() throws Exception {

        PagedList<CompanySummary> expected = new PagedList<>(new ArrayList<>(), 100, 2, 3);
        when(communicator.searchCompanies("1",2,3)).thenReturn(expected);

        OrchestratorResult<PagedList<CompanySummary>> result = orchestrator.findRegisteredCompanies("1", 2, 3);

        assertTrue(result.success());
        assertEquals(result.get(), expected);
    }

    @Test
    public void findRegisteredCompanies_whenThrows() throws Exception {

        PagedList<CompanySummary> expected = new PagedList<>(new ArrayList<>(), 100, 2, 3);
        when(communicator.searchCompanies("1",2,3)).thenThrow(IOException.class);

        OrchestratorResult<PagedList<CompanySummary>> result = orchestrator.findRegisteredCompanies("1", 2, 3);

        assertFalse(result.success());
    }

    @Test
    public void tryMakeReportFilingModel() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");

        when(communicator.mayFileForCompany("somestuff", "122")).thenReturn(true);
        when(communicator.tryGetCompany("122")).thenReturn(companyModel.Info);
        
        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "122");

        verify(communicator, times(1)).tryGetCompany("122");

        assertTrue(filingData.success());
        assertEquals("test",filingData.get().company.Name);
        assertEquals("122",filingData.get().company.CompaniesHouseIdentifier);
        assertEquals("November 2016",filingData.get().date.ToFriendlyString());
        assertEquals("122",filingData.get().model.getTargetCompanyCompaniesHouseIdentifier());
    }

    @Test
    public void tryMakeReportFilingModel_nocompany() throws Exception {
        when(communicator.tryGetCompany("122")).thenReturn(null);
        
        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "122");

        verify(communicator, times(1)).tryGetCompany("122");

        assertFailureResponse(filingData);
    }

    @Test
    public void tryMakeReportFilingModel_newcompany() throws Exception {
        when(communicator.tryGetCompany("1234")).thenReturn(new CompanySummary("New corp", "1234"));
        when(communicator.mayFileForCompany("data", "1234")).thenReturn(true);

        OrchestratorResult<FilingData> data = orchestrator.tryMakeReportFilingModel("data", "1234");
        assertTrue(data.success());
        assertEquals("New corp", data.get().company.Name);
        assertEquals("1234", data.get().model.getTargetCompanyCompaniesHouseIdentifier());
        assertEquals("1234", data.get().company.CompaniesHouseIdentifier);
    }

    @Test
    public void tryMakeReportFilingModel_noauthority() throws Exception {
        when(communicator.tryGetCompany("120")).thenReturn(noAuthorityCompanyModel.Info);
        when(communicator.mayFileForCompany("somestuff","120")).thenReturn(false);
        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "120");
        verify(communicator, times(1)).tryGetCompany("120");
        assertFailureResponse(filingData);
    }

    @Test
    public void tryValidateReportFilingModel() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");
        when(communicator.tryGetCompany("122")).thenReturn(companyModel.Info);

        OrchestratorResult<ValidatedFilingData> filingData = orchestrator.tryValidateReportFilingModel("somestuff", rfm);

        verify(communicator,times(1)).tryGetCompany("122");

        assertTrue(filingData.success());
        assertEquals(rfm, filingData.get().model);
        assertEquals("test", filingData.get().company.Name);
        assertEquals("November 2016", filingData.get().date.ToFriendlyString());
    }

    @Test
    public void tryValidateReportFilingModel_newcompany() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("1234");
        when(communicator.tryGetCompany("1234")).thenReturn(new CompanySummary("Newcorp", "1234"));
        OrchestratorResult<ValidatedFilingData> filingData = orchestrator.tryValidateReportFilingModel("somestuff", rfm);

        verify(communicator, times(1)).tryGetCompany("1234");

        assertTrue(filingData.success());
        assertEquals("1234", filingData.get().model.getTargetCompanyCompaniesHouseIdentifier());
        assertEquals("Newcorp", filingData.get().company.Name);
    }
    @Test
    public void tryValidateReportFilingModel_nocompany() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");
        when(communicator.tryGetCompany("122")).thenReturn(null);
        OrchestratorResult<ValidatedFilingData> filingData = orchestrator.tryValidateReportFilingModel("somestuff", rfm);

        verify(communicator, times(1)).tryGetCompany("122");

        assertFailureResponse(filingData);
    }

    @Test
    public void tryFileReport() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");

        when(communicator.mayFileForCompany("somestuff", "122")).thenReturn(true);
        when(communicator.tryGetCompany("122")).thenReturn(companyModel.Info);

        when(reportsRepository.TryFileReport(eq(rfm), eq(companyModel.Info), any())).thenReturn(42);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(communicator, times(1)).tryGetCompany("122");
        verify(reportsRepository, times(1)).TryFileReport(eq(rfm), eq(companyModel.Info), any());

        assertTrue(filingData.success());
        assertEquals(new Integer(42), filingData.get());
    }

    @Test
    public void tryFileReport_newcompany() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("1234");
        CompanySummary newCorp = new CompanySummary("New Corp", "1234");

        when(communicator.mayFileForCompany("somestuff", "1234")).thenReturn(true);
        when(communicator.tryGetCompany("1234")).thenReturn(newCorp);

        when(reportsRepository.TryFileReport(eq(rfm), eq(newCorp), any())).thenReturn(42);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(communicator, times(1)).tryGetCompany("1234");
        verify(reportsRepository, times(1)).TryFileReport(eq(rfm), eq(newCorp), any());

        assertTrue(filingData.success());
        assertEquals(new Integer(42), filingData.get());
    }

    @Test
    public void tryFileReport_noCompany() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");

        when(communicator.tryGetCompany("122")).thenReturn(null);
        when(reportsRepository.TryFileReport(eq(rfm), any(), any())).thenReturn(-1);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(communicator, times(1)).tryGetCompany("122");
        verify(reportsRepository, times(0)).TryFileReport(any(), any(), any());

        assertFailureResponse(filingData);
        assertTrue(filingData.message().contains("Unknown"));
    }

    @Test
    public void tryFileReport_noauthority() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("120");

        when(communicator.mayFileForCompany("somestuff", "120")).thenReturn(false);

        OrchestratorResult<Integer> filingData = orchestrator.tryFileReport("somestuff", rfm);

        verify(reportsRepository, times(0)).TryFileReport(any(), any(), any());

        assertFailureResponse(filingData);
    }

    private void assertFailureResponse(OrchestratorResult filingData) {
        assertFalse("Should be unsuccessful", filingData.success());
        assertTrue("Should have an error message", filingData.message().length() > 0);
        try {
            filingData.get();
        } catch (IllegalStateException e ) {
            return;
        }
        fail("get() should throw");
    }


}