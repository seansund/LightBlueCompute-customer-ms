package customer;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.cloudant.client.api.ClientBuilder;

public class CloudantDatabaseBuilder_cbTest {
	
	@Test
	public void clientBuilder_good() throws MalformedURLException {
		CloudantDatabaseBuilder builder = new CloudantDatabaseBuilder();
		
		URL url = new URL("http", "test.com", 1234, "");
		
		ClientBuilder cb = builder.clientBuilder(url);
		assertTrue(cb != null);
	}

}
