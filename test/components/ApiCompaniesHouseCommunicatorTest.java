package components;

import models.CompanySummary;
import models.CompanySummaryWithAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.libs.Json;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by daniel.rothig on 11/10/2016.
 *
 * Tests for ApiCompaniesHouseCommunicator
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiCompaniesHouseCommunicatorTest {
    @Mock
    HttpWrapper httpWrapper;

    @Test
    public void getAuthorizationUri() throws Exception {
        String authorizationUri = new ApiCompaniesHouseCommunicator(new HttpWrapper()).getAuthorizationUri("https://examplefoobar.com", "123");

        assertTrue(authorizationUri.contains("=123"));
        assertTrue(authorizationUri.contains("examplefoobar"));
    }

    @Test
    public void verifyAuthCode() throws Exception {
        when(httpWrapper.post(any())).thenReturn(Json.parse("{\"access_token\": \"sometoken\"}"));
        String sometoken = new ApiCompaniesHouseCommunicator(httpWrapper).verifyAuthCode("somecode", "http://example.com", "123");
        assertEquals("sometoken", sometoken);
    }

    @Test
    public void verifyAuthCode_fail() throws Exception {
        when(httpWrapper.post(any())).thenReturn(Json.parse("{\"error\": \"computer doesnt like your face\"}"));
        String sometoken = new ApiCompaniesHouseCommunicator(httpWrapper).verifyAuthCode("somecode", "http://example.com", "123");
        assertNull(sometoken);
    }

    @Test
    public void getEmailAddress() throws Exception {
        when(httpWrapper.get(any(), eq("Bearer sometoken"))).thenReturn(Json.parse("{\"email\": \"foo@bar.com\"}"));
        String emailAddress = new ApiCompaniesHouseCommunicator(httpWrapper).getEmailAddress("sometoken");

        assertEquals("foo@bar.com", emailAddress);
    }

    @Test
    public void getEmailAddress_fail() throws Exception {
        when(httpWrapper.get(any(), eq("Bearer sometoken"))).thenReturn(Json.parse("{\"error\": \"computer says no\"}"));
        String emailAddress = new ApiCompaniesHouseCommunicator(httpWrapper).getEmailAddress("sometoken");

        assertNull(emailAddress);
    }

    @Test
    public void getCompanies() throws Exception {
        List<CompanySummaryWithAddress> companies = new ApiCompaniesHouseCommunicator(new HttpWrapper()).searchCompanies("eigencode", 0, 25);
        assertEquals(1, companies.size());
    }

    @Test
    public void getByCompanyNumber() throws Exception {
        assertEquals("EIGENCODE LTD", new ApiCompaniesHouseCommunicator(new HttpWrapper()).searchCompanies("10203299", 0, 25).get(0).Name);
    }

    @Test
    public void tryGetCompany() throws Exception {
        assertEquals("EIGENCODE LTD", new ApiCompaniesHouseCommunicator(new HttpWrapper()).getCompany("10203299").Name);

    }

    @Test
    public void tryGetCompany_ThrowsWhenTheresNoCompany() throws Exception {
        CompanySummary noCompany = new ApiCompaniesHouseCommunicator(new HttpWrapper()).getCompany("ffffffffffffffffffffffffffffffffffffff");
        assertNull(noCompany);
    }

    //@Test
    public void oauth() throws Exception {
        String url = new ApiCompaniesHouseCommunicator(httpWrapper).getAuthorizationUri("https://paymentdutyregister.herokuapp.com/FileReport/cb", "10203299");

        //String token = new ApiCompaniesHouseCommunicator().verifyAuthCode(
        //        "b3D4vFtvsmXyh14UwzG78uJkf1un8k6EEMTAdm5JId4",
        //        "https://paymentdutyregister.herokuapp.com/FileReport/cb",
        //        "10203299"
        //);

        String token = "jAPoPSyvWWu8bz-f4o59eFbs0MhiRI87ZQzgGFHSLgBWlzaspGxuW1Sq6jZmpkeld67qnWDCnkr627nTGaRLZg";
        String result = new ApiCompaniesHouseCommunicator(httpWrapper).getEmailAddress(token);

    }
}