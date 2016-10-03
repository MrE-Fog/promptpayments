package models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Represents the identifying/descriptive information of a payment report
 */
public class ReportSummary {
    public int Identifier;
    private Date FilingDate;

    public String UiDateString() {
        return new SimpleDateFormat("MMMM yyyy").format(FilingDate);
    }

    public ReportSummary(int identifier, Date filingDate) {
        Identifier = identifier;
        FilingDate = filingDate;
    }
}
