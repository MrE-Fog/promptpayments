package models;

public class CompanySummaryWithAddress extends CompanySummary {
    public  String AddressLine;

    public CompanySummaryWithAddress(String name, String companiesHouseIdentifier, String addressLine) {
        super(name, companiesHouseIdentifier);
        AddressLine = addressLine;
    }
}
