CREATE INDEX Company_idx ON Company(CompaniesHouseIdentifier);
CREATE INDEX Report_Identifier_idx ON Report(Identifier);
CREATE INDEX Report_Company_Date_idx ON Report(CompaniesHouseIdentifier, FilingDate);