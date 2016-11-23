package components;

import com.google.inject.ImplementedBy;
import models.CompanySummary;
import models.CompanySummaryWithAddress;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Performs queries to Companies House
 */
@ImplementedBy(ApiCompaniesHouseCommunicator.class)
public interface CompaniesHouseCommunicator {
    String getAuthorizationUri(String callbackUri, String companiesHouseIdentifier);
    PagedList<CompanySummaryWithAddress> searchCompanies(String search, int page, int itemsPerPage) throws IOException;
    String verifyAuthCode(String authCode, String redirectUri, String companiesHouseIdentifier) throws IOException;
    CompanySummary getCompany(String s) throws IOException;
}

