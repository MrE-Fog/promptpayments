package components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by daniel.rothig on 04/10/2016.
 *
 * Used for dev and test only
 */
public class MockCompaniesHouseCommunicator implements CompaniesHouseCommunicator {
    @Override
    public List<String> RequestAuthorizedCompaniesForUser(String oAuthToken) {
        return Collections.singletonList("122");
    }
}
