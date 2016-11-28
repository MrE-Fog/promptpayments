package components;

import models.CompanySummary;
import models.ReportModel;
import models.ReportSummary;
import org.junit.Test;
import play.libs.F;
import utils.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CsvDataExporterTest {

    @Test
    public void generateCsv_RequestsCertainNumberOfMonths() throws Exception {
        ReportsRepository reportsRepository = mock(ReportsRepository.class);

        new CsvDataExporter(reportsRepository, new UtcTimeProvider()).GenerateCsv();

        int expectedRequestedMonths = 24;
        verify(reportsRepository).ExportData(expectedRequestedMonths);
    }

    @Test
    public void generateCsv_canDealWithNulls() throws Exception {
        ReportsRepository reportsRepository = mock(ReportsRepository.class);
        ReportModel reportModel = ReportModelExamples.makeEmptyReportModel();

        when(reportsRepository.ExportData(24)).thenReturn(Collections.singletonList(new F.Tuple<>(
                new CompanySummary(null, null),
                reportModel)));

        String csv = new CsvDataExporter(reportsRepository, new UtcTimeProvider()).GenerateCsv();

        int expectedRequestedMonths = 24;
        verify(reportsRepository).ExportData(expectedRequestedMonths);
        assertTrue(csv.split("\n")[1].contains(",,,,"));

    }

    @Test
    public void generateCsv_CachesResults() throws Exception {
        ReportsRepository reportsRepository = mock(ReportsRepository.class);
        CsvDataExporter csvDataExporter = new CsvDataExporter(reportsRepository, new UtcTimeProvider());

        csvDataExporter.GenerateCsv();
        csvDataExporter.GenerateCsv();

        verify(reportsRepository, times(1)).ExportData(anyInt());
    }

    @Test
    public void generateCsv_InvalidatesOldCache() throws Exception {
        ReportsRepository reportsRepository = mock(ReportsRepository.class);
        TimeProvider timeProvider = mock(TimeProvider.class);

        CsvDataExporter csvDataExporter = new CsvDataExporter(reportsRepository,timeProvider);

        when(timeProvider.Now()).thenReturn(new MockUtcTimeProvider(2016, 1, 1).Now());
        csvDataExporter.GenerateCsv();

        when(timeProvider.Now()).thenReturn(new MockUtcTimeProvider(2016, 1, 2).Now());
        csvDataExporter.GenerateCsv();

        verify(reportsRepository, times(2)).ExportData(anyInt());
    }

    @Test
    public void generateCsv_HasHeaderRow() throws Exception {
        String[] csv = getMockedCsvRows();

        assertEquals(5, csv.length);
        assertTrue("The header row should include the words \"Filing date\"", csv[0].contains("Filing date"));
    }

    @Test
    public void generateCsv_HasTheExpectedColumns() throws Exception {
        assertEquals("ReportModel appears to have changed - ensure that CsvDataExporter::generateCsv covers all desirable columns",
                22, ReflectiveObjectTester.countGettables(ReportModel.class));
        assertEquals("CompanySummary appears to have changed - ensure that CsvDataExporter::generateCsv covers all desirable columns",
                2, ReflectiveObjectTester.countGettables(CompanySummary.class));
        assertEquals("ReportSummary appears to have changed - ensure that CsvDataExporter::generateCsv covers all desirable columns",
                4, ReflectiveObjectTester.countGettables(ReportSummary.class));

        String[] csv = getMockedCsvRows();
        int expectedColumnCount = 24;
        assertEquals("Header row doesn't have the right number of columns",
                expectedColumnCount, csv[0].split(",").length);
        assertEquals("Data row doesn't have the right number of columns",
                expectedColumnCount, csv[1].split(",").length);

    }

    @Test
    public void generateCsv_ReturnsRowsInOriginalOrder() throws Exception {
        String[] csv = getMockedCsvRows();

        assertTrue(csv[1].contains("February 2016"));
        assertTrue(csv[2].contains("April 2016"));
        assertTrue(csv[3].contains("March 2016"));
        assertTrue(csv[4].contains("May 2016"));
    }

    @Test
    public void generateCsv_EscapesCommasCorrectly() throws Exception {
        String[] csv = getMockedCsvRows();

        assertTrue("Columns containing commas should be wrapped in quotation marks",csv[2].contains("\"A, B and C Ltd.\","));
    }

    @Test
    public void generateCsv_DoesntEscapeDoubleQuotesIfNoComma() throws Exception {
        String[] csv = getMockedCsvRows();

        assertTrue("In the absence of commas, quotes should not be escaped", csv[3].contains("The \"Dungeon\" Ltd."));
    }

    @Test
    public void generateCsv_EscapesDoubleQuotesIfComma() throws Exception {
        String[] csv = getMockedCsvRows();

        assertTrue("If there are commas, quotes should be escaped by doubling them", csv[4].contains("\"The Scary, Scary \"\"Dungeon\"\" Ltd.\""));
    }

    private String[] getMockedCsvRows() {
        ReportsRepository reportsRepository = mock(ReportsRepository.class);
        when(reportsRepository.ExportData(anyInt())).thenReturn(sampleExport());
        CsvDataExporter csvDataExporter = new CsvDataExporter(reportsRepository, new UtcTimeProvider());

        return csvDataExporter.GenerateCsv().split("\n");
    }

    private List<F.Tuple<CompanySummary, ReportModel>> sampleExport() {
        ArrayList<F.Tuple<CompanySummary, ReportModel>> rtn = new ArrayList<>();


        rtn.add(new F.Tuple<>(
                new CompanySummary("SomeComp", "123"),
                ReportModelExamples.makeReportModel(1, 2016, 1)
        ));

        rtn.add(new F.Tuple<>(
                new CompanySummary("A, B and C Ltd.", "124"),
                ReportModelExamples.makeReportModel(2, 2016, 3)
        ));

        rtn.add(new F.Tuple<>(
                new CompanySummary("The \"Dungeon\" Ltd.", "125"),
                ReportModelExamples.makeReportModel(3, 2016, 2)
        ));

        rtn.add(new F.Tuple<>(
                new CompanySummary("The Scary, Scary \"Dungeon\" Ltd.", "126"),
                ReportModelExamples.makeReportModel(4, 2016, 4)
        ));

        return rtn;
    }

}

