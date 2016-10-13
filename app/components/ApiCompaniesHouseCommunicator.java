package components;

import com.fasterxml.jackson.databind.JsonNode;
import models.CompanySummary;
import play.libs.Json;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

class ApiCompaniesHouseCommunicator implements CompaniesHouseCommunicator {

    @SuppressWarnings("FieldCanBeLocal")
    private String apiKey = System.getenv().get("COMPANIESHOUSE_API");


    @Override
    public boolean mayFileForCompany(String oAuthToken, String companiesHouseIdentifier) {
        return oAuthToken != null && companiesHouseIdentifier != null
                && tryGetCompany(companiesHouseIdentifier) != null;
    }

    @Override
    public PagedList<CompanySummary> searchCompanies(String search, int page, int itemsPerPage) throws IOException {

        String query = String.format("https://api.companieshouse.gov.uk/search/companies?q=%s&items_per_page=%s&start_index=%s",
                URLEncoder.encode(search, "UTF-8"),
                URLEncoder.encode(Integer.toString(itemsPerPage), "UTF-8"),
                URLEncoder.encode(Integer.toString(page*itemsPerPage), "UTF-8"));

        URLConnection connection = new URL(query).openConnection();

        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(apiKey.getBytes()));
        connection.setRequestProperty("Authorization", basicAuth);
        JsonNode parsed = Json.parse(connection.getInputStream());
        List<CompanySummary> rtn = new ArrayList<>();

        Iterator<JsonNode> items = parsed.get("items").elements();
        while (items.hasNext()) {
            JsonNode company = items.next();
            rtn.add(new CompanySummary(company.get("title").asText(), company.get("company_number").asText()));
        }

        return new PagedList<>(
                rtn,
                parsed.get("total_results").asInt(),
                page, // page page_number in the result is a bit strange parsed.get("page_number").asInt(),
                parsed.get("items_per_page").asInt());
    }

    @Override
    public CompanySummary tryGetCompany(String companiesHouseIdentifier) {

        try {
            String query = String.format("https://api.companieshouse.gov.uk/company/%s",
                    URLEncoder.encode(companiesHouseIdentifier, "UTF-8"));

            URLConnection connection = new URL(query).openConnection();

            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(apiKey.getBytes()));
            connection.setRequestProperty("Authorization", basicAuth);
            JsonNode parsed = Json.parse(connection.getInputStream());
            if (!parsed.has("company_name") || !parsed.has("company_number")) {
                return null;
            }
            return new CompanySummary(parsed.get("company_name").asText(), parsed.get("company_number").asText());
        } catch(IOException ignored) {
            return null;
        }
    }
}
