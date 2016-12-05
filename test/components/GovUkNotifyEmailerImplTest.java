package components;

import models.CompanySummary;
import models.ReportSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.service.notify.NotificationClientException;
import utils.UtcTimeProvider;

import java.util.Calendar;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class GovUkNotifyEmailerImplTest {

    @Mock
    NotifyWrapper mock;
    @Captor
    private ArgumentCaptor<HashMap<String, String>> paramsCaptor;

    @Test
    public void sendConfirmationEmail() throws Exception {
        GovUkNotifyEmailer emailer = new GovUkNotifyEmailerImpl(mock);
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);

        ReportSummary reportSummary = getReportSummary();

        boolean success = emailer.sendConfirmationEmail("foo@bar.com",new CompanySummary("CorpInc LTD", "123"), reportSummary, "http://www.my-url.com");
        verify(mock, times(1)).sendEmail(any(), captor1.capture(), paramsCaptor.capture());

        assertTrue(success);
        assertEquals("foo@bar.com", captor1.getValue());

        HashMap<String, String> params = paramsCaptor.getValue();

        assertEquals(6, params.size());
        assertEquals(params.get("companyname"), "CorpInc LTD");
        assertEquals(params.get("companieshouseidentifier"), "123");
        assertEquals(params.get("startdate"), reportSummary.StartDateString());
        assertEquals(params.get("enddate"), reportSummary.EndDateString());
        assertEquals(params.get("reportid"), String.valueOf(reportSummary.Identifier));
        assertEquals(params.get("reporturl"), "http://www.my-url.com");
    }

    @Test
    public void sendConfirmationEmail_WhenThrows_false() throws Exception {
        GovUkNotifyEmailer emailer = new GovUkNotifyEmailerImpl(mock);
        Mockito.doThrow(NotificationClientException.class).when(mock).sendEmail(any(),any(),any());
        boolean success = emailer.sendConfirmationEmail("foo@bar.com", new CompanySummary("CorpInc LTD", "123"), getReportSummary(), "");

        assertFalse(success);
    }

    private ReportSummary getReportSummary() {
        Calendar now = new UtcTimeProvider().Now();
        Calendar start = new UtcTimeProvider().Now();
        start.add(Calendar.MONTH, -2);
        Calendar end = new UtcTimeProvider().Now();
        end.add(Calendar.MONTH, -1);

        return new ReportSummary(1, now, start, end);
    }

}