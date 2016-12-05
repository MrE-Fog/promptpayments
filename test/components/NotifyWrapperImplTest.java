package components;

import org.jose4j.lang.InvalidKeyException;
import org.junit.Test;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;

import static org.junit.Assert.*;

public class NotifyWrapperImplTest {
    @Test
    public void sendEmail() throws Exception {
        NotifyWrapperImpl impl = new NotifyWrapperImpl("somekey");
        try {
            impl.sendEmail("sometemplate", "foo@bar.com", new HashMap<>());
        } catch (NotificationClientException ignored) {}
    }

}