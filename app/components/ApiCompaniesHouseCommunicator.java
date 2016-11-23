package components;

import com.fasterxml.jackson.databind.JsonNode;
import models.CompanySummary;
import models.CompanySummaryWithAddress;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
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


    @Override
    public String getAuthorizationUri(String callbackUri, String companiesHouseIdentifier) {
        try {
            return String.format("https://account.companieshouse.gov.uk/oauth2/authorise" +
                    "?response_type=code&client_id=%s" +
                    "&redirect_uri=%s" +
                    "&scope=%s" +
                    "&state=%s",
                    urlEncode(clientId),
                    urlEncode(callbackUri),
                    urlEncode("http://ch.gov.uk/company/" + companiesHouseIdentifier),
                    urlEncode(companiesHouseIdentifier));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String verifyAuthCode(String authCode, String redirectUri, String companiesHouseIdentifier) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://account.companieshouse.gov.uk/oauth2/token");

        String basicAuth = "Basic " + new String(Base64.getEncoder().encode((clientId+":"+clientSecret).getBytes()));
        post.setHeader("Authorization", basicAuth);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Charset", "utf-8");

        String body = String.format("client_id=%s&client_secret=%s&grant_type=authorization_code" +
                        "&code=%s" +
                        "&redirect_uri=%s" +
                        "&scope=%s" +
                        "&state=%s",
                urlEncode(clientId),
                urlEncode(clientSecret),
                urlEncode(authCode),
                urlEncode(redirectUri),
                urlEncode("http://ch.gov.uk/company/" + companiesHouseIdentifier),
                urlEncode(companiesHouseIdentifier)
        );

        post.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));
        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null)
            result.append(line);

        JsonNode json = Json.parse(result.toString());
        if (!json.has("access_token")) {
            return null;
        }

        //todo: confirm access token scope

        String access_token = json.get("access_token").asText();
        return access_token;
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

        URLConnection connection = new URL(query).openConnection();

        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(apiKey.getBytes()));
        connection.setRequestProperty("Authorization", basicAuth);
        JsonNode parsed = Json.parse(connection.getInputStream());
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

            URLConnection connection = new URL(query).openConnection();

            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(apiKey.getBytes()));
            connection.setRequestProperty("Authorization", basicAuth);
            JsonNode parsed = Json.parse(connection.getInputStream());
            if (!parsed.has("company_name") || !parsed.has("company_number")) {
                return null;
            }
            return new CompanySummary(parsed.get("company_name").asText(), parsed.get("company_number").asText());

    }
}
