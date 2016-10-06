CREATE TABLE Company(
    CompaniesHouseIdentifier nvarchar(30) PRIMARY KEY,
    Name nvarchar(256)
);
CREATE TABLE Report(
    Identifier SERIAL PRIMARY KEY,
     CompaniesHouseIdentifier nvarchar(30) NOT NULL REFERENCES Company(CompaniesHouseIdentifier),
     FilingDate TIMESTAMP NOT NULL,
     NumberOne NUMERIC(256,2) DEFAULT 0.00,
     NumberTwo NUMERIC(256,2) DEFAULT 0.00,
     NumberThree NUMERIC(256,2) DEFAULT 0.00
);


