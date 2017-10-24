package customer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

import customer.config.CloudantPropertiesBean;

public class CloudantDatabaseBuilderTest {
	@Mock private ClientBuilder clientBuilder;
	@InjectMocks private CloudantDatabaseBuilder cloudantClientBuilder;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void canary() {
		assertThat(true, is(true));
	}

	@Test
	public void buildCloudantClient_good() throws MalformedURLException {
		CloudantPropertiesBean cloudantProperties = mock(CloudantPropertiesBean.class);
		when(cloudantProperties.getDatabase()).thenReturn("database");
		when(cloudantProperties.getUsername()).thenReturn("username");
		when(cloudantProperties.getPassword()).thenReturn("password");
		when(cloudantProperties.buildURL()).thenReturn(new URL("http://test.com"));
		
		CloudantClient cloudantClient = mock(CloudantClient.class);
		
		when(clientBuilder.username(any())).thenReturn(clientBuilder);
		when(clientBuilder.password(any())).thenReturn(clientBuilder);
		when(clientBuilder.build()).thenReturn(cloudantClient);
		
		Database result = mock(Database.class);
		
		when(cloudantClient.database("database", true)).thenReturn(result);
		
		Database client = cloudantClientBuilder.build(cloudantProperties);
		
		assertThat(client, is(result));
		verify(clientBuilder, times(1)).username("username");
		verify(clientBuilder, times(1)).password("password");
		verify(clientBuilder, times(1)).build();
	}
}
