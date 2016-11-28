package utils;

import models.ReportFilingModel;
import models.ReportModel;
import models.ReportSummary;

import java.math.BigDecimal;
import java.util.Calendar;

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

                "2016",
                "1",
                "1",

                "2016",
                "6",
                "30",

                "Payment terms",
                "Maximum contract length",
                true,
                "Contract changes",
                true,
                "Notified suppliers of change description",
                "Payment terms comment",

                "Dispute resolution",

                true,
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

                "2017",
                "6",
                "2",

                "2017",
                "11",
                "31",

                "Strange Payment terms",
                "Extravagant max payment terms",
                false,
                "outstanding payment changes",
                false,
                "uproarious notifications",
                "stranger Payment terms comments",

                "Intriguing Dispute resolution",

                false,
                "Affronting Payment codes",

                false,
                true,
                false,
                true);
    }

    public static ReportModel makeReportModel(int id, int year, int month) {

        Calendar start = new MockUtcTimeProvider(2016, 0, 0).Now();
        Calendar end = new MockUtcTimeProvider(2016, 4, 30).Now();
        return new ReportModel(
                new ReportSummary(id, new MockUtcTimeProvider(year,month,1).Now(), start, end),
                new BigDecimal("31.00"),
                new BigDecimal("10.00"),
                new BigDecimal("80.00"),
                new BigDecimal("15.00"),
                new BigDecimal( "5.00"),
                start,
                end,
                "Payment terms",
                "max contract length",
                true,
                "contract changes",
                true,
                "contract change notification",
                "Payment terms comments",
                "Dispute terms",
                true,
                true,
                false,
                false,
                true,
                "Prompt payment code");
    }

    public static ReportModel makeEmptyReportModel() {
        Calendar start = new MockUtcTimeProvider(2016, 0, 1).Now();
        Calendar end = new MockUtcTimeProvider(2016, 5, 30).Now();
        return new ReportModel(
                new ReportSummary(1, new MockUtcTimeProvider(2016, 6, 1).Now(), start, end),
                null,
                null,
                null,
                null,
                null,
                start,
                end,
                null,
                null,
                false,
                null,
                false,
                null,
                null,
                null,
                false,
                false,
                false,
                false,
                false,
                null);
    }
}
