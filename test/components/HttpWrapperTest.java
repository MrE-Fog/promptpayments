package components;

import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

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

}