package customer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cloudant.client.api.Database;

import customer.config.CloudantPropertiesBean;
import customer.config.JWTPropertiesBean;

public class CustomerController_initTest {
	
	@Mock CloudantPropertiesBean cloudantProperties;
	@Mock JWTPropertiesBean jwtProperties;
	@Mock CustomerDatabaseBuilder dbBuilder;
	
	@InjectMocks CustomerController controller = new CustomerController();
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void canary() {
		assertThat(true, is(true));
	}
	
	@Test
	public void verify_init() throws Exception {
		when(jwtProperties.getEnabled()).thenReturn(false);
		
		final Database db = mock(Database.class);
		when(dbBuilder.createDatabase()).thenReturn(db);
		
		try {
			controller.init();
			
			assertThat(controller.getCloudantDatabase(), is(db));
			assertThat(controller.jwtChecker.isEnabled(), is(false));
		} catch (MalformedURLException e) {
			Assert.fail("Exception initializing controller");
		}
	}
}
