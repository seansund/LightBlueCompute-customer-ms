package customer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cloudant.client.api.Database;

import customer.config.CloudantPropertiesBean;
import customer.config.JWTPropertiesBean;
import customer.model.Customer;

public class CustomerControllerTest {
	
	@Mock CloudantPropertiesBean cloudantProperties;
	@Mock JWTPropertiesBean jwtProperties;
	@Mock CustomerDatabaseBuilder dbBuilder;
	
	@InjectMocks CustomerController controller = new CustomerController();
	
	private MockMvc mockMvc;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(jwtProperties.getEnabled()).thenReturn(false);
		try {
			controller.init();
		} catch (MalformedURLException e) {
			Assert.fail("Exception initializing test");
		}
		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
				.build();
	}
	
	@Test
	public void canary() {
		assertThat(true, is(true));
	}
	
	@Test
	public void check() throws Exception {
		mockMvc.perform(get("/check"))
			.andExpect(status().isOk())
			.andExpect(content().string(is(equalTo("it works!"))));
	}
	
	@Test
	public void search_good() throws Exception {
		final List<Customer> customers = new ArrayList<Customer>();
		customers.add(new Customer("1", "username", "password", "firstName", "lastName", "email", "imageUrl"));
		final Database db = mock(Database.class);
		
		when(dbBuilder.createDatabase()).thenReturn(db);
		
		when(db.findByIndex("{ \"selector\": { \"username\": \"test\" } }", Customer.class)).thenReturn(customers);
		
		try {
			controller.init();
		} catch (MalformedURLException e) {
			Assert.fail("Exception initializing controller");
		}
		
		mockMvc.perform(get("/customer/search?username=test"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$[0].username", is(equalTo("username"))))
			.andExpect(jsonPath("$[0].customerId", is(equalTo("1"))));
	}
	
	@Test
	public void search_bad() throws Exception {
		try {
			controller.init();
		} catch (MalformedURLException e) {
			Assert.fail("Exception initializing controller");
		}
		
		mockMvc.perform(get("/customer/search"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(equalTo("Missing username"))));
	}
	
	@Test
	public void search_exception() throws Exception {
		final List<Customer> customers = new ArrayList<Customer>();
		customers.add(new Customer("1", "username", "password", "firstName", "lastName", "email", "imageUrl"));
		final Database db = mock(Database.class);
		
		when(dbBuilder.createDatabase()).thenReturn(db);
		
		when(db.findByIndex("{ \"selector\": { \"username\": \"test\" } }", Customer.class)).thenThrow(Exception.class);
		
		try {
			controller.init();
		} catch (MalformedURLException e) {
			Assert.fail("Exception initializing controller");
		}
		
		mockMvc.perform(get("/customer/search?username=test"))
			.andExpect(status().is5xxServerError());
	}

}
