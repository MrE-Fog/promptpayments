package components;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.CompanySummary;
import models.CompanySummaryWithAddress;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import play.libs.Json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

class ApiCompaniesHouseCommunicator implements CompaniesHouseCommunicator {

    @SuppressWarnings("FieldCanBeLocal")
    private String apiKey = System.getenv().get("COMPANIESHOUSE_API");
    private String clientId = System.getenv().get("COMPANIESHOUSE_CLIENTID");
    private String clientSecret = System.getenv().get("COMPANIESHOUSE_CLIENTSECRET");

    @Inject
    private HttpWrapper httpWrapper;

    ApiCompaniesHouseCommunicator(HttpWrapper httpWrapper) {
        this.httpWrapper = httpWrapper;
    }

    @Override
    public String getAuthorizationUri(String callbackUri, String companiesHouseIdentifier) throws IOException {
        return String.format("https://account.companieshouse.gov.uk/oauth2/authorise" +
                "?response_type=code&client_id=%s" +
                "&redirect_uri=%s" +
                "&scope=%s" +
                "&state=%s",
                urlEncode(clientId),
                urlEncode(callbackUri),
                urlEncode(getTargetScope(companiesHouseIdentifier)),
                urlEncode(companiesHouseIdentifier));
    }

    public String verifyAuthCode(String authCode, String redirectUri, String companiesHouseIdentifier) throws IOException {
        HttpPost post = new HttpPost("https://account.companieshouse.gov.uk/oauth2/token");

        String basicAuth = "Basic " + new String(Base64.getEncoder().encode((clientId+":"+clientSecret).getBytes()));
        post.setHeader("Authorization", basicAuth);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Charset", "utf-8");

        String targetScope = getTargetScope(companiesHouseIdentifier);

        String body = String.format("client_id=%s&client_secret=%s&grant_type=authorization_code" +
                        "&code=%s" +
                        "&redirect_uri=%s" +
                        "&scope=%s" +
                        "&state=%s",
                urlEncode(clientId),
                urlEncode(clientSecret),
                urlEncode(authCode),
                urlEncode(redirectUri),
                urlEncode(targetScope),
                urlEncode(companiesHouseIdentifier)
        );

        post.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));

        JsonNode json = httpWrapper.post(post);

        if (!json.has("access_token") || !json.has("refresh_token")) {
            return null;
        }

        String access_token = json.get("access_token").asText();
        String refresh_token = json.get("refresh_token").asText();

        if (doesOauthTokenHaveScope(companiesHouseIdentifier, access_token)) {
            return refresh_token;
        } else {
            return null;
        }
    }


    private RefreshTokenAndValue<String> getOauthToken(String refreshToken) throws IOException {
        HttpPost post = new HttpPost("https://account.companieshouse.gov.uk/oauth2/token");

        String basicAuth = "Basic " + new String(Base64.getEncoder().encode((clientId+":"+clientSecret).getBytes()));
        post.setHeader("Authorization", basicAuth);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Charset", "utf-8");

        String body = String.format("client_id=%s&client_secret=%s&grant_type=refresh_token" +
                        "&refresh_token=%s",
                urlEncode(clientId),
                urlEncode(clientSecret),
                urlEncode(refreshToken)
        );

        post.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));

        JsonNode json = httpWrapper.post(post);

        if (!json.has("access_token") && !json.has("refresh_token")) {
            return new RefreshTokenAndValue<>(null,null);
        }

        return new RefreshTokenAndValue<>(json.get("refresh_token").asText(), json.get("access_token").asText());
    }

    @Override
    public RefreshTokenAndValue<Boolean> isInScope(String companiesHouseIdentifier, String refreshToken) throws IOException {
        RefreshTokenAndValue<String> oauthToken = getOauthToken(refreshToken);
        return new RefreshTokenAndValue<>(oauthToken.refreshToken, doesOauthTokenHaveScope(companiesHouseIdentifier, oauthToken.value));
    }

    private boolean doesOauthTokenHaveScope(String companiesHouseIdentifier, String oAuthToken) throws IOException {
        String targetScope = getTargetScope(companiesHouseIdentifier);
        JsonNode verifyJson = httpWrapper.get("https://account.companieshouse.gov.uk/oauth2/verify", "Bearer " + oAuthToken);
        return verifyJson.has("scope") && verifyJson.get("scope").asText().equals(targetScope);
    }

    private String getTargetScope(String companiesHouseIdentifier) {
        return "http://ch.gov.uk/company/" + companiesHouseIdentifier;
    }

    @Override
    public RefreshTokenAndValue<String> getEmailAddress(String refreshToken) throws IOException {
        RefreshTokenAndValue<String> token = getOauthToken(refreshToken);
        JsonNode parsed = httpWrapper.get("https://account.companieshouse.gov.uk/user/profile", "Bearer " + token.value);
        return new RefreshTokenAndValue<>(token.refreshToken, parsed.has("email") ? parsed.get("email").asText() : null);
    }

    private static String urlEncode(String authCode) throws UnsupportedEncodingException {
        return URLEncoder.encode(authCode, "UTF-8");
    }

    @Override
    public PagedList<CompanySummaryWithAddress> searchCompanies(String search, int page, int itemsPerPage) throws IOException {

        String query = String.format("https://api.companieshouse.gov.uk/search/companies?q=%s&items_per_page=%s&start_index=%s",
                urlEncode(search),
                urlEncode(Integer.toString(itemsPerPage)),
                urlEncode(Integer.toString(page*itemsPerPage)));

        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(apiKey.getBytes()));
        JsonNode parsed = httpWrapper.get(query, basicAuth);

        List<CompanySummaryWithAddress> rtn = new ArrayList<>();

        Iterator<JsonNode> items = parsed.get("items").elements();
        while (items.hasNext()) {
            JsonNode company = items.next();
            rtn.add(new CompanySummaryWithAddress(company.get("title").asText(), company.get("company_number").asText(), company.get("address_snippet").asText()));
        }

        return new PagedList<>(
                rtn,
                parsed.get("total_results").asInt(),
                page, // page page_number in the result is a bit strange parsed.get("page_number").asInt(),
                parsed.get("items_per_page").asInt());
    }

    @Override
    public CompanySummary getCompany(String companiesHouseIdentifier) throws IOException {

        String query = String.format("https://api.companieshouse.gov.uk/company/%s",
                urlEncode(companiesHouseIdentifier));

        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(apiKey.getBytes()));
        JsonNode parsed = httpWrapper.get(query, basicAuth);
        if (!parsed.has("company_name") || !parsed.has("company_number")) {
            return null;
        }

        return new CompanySummary(parsed.get("company_name").asText(), parsed.get("company_number").asText());

    }
}

class HttpWrapper {
    public JsonNode post(HttpPost post) throws IOException {
        HttpResponse response = new DefaultHttpClient().execute(post);
        try {
            return Json.parse(response.getEntity().getContent());
        } catch (Exception e) {
            throw new IOException("Could not parse response", e);
        }
    }

    public JsonNode get(String url, String authorization) throws IOException {
        HttpGet request = new HttpGet(url);
        if (authorization != null) request.setHeader("Authorization", authorization);
        HttpResponse response = new DefaultHttpClient().execute(request);
        try {
            return Json.parse(response.getEntity().getContent());
        } catch (Exception e) {
            throw new IOException("Could not parse response", e);
        }

    }
}