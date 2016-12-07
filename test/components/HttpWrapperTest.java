package components;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by daniel.rothig on 05/12/2016.
 */
public class HttpWrapperTest {
    @Test
    public void post() throws Exception {
        new HttpWrapper().post(new HttpPost("http://jsonplaceholder.typicode.com/posts"));
    }

    @Test
    public void get() throws Exception {
        new HttpWrapper().get("http://jsonplaceholder.typicode.com/users", null);
    }

    @Test
    public void get_notjson() throws Exception {
        HttpWrapper httpWrapper = new HttpWrapper();
        try {
            JsonNode jsonNode = httpWrapper.get("http://www.google.com", null);
        } catch(IOException e) {
            assertTrue(e.getMessage().toLowerCase().contains("parse"));
            return;
        }
        fail("should throw");
    }

    @Test
    public void post_notjson() throws Exception {
        HttpWrapper httpWrapper = new HttpWrapper();
        try {
            JsonNode post = httpWrapper.post(new HttpPost("http://www.google.com/"));
        } catch(IOException e) {
            assertTrue(e.getMessage().toLowerCase().contains("parse"));
            return;
        }
        fail("should throw");
    }

}