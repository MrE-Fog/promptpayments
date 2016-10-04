package components;

import com.google.inject.ImplementedBy;

import java.util.List;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Performs queries to Companies House
 */
@ImplementedBy(MockCompaniesHouseCommunicator.class)
interface CompaniesHouseCommunicator {
    List<String> RequestAuthorizedCompaniesForUser(String oAuthToken);
}
