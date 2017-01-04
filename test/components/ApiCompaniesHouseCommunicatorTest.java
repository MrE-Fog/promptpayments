package components;

import models.CompanySummary;
import models.CompanySummaryWithAddress;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.libs.Json;

import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        when(httpWrapper.post(any())).thenReturn(Json.parse("{\"access_token\": \"sometoken\", \"refresh_token\": \"somerefreshtoken\"}"));
        when(httpWrapper.get(any(), any())).thenReturn(Json.parse("{\"scope\": \"http://ch.gov.uk/company/123\"}"));
        String sometoken = new ApiCompaniesHouseCommunicator(httpWrapper).verifyAuthCode("somecode", "http://example.com", "123");
        assertEquals("somerefreshtoken", sometoken);
    }

    @Test
    public void verifyAuthCode_fail() throws Exception {
        when(httpWrapper.post(any())).thenReturn(Json.parse("{\"error\": \"computer doesnt like your face\"}"));
        String sometoken = new ApiCompaniesHouseCommunicator(httpWrapper).verifyAuthCode("somecode", "http://example.com", "123");
        assertNull(sometoken);
    }

    @Test
    public void getEmailAddress() throws Exception {
        when(httpWrapper.post(any())).thenReturn(Json.parse("{\"access_token\": \"somenewtoken\", \"refresh_token\": \"somerefreshtoken\"}"));
        when(httpWrapper.get(any(), eq("Bearer somenewtoken"))).thenReturn(Json.parse("{\"email\": \"foo@bar.com\"}"));
        String emailAddress = new ApiCompaniesHouseCommunicator(httpWrapper).getEmailAddress("sometoken").value;

        assertEquals("foo@bar.com", emailAddress);
    }

    @Test
    public void getEmailAddress_fail() throws Exception {
        when(httpWrapper.post(any())).thenReturn(Json.parse("{\"access_token\": \"somenewtoken\", \"refresh_token\": \"somerefreshtoken\"}"));when(httpWrapper.get(any(), eq("Bearer somenewtoken"))).thenReturn(Json.parse("{\"error\": \"computer says no\"}"));
        String emailAddress = new ApiCompaniesHouseCommunicator(httpWrapper).getEmailAddress("sometoken").value;

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

    /**
     * Complex integration test for oAuth protocol
     */
    @Test
    public void oauth() throws Exception {

        ApiCompaniesHouseCommunicator communicator = new ApiCompaniesHouseCommunicator(new HttpWrapper());

        String validUsername = System.getenv().get("TESTCH_USER");
        String validPassword = System.getenv().get("TESTCH_PASSWORD");
        String validAuthCode = System.getenv().get("TESTCH_AUTHCODE");

        // STEP 1 - GET request to the authorization URL
        String url = communicator.getAuthorizationUri("https://paymentdutyregister.herokuapp.com/FileReport/cb", "10203299");
        String signInHtml = IOUtils.toString(new DefaultHttpClient().execute(new HttpGet(url)).getEntity().getContent(), "UTF-8");

        // STEP 2 - extract the "Request" parameter from the returned HTML
        Matcher requestRegex = Pattern.compile("href=\"/user/register\\?request=([^\"]*)\"").matcher(signInHtml);
        requestRegex.find();
        String request = requestRegex.group(1);

        // STEP 3 - use the "request" parameter to put together a post request for user authentication
        HttpPost userSigninPost = new HttpPost("https://account.companieshouse.gov.uk/oauth2/user/signin");
        userSigninPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        userSigninPost.setHeader("Charset", "utf-8");
        userSigninPost.setEntity(new ByteArrayEntity(String.format("signin_email=%s&password=%s&request=%s",
                URLEncoder.encode(validUsername, "utf-8"),
                URLEncoder.encode(validPassword, "utf-8"),
                URLEncoder.encode(request, "utf-8")).getBytes("utf-8")));
        HttpResponse signInExecute = new DefaultHttpClient().execute(userSigninPost);

        // STEP 4 - follow the redirect that the previous POST returns
        HttpGet authGet = new HttpGet("https://account.companieshouse.gov.uk" + signInExecute.getHeaders("Location")[0].getValue());
        authGet.setHeader(new BasicHeader("Cookie", signInExecute.getHeaders("Set-Cookie")[1].getValue()));
        String authHtml = IOUtils.toString(new DefaultHttpClient().execute(authGet).getEntity().getContent(), "UTF-8");

        // STEP 5 - extract the "request" parameter from the HTML of that GET
        requestRegex = Pattern.compile("href=\"/company/10203299/authcode/request\\?request=([^\"]*)\"").matcher(authHtml);
        requestRegex.find();
        request = requestRegex.group(1);

        // STEP 6 - use the "request" parameter to put together a POST request for company authentication
        HttpPost companySigninPost = new HttpPost("https://account.companieshouse.gov.uk/oauth2/company/signin");
        companySigninPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        companySigninPost.setHeader("Charset", "utf-8");
        companySigninPost.setHeader(new BasicHeader("Cookie", signInExecute.getHeaders("Set-Cookie")[1].getValue()));
        companySigninPost.setEntity(new ByteArrayEntity(String.format("auth_code=%s&request=%s",
                URLEncoder.encode(validAuthCode, "utf-8"),
                URLEncoder.encode(request, "utf-8")).getBytes("utf-8")));
        HttpResponse companySignInExecute = new DefaultHttpClient().execute(companySigninPost);

        // STEP 7 - extract the authentication code from the redirect location that a succcessful company authentication returns
        companySignInExecute.getHeaders("Location")[0].getValue();
        Matcher codeRegex = Pattern.compile("code=([^&]+)").matcher(companySignInExecute.getHeaders("Location")[0].getValue());
        codeRegex.find();
        String code = codeRegex.group(1);

        // STEP 8 - the actual test: now that we have been given an authentication code, try and convert it into an Authentication Token and verify the scope
        String token = communicator.verifyAuthCode(
                code,
                "https://paymentdutyregister.herokuapp.com/FileReport/cb",
                "10203299"
        );

        // STEP 9 - an additional test to check that the email address is correctly returned, thus coming full circle
        String result = communicator.getEmailAddress(token).value;

        assertEquals(validUsername, result);
    }
}