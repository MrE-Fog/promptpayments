package components;

import com.google.inject.ImplementedBy;
import models.CompanySummary;

import java.io.IOException;
import java.util.List;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Performs queries to Companies House
 */
@ImplementedBy(ApiCompaniesHouseCommunicator.class)
public interface CompaniesHouseCommunicator {
    boolean mayFileForCompany(String oAuthToken, String companiesHouseIdentifier);
    PagedList<CompanySummary> searchCompanies(String search, int page, int itemsPerPage) throws IOException;
    CompanySummary tryGetCompany(String s);
}

