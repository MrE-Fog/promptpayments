package orchestrators;

import components.*;
import models.*;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import scala.Option;
import utils.MockUtcTimeProvider;
import utils.ReportModelExamples;

import java.io.IOException;
import java.util.*;

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
    private GovUkNotifyEmailer emailer;

    private CompanyModel companyModel;
    private Calendar utcNow;

    @Before
    public void setUp() throws Exception {
        reportsRepository = mock(ReportsRepository.class);
        communicator = mock(CompaniesHouseCommunicator.class);
        emailer = mock(GovUkNotifyEmailer.class);

        utcNow = new GregorianCalendar();
        utcNow.setTimeZone(TimeZone.getTimeZone("UTC"));

        orchestrator = new FileReportOrchestrator(communicator, reportsRepository, emailer, new MockUtcTimeProvider(2016,10,1));

        companyModel = new CompanyModel(new CompanySummary("test", "122"), new PagedList<>(new ArrayList<>(), 50,0,25));
    }

    @Test
    public void findRegisteredCompanies() throws Exception {

        PagedList<CompanySummaryWithAddress> expected = new PagedList<>(new ArrayList<>(), 100, 2, 3);
        when(communicator.searchCompanies("1",2,3)).thenReturn(expected);

        OrchestratorResult<PagedList<CompanySummaryWithAddress>> result = orchestrator.findRegisteredCompanies("1", 2, 3);

        assertTrue(result.success());
        assertEquals(result.get(), expected);
    }

    @Test
    public void findRegisteredCompanies_whenThrows() throws Exception {

        when(communicator.searchCompanies("1",2,3)).thenThrow(IOException.class);

        OrchestratorResult<PagedList<CompanySummaryWithAddress>> result = orchestrator.findRegisteredCompanies("1", 2, 3);

        assertFalse(result.success());
    }

    @Test
    public void tryMakeReportFilingModel() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");

        //when(reportsRepository.mayFileForCompany("somestuff", "122")).thenReturn(true);
        when(communicator.getCompany("122")).thenReturn(companyModel.Info);
        
        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "122");

        verify(communicator, times(1)).getCompany("122");

        assertTrue(filingData.success());
        assertEquals("test",filingData.get().company.Name);
        assertEquals("122",filingData.get().company.CompaniesHouseIdentifier);
        assertEquals("November 2016",filingData.get().date.ToFriendlyString());
        assertEquals("122",filingData.get().model.getTargetCompanyCompaniesHouseIdentifier());
    }

    @Test
    public void tryMakeReportFilingModel_nocompany() throws Exception {
        when(communicator.getCompany("122")).thenReturn(null);
        
        OrchestratorResult<FilingData> filingData = orchestrator.tryMakeReportFilingModel("somestuff", "122");

        verify(communicator, times(1)).getCompany("122");

        assertFailureResponse(filingData);
    }

    @Test
    public void tryMakeReportFilingModel_newcompany() throws Exception {
        when(communicator.getCompany("1234")).thenReturn(new CompanySummary("New corp", "1234"));
        //when(reportsRepository.mayFileForCompany("data", "1234")).thenReturn(true);

        OrchestratorResult<FilingData> data = orchestrator.tryMakeReportFilingModel("data", "1234");
        assertTrue(data.success());
        assertEquals("New corp", data.get().company.Name);
        assertEquals("1234", data.get().model.getTargetCompanyCompaniesHouseIdentifier());
        assertEquals("1234", data.get().company.CompaniesHouseIdentifier);
    }

    @Test
    public void tryValidateReportFilingModel() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");
        when(communicator.getCompany("122")).thenReturn(companyModel.Info);

        OrchestratorResult<ValidatedFilingData> filingData = orchestrator.tryValidateReportFilingModel(rfm);

        verify(communicator,times(1)).getCompany("122");

        assertTrue(filingData.success());
        assertEquals(rfm, filingData.get().model);
        assertEquals("test", filingData.get().company.Name);
        assertEquals("November 2016", filingData.get().date.ToFriendlyString());
    }

    @Test
    public void tryValidateReportFilingModel_newcompany() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("1234");
        when(communicator.getCompany("1234")).thenReturn(new CompanySummary("Newcorp", "1234"));
        OrchestratorResult<ValidatedFilingData> filingData = orchestrator.tryValidateReportFilingModel(rfm);

        verify(communicator, times(1)).getCompany("1234");

        assertTrue(filingData.success());
        assertEquals("1234", filingData.get().model.getTargetCompanyCompaniesHouseIdentifier());
        assertEquals("Newcorp", filingData.get().company.Name);
    }
    @Test
    public void tryValidateReportFilingModel_nocompany() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");
        when(communicator.getCompany("122")).thenReturn(null);
        OrchestratorResult<ValidatedFilingData> filingData = orchestrator.tryValidateReportFilingModel(rfm);

        verify(communicator, times(1)).getCompany("122");

        assertFailureResponse(filingData);
    }

    @Test
    public void tryFileReport() throws Exception {
        when(communicator.getEmailAddress("somestuff")).thenReturn("foo@bar.com");
        when(emailer.sendConfirmationEmail(any(), any(), any(), any())).thenReturn(true);
        testReportFiling(companyModel.Info);
        verify(emailer, times(1)).sendConfirmationEmail(eq("foo@bar.com"), eq(companyModel.Info), any(), eq("42"));
    }

    @Test
    public void tryFileReport_newcompany() throws Exception {
        CompanySummary newCorp = new CompanySummary("new corp", "1234");

        when(communicator.getEmailAddress("somestuff")).thenReturn("foo@bar.com");
        testReportFiling(newCorp);
        verify(emailer, times(1)).sendConfirmationEmail(eq("foo@bar.com"), eq(newCorp), any(), eq("42"));
    }

    @Test
    public void tryFileReport_emailfail1() throws Exception {
        when(communicator.getEmailAddress("somestuff")).thenThrow(IOException.class);
        OrchestratorResult<FilingOutcome> result = testReportFiling(companyModel.Info);

        verify(emailer, times(0)).sendConfirmationEmail(any(),any(),any(),any());

        assertTrue(result.success());
        assertEquals(null, result.get().confirmationEmailRecipient);
    }

    @Test
    public void tryFileReport_emailfail2() throws Exception {
        when(communicator.getEmailAddress("somestuff")).thenReturn(null);
        OrchestratorResult<FilingOutcome> result = testReportFiling(companyModel.Info);

        verify(emailer, times(0)).sendConfirmationEmail(any(),any(),any(),any());

        assertTrue(result.success());
        assertEquals(null, result.get().confirmationEmailRecipient);
    }

    @Test
    public void tryFileReport_sendfail() throws Exception {
        when(communicator.getEmailAddress("somestuff")).thenReturn("foo@bar.com");
        when(emailer.sendConfirmationEmail(any(),any(),any(),any())).thenReturn(false);

        OrchestratorResult<FilingOutcome> result = testReportFiling(companyModel.Info);

        verify(emailer, times(1)).sendConfirmationEmail(eq("foo@bar.com"),eq(companyModel.Info),any(),any());

        assertTrue(result.success());
        assertEquals(null, result.get().confirmationEmailRecipient);
    }

    private OrchestratorResult<FilingOutcome> testReportFiling(CompanySummary newCorp) throws IOException {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget(newCorp.CompaniesHouseIdentifier);
        rfm.setStartDate_year("2001");
        rfm.setStartDate_month("1");
        rfm.setStartDate_day("1");

        rfm.setEndDate_year("2001");
        rfm.setEndDate_month("5");
        rfm.setEndDate_day("31");


        //when(reportsRepository.mayFileForCompany("somestuff", "1234")).thenReturn(true);
        when(communicator.getCompany(newCorp.CompaniesHouseIdentifier)).thenReturn(newCorp);

        when(reportsRepository.tryFileReport(eq(rfm), eq(newCorp), any())).thenReturn(new ReportSummary(42, utcNow, rfm.getStartDate(), rfm.getEndDate()));

        OrchestratorResult<FilingOutcome> filingData = orchestrator.tryFileReport("somestuff", rfm, x -> ""+x);

        verify(communicator, times(1)).getCompany(newCorp.CompaniesHouseIdentifier);
        verify(reportsRepository, times(1)).tryFileReport(eq(rfm), eq(newCorp), any());


        assertTrue(filingData.success());
        assertEquals(42, filingData.get().reportId);

        return filingData;
    }


    @Test
    public void tryFileReport_noCompany() throws Exception {
        ReportFilingModel rfm = ReportFilingModel.MakeEmptyModelForTarget("122");

        when(communicator.getCompany("122")).thenReturn(null);
        when(reportsRepository.tryFileReport(eq(rfm), any(), any())).thenReturn(null);

        OrchestratorResult<FilingOutcome> filingData = orchestrator.tryFileReport("somestuff", rfm, x-> ""+x);

        verify(communicator, times(1)).getCompany("122");
        verify(reportsRepository, times(0)).tryFileReport(any(), any(), any());
        verify(emailer, times(0)).sendConfirmationEmail(any(),any(),any(),any());

        assertFailureResponse(filingData);
        assertTrue(filingData.message().contains("Unknown"));
    }

    @Test
    public void getAuthorizationUri() throws Exception {
        when(communicator.getAuthorizationUri(any(), eq("123"))).thenReturn("expected-uri");
        assertEquals("expected-uri", orchestrator.getAuthorizationUri("123"));
    }

    @Test
    public void getAuthorizationUri_nulltest() throws Exception {
        when(communicator.getAuthorizationUri(any(), eq("123"))).thenThrow(IOException.class);
        assertNull(orchestrator.getAuthorizationUri("123"));
    }

    @Test
    public void tryAuthorise_success() throws Exception {
        when(communicator.verifyAuthCode(eq("code"), any(), eq("123"))).thenReturn("expected-cookie");
        OrchestratorResult<Http.Cookie> result = orchestrator.tryAuthorize("code", "123");
        assertTrue(result.success());
        assertEquals("expected-cookie", result.get().value());
    }

    @Test
    public void tryAuthorise_failure() throws Exception {
        when(communicator.verifyAuthCode(eq("code"), any(), eq("123"))).thenReturn(null);
        when(communicator.verifyAuthCode(eq("code"), any(), eq("456"))).thenThrow(IOException.class);
        OrchestratorResult<Http.Cookie> result1 = orchestrator.tryAuthorize("code", "123");
        OrchestratorResult<Http.Cookie> result2 = orchestrator.tryAuthorize("code", "456");

        assertFalse(result1.success());
        assertFalse(result2.success());
    }

    @Test
    public void trySearchCompanies_success() throws Exception {
        PagedList<CompanySummaryWithAddress> communicatorResponse = new PagedList<>(Arrays.asList(), 100, 0, 10);
        PagedList<CompanySearchResult> repoResponse = new PagedList<>(Arrays.asList(), 100, 0, 10);

        when(communicator.searchCompanies("123", 0, 10)).thenReturn(communicatorResponse);
        when(reportsRepository.getCompanySearchInfo(communicatorResponse)).thenReturn(repoResponse);

        OrchestratorResult<PagedList<CompanySearchResult>> result = orchestrator.trySearchCompanies("123", 0, 10);

        assertTrue(result.success());
        assertEquals(repoResponse, result.get());
    }

    @Test
    public void trySearchCompanies_fail() throws Exception {
        when(communicator.searchCompanies("123",0,10)).thenThrow(IOException.class);
        assertFailureResponse(orchestrator.trySearchCompanies("123",0,10));
    }

    @Test
    public void getCompanyModel_apifail() throws Exception {
        when(communicator.getCompany(any())).thenThrow(IOException.class);
        assertFailureResponse(orchestrator.getCompanyModel("123", 0, 10));
    }

    @Test
    public void getCompanyModel_noCompany() throws Exception {
        when(communicator.getCompany(any())).thenReturn(null);
        assertFailureResponse(orchestrator.getCompanyModel("123", 0, 10));
    }

    @Test
    public void getCompanyModel() throws Exception {
        CompanySummary company = new CompanySummary("testcorp", "123");
        when(communicator.getCompany(any())).thenReturn(company);
        PagedList<ReportSummary> reports = new PagedList<>(Lists.emptyList(), 0, 0, 10);
        when(reportsRepository.getReportSummaries("123", 0, 10)).thenReturn(reports);

        OrchestratorResult<CompanyModel> companyModel = orchestrator.getCompanyModel("123", 0, 10);

        assertTrue(companyModel.success());
        assertEquals(company, companyModel.get().Info);
        assertEquals(reports, companyModel.get().ReportSummaries);
    }

    @Test
    public void getReport_apifail() throws Exception {
        when(communicator.getCompany(any())).thenThrow(IOException.class);
        assertFailureResponse(orchestrator.getReport("123", 1));
    }

    @Test
    public void getReport_noCompany() throws Exception {
        when(communicator.getCompany(any())).thenReturn(null);
        assertFailureResponse(orchestrator.getReport("123", 1));
    }

    @Test
    public void getReport_noReport() throws Exception {
        when(communicator.getCompany("123")).thenReturn(new CompanySummary("testcorp", "123"));
        when(reportsRepository.getReport("123", 1)).thenReturn(Option.empty());
        assertFailureResponse(orchestrator.getReport("123", 1));
    }

    @Test
    public void getReport() throws Exception {
        CompanySummary company = new CompanySummary("testcorp", "123");
        when(communicator.getCompany("123")).thenReturn(company);
        ReportModel report = ReportModelExamples.makeReportModel(1, 2016, 0);
        when(reportsRepository.getReport("123", 1)).thenReturn(Option.apply(report));

        OrchestratorResult<CompanySummaryAndReport> result = orchestrator.getReport("123", 1);

        assertTrue(result.success());
        assertEquals(company, result.get().company);
        assertEquals(report, result.get().report);
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