package models;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by daniel.rothig on 03/10/2016.
 *
 * Represents the identifying/descriptive information of a payment report
 */
public class ReportSummary {
    public int Identifier;
    private Calendar FilingDate;

    public String UiDateString() {
        return new UiDate(FilingDate).ToFriendlyString();
    }

    public Calendar getFilingDate() {return FilingDate; }

    public ReportSummary(int identifier, Calendar filingDate) {
        if (!filingDate.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
            throw new InvalidParameterException("filingDate must be UTC");
        }
        Identifier = identifier;
        FilingDate = filingDate;
    }
}
