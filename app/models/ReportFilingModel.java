package models;

import java.math.BigDecimal;

import static utils.DecimalConverter.getBigDecimal;

/**
 * Model to be
 */
public class ReportFilingModel {
    private String TargetCompanyCompaniesHouseIdentifier;

    private double NumberOne;
    private double NumberTwo;
    private double NumberThree;

    /* Getters and setters */

    public String getTargetCompanyCompaniesHouseIdentifier() {
        return TargetCompanyCompaniesHouseIdentifier;
    }

    public void setTargetCompanyCompaniesHouseIdentifier(String targetCompanyCompaniesHouseIdentifier) {
        TargetCompanyCompaniesHouseIdentifier = targetCompanyCompaniesHouseIdentifier;
    }

    public double getNumberOne() {
        return NumberOne;
    }

    public BigDecimal getNumberOneAsDecimal() {
        return getBigDecimal(NumberOne);
    }

    public void setNumberOne(double numberOne) {
        NumberOne = numberOne;
    }

    public double getNumberTwo() {
        return NumberTwo;
    }

    public BigDecimal getNumberTwoAsDecimal() {
        return getBigDecimal(NumberTwo);
    }

    public void setNumberTwo(double numberTwo) {
        NumberTwo = numberTwo;
    }

    public double getNumberThree() {
        return NumberThree;
    }

    public BigDecimal getNumberThreeAsDecimal() {
        return getBigDecimal(NumberThree);
    }

    public void setNumberThree(double numberThree) {
        NumberThree = numberThree;
    }
}

