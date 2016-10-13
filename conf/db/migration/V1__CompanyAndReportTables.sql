CREATE TABLE Company(
    CompaniesHouseIdentifier varchar(30) PRIMARY KEY,
    Name varchar(256)
);
CREATE TABLE Report(
    Identifier SERIAL PRIMARY KEY,
     CompaniesHouseIdentifier varchar(30) NOT NULL REFERENCES Company(CompaniesHouseIdentifier),
     FilingDate TIMESTAMP NOT NULL,

     AverageTimeToPay NUMERIC(256,2),
     PercentInvoicesPaidBeyondAgreedTerms NUMERIC(256,2),
     PercentInvoicesPaidWithin30Days NUMERIC(256,2),
     PercentInvoicesPaidWithin60Days NUMERIC(256,2),
     PercentInvoicesPaidBeyond60Days NUMERIC(256,2),

     StartDate TIMESTAMP,
     EndDate TIMESTAMP,

     PaymentTerms VARCHAR(500),
     DisputeResolution VARCHAR(500),

     OfferEInvoicing BOOLEAN DEFAULT FALSE,
     OfferSupplyChainFinance BOOLEAN DEFAULT FALSE,
     RetentionChargesInPolicy BOOLEAN DEFAULT FALSE,
     RetentionChargesInPast BOOLEAN DEFAULT FALSE,

     PaymentCodes VARCHAR(500)
);