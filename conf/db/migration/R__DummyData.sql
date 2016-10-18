DELETE FROM Company;
DELETE FROM Report;

INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Nicecorp', '120');
INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Cookies Ltd.', '121');
INSERT INTO Company(Name, CompaniesHouseIdentifier) VALUES ('Eigencode Ltd.', '122');

INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2015-02-01');
INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2015-08-01');
INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('120', '2016-02-01');
INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('121', '2015-07-01');
INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('121', '2016-01-01');
INSERT INTO Report(CompaniesHouseIdentifier, FilingDate) VALUES ('122', '2016-05-01');