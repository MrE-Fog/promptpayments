package components;

import models.*;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.F;
import scala.Option;
import utils.MockUtcTimeProvider;
import utils.ReportModelExamples;
import utils.TimeProvider;
import utils.UtcTimeProvider;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JdbcReportsRepositoryTest {

    private ReportsRepository jdbcReportsRepository;
    private MockRepositoryCreator mockRepositoryCreator;

    public JdbcReportsRepositoryTest() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        TimeProvider timeProvider = new MockUtcTimeProvider(2016,10,1);
        mockRepositoryCreator = new MockRepositoryCreator(timeProvider);
        jdbcReportsRepository = mockRepositoryCreator.getMockRepository();
    }

    @After
    public void tearDown() throws Exception {
        mockRepositoryCreator.shutdown();
    }

    @Test
    public void searchCompanies_CaseInsenstivie() throws Exception {

        List<CompanySummary> result1 = jdbcReportsRepository.searchCompanies("cookies", 0, 25);
        List<CompanySummary> result2 = jdbcReportsRepository.searchCompanies("CoOkIeS", 0, 25);

        assertTrue(result1.size() == 1);
        assertTrue(result2.size() == 1);
    }

    @Test
    public void searchCOmpanies_ByNumber() throws Exception {
        PagedList<CompanySummary> result = jdbcReportsRepository.searchCompanies(" 120\t", 0, 25);

        assertTrue(result.size() == 1);
        assertTrue(result.totalSize() == 1);
    }

    @Test
    public void searchCompanies_TruncatesSurroundingWhitespace() throws Exception {
        List<CompanySummary> result1 = jdbcReportsRepository.searchCompanies("    Cookies",0, 25);
        List<CompanySummary> result2 = jdbcReportsRepository.searchCompanies("\tCookies\t",0, 25);

        assertTrue(result1.size() == 1);
        assertTrue(result2.size() == 1);
    }
    @Test
    public void searchCompanies_SearchesWithinString() throws Exception {
        List<CompanySummary> result = jdbcReportsRepository.searchCompanies("   co",0,25);
        assertTrue(result.size() == 3);

        List<String> names = result.stream().map(x -> x.Name).collect(Collectors.toList());
        assertTrue(names.contains("Nicecorp"));
        assertTrue(names.contains("Eigencode Ltd."));
        assertTrue(names.contains("Cookies Ltd."));
    }

    @Test
    public void searchCompanies_Alphabetic() throws Exception {
        List<CompanySummary> result = jdbcReportsRepository.searchCompanies("   co",0,25);
        List<CompanySummary> sorted = new ArrayList<>(result);
        sorted.sort((a, b) -> a.Name.compareTo(b.Name));

        assertEquals(result.get(0), sorted.get(0));
        assertEquals(result.get(1), sorted.get(1));
        assertEquals(result.get(2), sorted.get(2));
    }

    @Test
    public void searchCompanies_paged() throws Exception {
        PagedList<CompanySummary> result = jdbcReportsRepository.searchCompanies("   co",0,2);
        PagedList<CompanySummary> result2 = jdbcReportsRepository.searchCompanies("   co",1,2);

        assertEquals("The number of results should not exceed page size", 2, result.size());
        assertEquals("Page number should be accurately reported", 0, result.pageNumber());
        assertEquals("Total size should be accurately reported", 3, result.totalSize());
        assertEquals("Lower bound should be accurately reported", false, result.canGoBack());
        assertEquals("Upper bound should be accurately reported", true, result.canGoNext());

        assertEquals("The number of results should not exceed page size", 1, result2.size());
        assertEquals("Page number should be accurately reported", 1, result2.pageNumber());
        assertEquals("Total size should be accurately reported", 3, result2.totalSize());
        assertEquals("Lower bound should be accurately reported", true, result2.canGoBack());
        assertEquals("Upper bound should be accurately reported", false, result2.canGoNext());
    }

    @Test
    public void searchCompanies_paged_alphabetic() throws Exception {
        PagedList<CompanySummary> result1 = jdbcReportsRepository.searchCompanies("   co",0,2);
        PagedList<CompanySummary> result2 = jdbcReportsRepository.searchCompanies("   co",1,2);

        assertTrue(result1.get(0).Name.compareTo(result1.get(1).Name) <0);
        assertTrue(result1.get(1).Name.compareTo(result2.get(0).Name) <0);
    }

    @Test
    public void searchCompanies_emptyforzeropagesize() throws Exception {
        PagedList<CompanySummary> result = jdbcReportsRepository.searchCompanies("   co",0,0);
        assertEquals(0, result.size());
        assertEquals(3, result.totalSize());
        assertEquals(false, result.canGoBack());
        assertEquals(false, result.canGoNext());
        assertEquals(false, result.canPage());
    }

    @Test
    public void getCompany_paged() throws Exception {
        PagedList<ReportSummary> result = jdbcReportsRepository.getReportSummaries("120", 0, 3);
        PagedList<ReportSummary> result2 = jdbcReportsRepository.getReportSummaries("120", 1, 3);

        assertEquals("The number of results should not exceed page size", 3, result.size());
        assertEquals("Page number should be accurately reported", 0, result.pageNumber());
        assertEquals("Total size should be accurately reported", 4, result.totalSize());
        assertEquals("CanGoBack should be accurately reported", false, result.canGoBack());
        assertEquals("CanGoNext should be accurately reported", true, result.canGoNext());
        assertEquals("CanPage should be accurately reported", true, result.canPage());

        assertEquals("The number of results should not exceed page size", 1, result2.size());
        assertEquals("Page number should be accurately reported", 1, result2.pageNumber());
        assertEquals("Total size should be accurately reported", 4, result2.totalSize());
        assertEquals("CanGoBack should be accurately reported", true, result2.canGoBack());
        assertEquals("CanGoNext should be accurately reported", false, result2.canGoNext());
        assertEquals("CanPage should be accurately reported", true, result2.canPage());
    }

    @Test
    public void getCompany_paged_chronological() throws Exception {
        PagedList<ReportSummary> result1 = jdbcReportsRepository.getReportSummaries("120", 0, 3);
        PagedList<ReportSummary> result2 = jdbcReportsRepository.getReportSummaries("120", 1, 3);

        assertTrue(result1.get(0).getFilingDate().compareTo(result1.get(1).getFilingDate()) > 0);
        assertTrue(result1.get(1).getFilingDate().compareTo(result1.get(2).getFilingDate()) > 0);
        assertTrue(result1.get(2).getFilingDate().compareTo(result2.get(0).getFilingDate()) > 0);
    }

    @Test
    public void getCompany_emptyforzeropagesize() throws Exception {
        PagedList<ReportSummary> result = jdbcReportsRepository.getReportSummaries("120", 0, 0);
        assertEquals(0, result.size());
        assertEquals(4, result.totalSize());
        assertEquals(false, result.canGoBack());
        assertEquals(false, result.canGoNext());
        assertEquals(false, result.canPage());
    }

    @Test
    public void getCompanyByCompaniesHouseIdentifier() throws Exception {
        PagedList<ReportSummary> reportSummaries = jdbcReportsRepository.getReportSummaries("122", 0, 25);

        assertEquals(1, reportSummaries.size());
        assertEquals("1 May 2016", reportSummaries.get(0).UiDateString());
    }

    @Test
    public void getCompanyByCompaniesHouseIdentifier_ReportsChronological() throws Exception {
        PagedList<ReportSummary> reportSummaries = jdbcReportsRepository.getReportSummaries("120", 0, 25);

        assertEquals(4, reportSummaries.size());

        assertTrue(reportSummaries.get(0).getFilingDate().compareTo(reportSummaries.get(1).getFilingDate()) > 0);
        assertTrue(reportSummaries.get(1).getFilingDate().compareTo(reportSummaries.get(2).getFilingDate()) > 0);
        assertTrue(reportSummaries.get(2).getFilingDate().compareTo(reportSummaries.get(3).getFilingDate()) > 0);
    }

    @Test
    public void getCompanyByCompaniesHouseIdentifier_DoesntExist() throws Exception {
        assertTrue(jdbcReportsRepository.getReportSummaries("124", 0,25).isEmpty());
    }


    @Test
    public void getReport() throws Exception {
        ReportModel report = jdbcReportsRepository.getReport("120", 333).get();

        MockUtcTimeProvider expectedStartDate = new MockUtcTimeProvider(2016, 0, 1);
        MockUtcTimeProvider expectedEndDate = new MockUtcTimeProvider(2016, 4, 31);

        assertEquals("Ensure that ALL fields are tested below", 22 ,ReportModel.class.getDeclaredFields().length);

        assertEquals("1 February 2010", report.Info.UiDateString());
        assertEquals("1 January 2016", report.Info.StartDateString());
        assertEquals("31 May 2016", report.Info.EndDateString());
        assertEquals(333, report.Info.Identifier);
        assertEquals(new BigDecimal("31"), report.AverageTimeToPay);
        assertEquals(new BigDecimal("10"), report.PercentInvoicesPaidBeyondAgreedTerms);
        assertEquals(new BigDecimal("80"), report.PercentInvoicesWithin30Days);
        assertEquals(new BigDecimal("15"), report.PercentInvoicesWithin60Days);
        assertEquals(new BigDecimal( "5"), report.PercentInvoicesBeyond60Days);
        assertEquals(new UiDate(expectedStartDate.Now()).ToDateString(), new UiDate(report.StartDate).ToDateString());
        assertEquals(new UiDate(expectedEndDate.Now()).ToDateString(), new UiDate(report.EndDate).ToDateString());
        assertEquals("User-specified payment terms", report.PaymentTerms);
        assertEquals("User-specified maximum contract length", report.MaximumContractPeriod);
        assertEquals(true, report.PaymentTermsChanged);
        assertEquals("User-specified payment terms change", report.PaymentTermsChangedComment);
        assertEquals(true, report.PaymentTermsChangedNotified);
        assertEquals("User-specified notification comment", report.PaymentTermsChangedNotifiedComment);
        assertEquals("User-specified payment terms comment", report.PaymentTermsComment);
        assertEquals("User-specified dispute resolution", report.DisputeResolution);
        assertEquals(true, report.OfferEInvoicing);
        assertEquals(true, report.OfferSupplyChainFinance);
        assertEquals(false, report.RetentionChargesInPolicy);
        assertEquals(false, report.RetentionChargesInPast);
        assertEquals(true, report.HasPaymentCodes);
        assertEquals("Prompt Payment Code", report.PaymentCodes);
    }

    @Test
    public void getReport_DoesntExist() throws Exception {
        assertTrue(jdbcReportsRepository.getReport("124", 10).isEmpty());
    }

    @Test
    public void tryFileReport_withNoUtc() throws Exception {
        try {
            Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            gmt.set(2016,9,1);

            jdbcReportsRepository.tryFileReport(ReportModelExamples.makeFullReportFilingModel("120"), new CompanySummary("Some Company", "122"), gmt);
        } catch (InvalidParameterException e) {
            return;
        }
        fail();
    }
    @Test
    public void tryFileReport() throws Exception {
        Calendar time = new UtcTimeProvider().Now();
        ReportFilingModel rfm = ReportModelExamples.makeFullReportFilingModel("122");

        ReportSummary reportSummary = jdbcReportsRepository.tryFileReport(rfm, new CompanySummary("Some Company", "122"), time);

        assertNotNull(reportSummary);
        assertTrue(reportSummary.Identifier > 0);

        ReportModel report = jdbcReportsRepository.getReport("122", reportSummary.Identifier).get();

        assertEquals("Ensure that ALL fields are tested below", 22,ReportModel.class.getDeclaredFields().length);

        assertEquals(report.Info.Identifier, reportSummary.Identifier);
        assertEquals(time, report.Info.getFilingDate());

        assertEquals(new BigDecimal("31"), report.AverageTimeToPay);
        assertEquals(new BigDecimal("10"), report.PercentInvoicesPaidBeyondAgreedTerms);
        assertEquals(new BigDecimal("80"), report.PercentInvoicesWithin30Days);
        assertEquals(new BigDecimal("15"), report.PercentInvoicesWithin60Days);
        assertEquals(new BigDecimal( "5"), report.PercentInvoicesBeyond60Days);
        assertEquals("1 January 2016", new UiDate(report.StartDate).ToDateString());
        assertEquals("30 June 2016", new UiDate(report.EndDate).ToDateString());
        assertEquals("Payment terms", report.PaymentTerms);
        assertEquals("Maximum contract length", report.MaximumContractPeriod);
        assertEquals(true, report.PaymentTermsChanged);
        assertEquals("Contract changes", report.PaymentTermsChangedComment);
        assertEquals(true, report.PaymentTermsChangedNotified);
        assertEquals("Notified suppliers of change description", report.PaymentTermsChangedNotifiedComment);
        assertEquals("Payment terms comment", report.PaymentTermsComment);
        assertEquals("Dispute resolution", report.DisputeResolution);
        assertEquals(true, report.OfferEInvoicing);
        assertEquals(false, report.OfferSupplyChainFinance);
        assertEquals(true, report.RetentionChargesInPolicy);
        assertEquals(false, report.RetentionChargesInPast);
        assertEquals(true, report.HasPaymentCodes);
        assertEquals("Payment codes", report.PaymentCodes);
    }

    @Test
    public void tryFileReport_WhenCompanyUnknown() throws Exception {
        ReportFilingModel rfm = ReportModelExamples.makeFullReportFilingModel("124");
        CompanySummary newCorp = new CompanySummary("New corp", "124");
        ReportSummary reportSummary = jdbcReportsRepository.tryFileReport(rfm, newCorp, new UtcTimeProvider().Now());
        assertTrue(reportSummary != null);
        assertTrue(reportSummary.Identifier > 0);

        Option<ReportModel> report = jdbcReportsRepository.getReport("124", reportSummary.Identifier);
        assertEquals(false, report.isEmpty());
    }

    @Test
    public void tryFileReport_MistmatchingModels() throws Exception {
        try {
            jdbcReportsRepository.tryFileReport(
                    ReportFilingModel.MakeEmptyModelForTarget("120"),
                    new CompanySummary("mismatching company","121"),
                    new UtcTimeProvider().Now()
            );
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("if models mismatch, tryFileReport should fail");
    }

    @Test
    public void getCompanySearchInfo() throws Exception {
        PagedList<CompanySearchResult> results = jdbcReportsRepository.getCompanySearchInfo(new PagedList<>(Arrays.asList(
                new CompanySummaryWithAddress("Corp1", "120", "Address1"),
                new CompanySummaryWithAddress("Corp3", "122", "Address3"),
                new CompanySummaryWithAddress("NonexistCorp", "124", "Address4"),
                new CompanySummaryWithAddress("Corp2", "121", "Address2")),
                50, 0, 3));

        assertEquals(4, results.size());
        assertEquals(4, results.get(0).ReportCount);
        assertEquals(1, results.get(1).ReportCount);
        assertEquals(0, results.get(2).ReportCount);
        assertEquals(2, results.get(3).ReportCount);

        assertEquals(50, results.totalSize());
        assertEquals(0, results.pageNumber());
        assertEquals(3, results.pageSize());

    }

    @Test
    public void getCompanySearchInfo_EmptyInput() throws Exception {
        PagedList<CompanySearchResult> results = jdbcReportsRepository.getCompanySearchInfo(new PagedList<>(Lists.emptyList(), 0, 0, 3));
        assertEquals(0, results.size());
        assertEquals(0, results.totalSize());
        assertEquals(0, results.pageNumber());
        assertEquals(3, results.pageSize());
    }

    @Test
    public void exportData_withInvalidRange() throws Exception {
        List<F.Tuple<CompanySummary, ReportModel>> tuples = jdbcReportsRepository.exportData(-1);
        assertEquals(0, tuples.size());
    }

    @Test
    public void exportData() throws Exception {
        List<F.Tuple<CompanySummary, ReportModel>> data = jdbcReportsRepository.exportData(24);

        assertEquals("Should return all reports except oldest one: " + data.size(), 6, data.size());
    }
}

