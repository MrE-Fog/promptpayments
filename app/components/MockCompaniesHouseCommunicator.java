package components;

import controllers.routes;
import models.CompanySummary;

import java.io.IOException;

public class MockCompaniesHouseCommunicator implements CompaniesHouseCommunicator {
    private CompaniesHouseCommunicator inner = new ApiCompaniesHouseCommunicator();

    @Override
    public String getAuthorizationUri(String callbackUri, String companiesHouseIdentifier) {
        return routes.CompaniesHouseMock.getPage1(companiesHouseIdentifier).url();
    }

    @Override
    public PagedList<CompanySummary> searchCompanies(String search, int page, int itemsPerPage) throws IOException {
        return inner.searchCompanies(search,page,itemsPerPage);
    }

    @Override
    public String verifyAuthCode(String authCode, String redirectUri, String companiesHouseIdentifier) throws IOException {
        return "ok";
    }

    @Override
    public CompanySummary tryGetCompany(String s) {
        return inner.tryGetCompany(s);
    }
}
