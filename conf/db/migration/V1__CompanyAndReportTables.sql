CREATE TABLE Company(
    CompaniesHouseIdentifier nvarchar(30),
    Name nvarchar(256)
);
CREATE TABLE Report(
    Identifier SERIAL PRIMARY KEY,
     CompaniesHouseIdentifier nvarchar(30) NOT NULL,
     FilingDate date NOT NULL,
     NumberOne DECIMAL DEFAULT 0.0,
     NumberTwo DECIMAL DEFAULT 0.0,
     NumberThree DECIMAL DEFAULT 0.0
);


