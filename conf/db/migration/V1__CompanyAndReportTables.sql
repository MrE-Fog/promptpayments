CREATE TABLE Company(
    CompaniesHouseIdentifier nvarchar(30) PRIMARY KEY,
    Name nvarchar(256)
);
CREATE TABLE Report(
    Identifier SERIAL PRIMARY KEY,
     CompaniesHouseIdentifier nvarchar(30) NOT NULL REFERENCES Company(CompaniesHouseIdentifier),
     FilingDate DATE NOT NULL,
     NumberOne DECIMAL DEFAULT 0.0,
     NumberTwo DECIMAL DEFAULT 0.0,
     NumberThree DECIMAL DEFAULT 0.0
);


