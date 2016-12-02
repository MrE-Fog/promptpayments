package components;

import models.CompanySummary;
import models.ReportSummary;

/**
 * Created by daniel.rothig on 02/12/2016.
 */
public interface GovUkNotifyEmailer {
    boolean sendConfirmationEmail(String recipient, CompanySummary company, ReportSummary report, String url);
}
