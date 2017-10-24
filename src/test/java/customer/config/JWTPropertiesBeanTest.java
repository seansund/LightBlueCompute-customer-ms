package customer.config;

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
public class JWTPropertiesBeanTest {
	
	@Autowired
	private JWTPropertiesBean bean;
	
	@Test
	public void verifyProperties() {
		assertThat(bean.getEnabled(), is(false));
	}
}
