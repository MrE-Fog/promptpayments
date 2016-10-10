package components;

import models.CompanySummary;
import models.ReportModel;
import models.ReportSummary;
import org.junit.Test;
import play.libs.F;
import utils.MockUtcTimeProvider;
import utils.UtcTimeProvider;
import utils.TimeProvider;

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
        ReportModel reportModel = getEmptyReportModel();

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
                makeReportModel(1, 2016, 1)
        ));

        rtn.add(new F.Tuple<>(
                new CompanySummary("A, B and C Ltd.", "124"),
                makeReportModel(2, 2016,3)
        ));

        rtn.add(new F.Tuple<>(
                new CompanySummary("The \"Dungeon\" Ltd.", "125"),
                makeReportModel(3, 2016,2)
        ));

        rtn.add(new F.Tuple<>(
                new CompanySummary("The Scary, Scary \"Dungeon\" Ltd.", "126"),
                makeReportModel(4,2016,4)
        ));

        return rtn;
    }

    private ReportModel makeReportModel(int id, int year, int month) {
        return new ReportModel(
                new ReportSummary(id, new MockUtcTimeProvider(year,month,1).Now()),
                new BigDecimal("31.00"),
                new BigDecimal("10.00"),
                new BigDecimal("80.00"),
                new BigDecimal("15.00"),
                new BigDecimal( "5.00"),
                new MockUtcTimeProvider(2016,0,0).Now(),
                new MockUtcTimeProvider(2016,4,30).Now(),
                "Payment terms",
                "Dispute terms",
                true,
                true,
                false,
                false,
                "Prompt payment code");
    }

    private ReportModel getEmptyReportModel() {
        return new ReportModel(
                new ReportSummary(1, new MockUtcTimeProvider(2016, 6, 1).Now()),
                null,
                null,
                null,
                null,
                null,
                new MockUtcTimeProvider(2016, 0, 1).Now(),
                new MockUtcTimeProvider(2016, 5, 30).Now(),
                null,
                null,
                false,
                false,
                false,
                false,
                null);
    }

}

