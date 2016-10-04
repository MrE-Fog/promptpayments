package models;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Model to be
 */
public class ReportFilingModel {
    public String TargetCompanyName;
    public String TargetCompanyCompaniesHouseIdentifier;
    public String FilingDate;

    public BigDecimal NumberOne;
    public BigDecimal NumberTwo;
    public BigDecimal NumberThree;

    private static DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");

    public ReportFilingModel(CompanySummary targetCompany, Date filingDate) {
        TargetCompanyName = targetCompany.Name;
        TargetCompanyCompaniesHouseIdentifier = targetCompany.CompaniesHouseIdentifier;
        FilingDate = dateFormat.format(filingDate);
    }

    public ReportFilingModel(){}

    public String FilingDateUiString() {
        return FilingDate;
    }

    public Date FilingDateAsDate() {
        try {
            return dateFormat.parse(FilingDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean IsComplete() {return false;}

    /* Getters and setters */

    public String getTargetCompanyName() {
        return TargetCompanyName;
    }

    public void setTargetCompanyName(String targetCompanyName) {
        TargetCompanyName = targetCompanyName;
    }

    public String getTargetCompanyCompaniesHouseIdentifier() {
        return TargetCompanyCompaniesHouseIdentifier;
    }

    public void setTargetCompanyCompaniesHouseIdentifier(String targetCompanyCompaniesHouseIdentifier) {
        TargetCompanyCompaniesHouseIdentifier = targetCompanyCompaniesHouseIdentifier;
    }

    public String getFilingDate() {
        return FilingDate;
    }

    public void setFilingDate(String filingDate) {
        FilingDate = filingDate;
    }

    public BigDecimal getNumberOne() {
        return NumberOne;
    }

    public void setNumberOne(BigDecimal numberOne) {
        NumberOne = numberOne;
    }

    public BigDecimal getNumberTwo() {
        return NumberTwo;
    }

    public void setNumberTwo(BigDecimal numberTwo) {
        NumberTwo = numberTwo;
    }

    public BigDecimal getNumberThree() {
        return NumberThree;
    }

    public void setNumberThree(BigDecimal numberThree) {
        NumberThree = numberThree;
    }
}
