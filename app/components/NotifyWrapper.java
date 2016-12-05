package components;

import com.google.inject.ImplementedBy;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;

/**
 * Thin wrapper for the GOV.UK Notify client (for testing purposes)
 */
@ImplementedBy(NotifyWrapperImpl.class)
interface NotifyWrapper {
    void sendEmail(String templateId, String recipient, HashMap<String, String> params) throws NotificationClientException;
}

class NotifyWrapperImpl implements NotifyWrapper {
    private final String apiKey = System.getenv().get("GOVUKNOTIFY_API");
    private final NotificationClient client = new NotificationClient(apiKey);


    @Override
    public void sendEmail(String templateId, String recipient, HashMap<String, String> params) throws NotificationClientException {
        client.sendEmail(templateId, recipient, params);
    }
}


