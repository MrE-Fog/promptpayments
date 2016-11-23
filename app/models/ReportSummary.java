package models;

import scala.collection.script.Start;

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
    public Calendar StartDate;
    public Calendar EndDate;
    public int Identifier;
    private Calendar FilingDate;

    public String UiDateString() {
        return new UiDate(FilingDate).ToDateString();
    }
    public String StartDateString() {
        return new UiDate(StartDate).ToDateString();
    }
    public String EndDateString() {
        return new UiDate(EndDate).ToDateString();
    }

    public Calendar getFilingDate() { return FilingDate; }

    public ReportSummary(int identifier, Calendar filingDate, Calendar startDate, Calendar endDate) {
        if (!filingDate.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
            throw new InvalidParameterException("filingDate must be UTC");
        }
        if (!startDate.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
            throw new InvalidParameterException("startDate must be UTC");
        }
        if (!endDate.getTimeZone().equals(TimeZone.getTimeZone("UTC"))) {
            throw new InvalidParameterException("endDate must be UTC");
        }
        Identifier = identifier;
        FilingDate = filingDate;

        StartDate = startDate;
        EndDate = endDate;

    }
}
