package components;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Used for dev and test only
 */
class MockCompaniesHouseCommunicator implements CompaniesHouseCommunicator {
    @Override
    public List<String> RequestAuthorizedCompaniesForUser(String oAuthToken) {
        List<String> rtn = new ArrayList<>();
        rtn.add("122");
        return rtn;
    }
}
