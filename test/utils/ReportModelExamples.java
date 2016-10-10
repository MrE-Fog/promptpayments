package utils;

import models.ReportFilingModel;

/**
 * Created by daniel.rothig on 10/10/2016.
 *
 * Some example models around reports
 */
public class ReportModelExamples {
    public static ReportFilingModel makeFullReportFilingModel(String companiesHouseIdentifier) {
        return new ReportFilingModel(
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
                true,
                false,
                false);
    }

    public static ReportFilingModel makeDifferentFullReportFilingModel(String companiesHouseIdentifier) {
        return new ReportFilingModel(
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
                false,
                true,
                true);
    }
}
