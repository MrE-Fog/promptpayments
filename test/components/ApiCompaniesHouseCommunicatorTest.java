package components;

import models.CompanySummaryWithAddress;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 11/10/2016.
 *
 * Tests for ApiCompaniesHouseCommunicator
 */
public class ApiCompaniesHouseCommunicatorTest {

    @Test
    public void getCompanies() throws Exception {
        List<CompanySummaryWithAddress> companies = new ApiCompaniesHouseCommunicator().searchCompanies("eigencode", 0, 25);
        assertEquals(1, companies.size());
    }

    @Test
    public void getByCompanyNumber() throws Exception {
        assertEquals("EIGENCODE LTD", new ApiCompaniesHouseCommunicator().searchCompanies("10203299", 0, 25).get(0).Name);
    }

    @Test
    public void tryGetCompany() throws Exception {
        assertEquals("EIGENCODE LTD", new ApiCompaniesHouseCommunicator().getCompany("10203299").Name);

    }

    @Test
    public void tryGetCompany_ThrowsWhenTheresNoCompany() throws Exception {

        try {
            new ApiCompaniesHouseCommunicator().getCompany("123");
        } catch (IOException ignored){
            return;
        }
        fail("Should throw");
    }



    //@Test
    public void oauth() throws Exception {
        String url = new ApiCompaniesHouseCommunicator().getAuthorizationUri("https://paymentdutyregister.herokuapp.com/FileReport/cb", "10203299");

        new ApiCompaniesHouseCommunicator().verifyAuthCode(
                "iLwmKNd5-TX2CzFvQc2ANDxv-0c3nLS7fdVALdK7TI8",
                "https://paymentdutyregister.herokuapp.com/FileReport/cb",
                "10203299"
        );

    }
}