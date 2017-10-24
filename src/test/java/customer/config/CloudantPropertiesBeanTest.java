package customer.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={TestApplication.class})
@ActiveProfiles("test")
public class CloudantPropertiesBeanTest {
	@Autowired
	private CloudantPropertiesBean cloudantPropertiesBean;
	
	@Test
	public void verify_CloudantPropertiesBean_from_properties() {
		assertThat(cloudantPropertiesBean.getUsername(), is(equalTo("username")));
		assertThat(cloudantPropertiesBean.getPassword(), is(equalTo("password")));
		assertThat(cloudantPropertiesBean.getHost(), is(equalTo("host")));
		assertThat(cloudantPropertiesBean.getProtocol(), is(equalTo("https")));
		assertThat(cloudantPropertiesBean.getPort(), is(equalTo(1234)));
		assertThat(cloudantPropertiesBean.getDatabase(), is(equalTo("customers")));
	}
	
	@Test
	public void verify_CloudantPropertiesBean_from_constructor() {
		final String username = "username1";
		final String password = "password1";
		final String host = "host1";
		final String protocol = "ftp";
		final int port = 4321;
		final String database = "database";
		
		cloudantPropertiesBean = new CloudantPropertiesBean(
				username, 
				password, 
				host, 
				protocol, 
				port, 
				database);
		
		assertThat(cloudantPropertiesBean.getUsername(), is(equalTo(username)));
		assertThat(cloudantPropertiesBean.getPassword(), is(equalTo(password)));
		assertThat(cloudantPropertiesBean.getHost(), is(equalTo(host)));
		assertThat(cloudantPropertiesBean.getProtocol(), is(equalTo(protocol)));
		assertThat(cloudantPropertiesBean.getPort(), is(equalTo(port)));
		assertThat(cloudantPropertiesBean.getDatabase(), is(equalTo(database)));
	}
	
	@Test
	public void verify_buildURL() throws MalformedURLException {
		URL expected = new URL(cloudantPropertiesBean.getProtocol(), cloudantPropertiesBean.getHost(), cloudantPropertiesBean.getPort(), "");
		
		assertThat(cloudantPropertiesBean.buildURL(), is(equalTo(expected)));
	}
}
