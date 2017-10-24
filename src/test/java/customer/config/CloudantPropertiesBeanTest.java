package customer.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
	private CloudantPropertiesBean customerBean;
	
	@Test
	public void verifyProperties() {
		assertThat(customerBean.getUsername(), is(equalTo("username")));
		assertThat(customerBean.getPassword(), is(equalTo("password")));
		assertThat(customerBean.getHost(), is(equalTo("host")));
		assertThat(customerBean.getProtocol(), is(equalTo("https")));
		assertThat(customerBean.getPort(), is(equalTo(1234)));
		assertThat(customerBean.getDatabase(), is(equalTo("customers")));
	}
}
