package components;

import com.google.inject.ImplementedBy;
import models.CompanySummary;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Performs queries to Companies House
 */
@ImplementedBy(MockCompaniesHouseCommunicator.class)
public interface CompaniesHouseCommunicator {
    String getAuthorizationUri(String callbackUri, String companiesHouseIdentifier);
    PagedList<CompanySummary> searchCompanies(String search, int page, int itemsPerPage) throws IOException;
    String verifyAuthCode(String authCode, String redirectUri, String companiesHouseIdentifier) throws IOException;
    CompanySummary tryGetCompany(String s);
}

