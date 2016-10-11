package utils;

import models.ReportFilingModel;
import models.ReportModel;
import models.ReportSummary;

import java.math.BigDecimal;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * Some example models around reports
 */
public class ReportModelExamples {
    public static ReportFilingModel makeFullReportFilingModel(String companiesHouseIdentifier) {
        return ReportFilingModel.makeReportFilingModel(
                companiesHouseIdentifier,
                31.0,
                10.0,
                80.0,
                15.0,
                5.0,

                2016,
                0,
                1,

                2016,
                5,
                30,

                "Payment terms",
                "Dispute resolution",
                "Payment codes",

                true,
                false,
                true,
                false);
    }

    public static ReportFilingModel makeDifferentFullReportFilingModel(String companiesHouseIdentifier) {
        return ReportFilingModel.makeReportFilingModel(
                companiesHouseIdentifier,
                61.0,
                20.0,
                40.0,
                50.0,
                10.0,

                2017,
                6,
                2,

                2017,
                11,
                31,

                "Strange Payment terms",
                "Intriguing Dispute resolution",
                "Affronting Payment codes",

                false,
                true,
                false,
                true);
    }

    public static ReportModel makeReportModel(int id, int year, int month) {
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

    public static ReportModel makeEmptyReportModel() {
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
