CREATE TABLE Company(
    CompaniesHouseIdentifier nvarchar(30) PRIMARY KEY,
    Name nvarchar(256)
);
CREATE TABLE Report(
    Identifier SERIAL PRIMARY KEY,
     CompaniesHouseIdentifier nvarchar(30) NOT NULL REFERENCES Company(CompaniesHouseIdentifier),
     FilingDate TIMESTAMP NOT NULL,

     AverageTimeToPay NUMERIC(256,2),
     PercentInvoicesPaidBeyondAgreedTerms NUMERIC(256,2),
     PercentInvoicesPaidWithin30Days NUMERIC(256,2),
     PercentInvoicesPaidWithin60Days NUMERIC(256,2),
     PercentInvoicesPaidBeyond60Days NUMERIC(256,2),

     StartDate TIMESTAMP,
     EndDate TIMESTAMP,

     PaymentTerms NVARCHAR(500),
     DisputeResolution NVARCHAR(500),

     OfferEInvoicing BIT DEFAULT 0,
     OfferSupplyChainFinance BIT DEFAULT 0,
     RetentionChargesInPolicy BIT DEFAULT 0,
     RetentionChargesInPast BIT DEFAULT 0,

     PaymentCodes NVARCHAR(500)
);