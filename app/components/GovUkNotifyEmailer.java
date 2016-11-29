package components;

import controllers.routes;
import models.CompanySummary;
import models.ReportSummary;
import uk.gov.service.notify.*;

import java.util.HashMap;

/**
 * Created by daniel.rothig on 29/11/2016.
 *
 */
public class GovUkNotifyEmailer {
    private String apiKey = System.getenv().get("GOVUKNOTIFY_API");

    public boolean sendConfirmationEmail(String recipient, CompanySummary company, ReportSummary report, String url) {
        NotificationClient client = new NotificationClient(apiKey);

        HashMap<String, String> params = new HashMap<>();
        params.put("companyname", company.Name);
        params.put("companieshouseidentifier", company.CompaniesHouseIdentifier);
        params.put("startdate", report.StartDateString());
        params.put("enddate", report.EndDateString());
        params.put("reportid", String.valueOf(report.Identifier));
        params.put("reporturl", url);

        try {
            NotificationResponse confirmation = client.sendEmail("28e93e09-fcdc-4077-893a-6b61c4340840", recipient, params);
            confirmation.getNotificationId();
        } catch (NotificationClientException e) {
            return false;
        }
        return true;
    }
}
