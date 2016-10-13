package components;

import models.CompanySummary;
import org.junit.Test;

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
        List<CompanySummary> companies = new ApiCompaniesHouseCommunicator().searchCompanies("eigencode", 0, 25);
        assertEquals(1, companies.size());
    }

    @Test
    public void getByCompanyNumber() throws Exception {
        assertEquals("EIGENCODE LTD", new ApiCompaniesHouseCommunicator().searchCompanies("10203299", 0, 25).get(0).Name);
    }

    @Test
    public void tryGetCompany() throws Exception {
        assertEquals("EIGENCODE LTD", new ApiCompaniesHouseCommunicator().tryGetCompany("10203299").Name);
        assertNull(new ApiCompaniesHouseCommunicator().tryGetCompany("123"));
    }

    @Test
    public void mayFileForCompany() throws Exception {
        assertTrue(new ApiCompaniesHouseCommunicator().mayFileForCompany("something","10203299"));
        assertFalse(new ApiCompaniesHouseCommunicator().mayFileForCompany("something","123"));
        assertFalse(new ApiCompaniesHouseCommunicator().mayFileForCompany(null,"123"));
        assertFalse(new ApiCompaniesHouseCommunicator().mayFileForCompany("something",null));
    }
}