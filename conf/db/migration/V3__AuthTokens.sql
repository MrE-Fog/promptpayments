CREATE TABLE AuthTokens(
    Token varchar NOT NULL PRIMARY KEY,
    CompaniesHouseIdentifier varchar NOT NULL,
    LoggingDate TIMESTAMP
);
CREATE INDEX AuthTokens_idx ON AuthTokens(Token, CompaniesHouseIdentifier);

