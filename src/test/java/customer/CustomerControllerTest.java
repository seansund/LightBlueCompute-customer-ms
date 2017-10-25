package customer;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.fasterxml.jackson.databind.ObjectMapper;

import customer.model.Customer;

public class CustomerControllerTest {
	
	private Database cloudantDb;
	private CustomerController controller;
	
	private MockMvc mockMvc;
	
	private Customer customer;
	
	@Before
	public void init() {
		cloudantDb = mock(Database.class);
		controller = new CustomerController(cloudantDb, false, "");
		
		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
				.build();
		
		customer = new Customer("1", "username", "password", "firstName", "lastName", "email", "imageUrl");
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
		customers.add(customer);
		
		when(cloudantDb.findByIndex("{ \"selector\": { \"username\": \"test\" } }", Customer.class)).thenReturn(customers);
		
		mockMvc.perform(get("/customer/search?username=test"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$[0].username", is(equalTo("username"))))
			.andExpect(jsonPath("$[0].customerId", is(equalTo("1"))));
	}
	
	@Test
	public void search_bad() throws Exception {
		mockMvc.perform(get("/customer/search"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(equalTo("Missing username"))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void search_exception() throws Exception {
		final List<Customer> customers = new ArrayList<Customer>();
		customers.add(customer);
		
		when(cloudantDb.findByIndex("{ \"selector\": { \"username\": \"test\" } }", Customer.class)).thenThrow(Exception.class);
		
		mockMvc.perform(get("/customer/search?username=test"))
			.andExpect(status().is5xxServerError());
	}
	
	@Test
	public void getById_noHeader() throws Exception {
		mockMvc.perform(get("/customer/1"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(equalTo("Missing header: ibm-app-user"))));
	}
	
	@Test
	public void getById_noMatchId() throws Exception {
		mockMvc.perform(get("/customer/1").header("ibm-app-user", "2"))
			.andExpect(status().isUnauthorized());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getById_noDocument() throws Exception {
		final String id = "1";
		
		when(cloudantDb.find(Customer.class, id)).thenThrow(NoDocumentException.class);
		
		mockMvc.perform(get("/customer/" + id).header("ibm-app-user", id))
			.andExpect(status().isNotFound())
			.andExpect(content().string(is(equalTo("Customer with ID " + id + " not found"))));
	}
	
	@Test
	public void getById_good() throws Exception {
		final String id = "1";
		
		when(cloudantDb.find(Customer.class, id)).thenReturn(customer);
		
		mockMvc.perform(get("/customer/" + id).header("ibm-app-user", id))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.customerId", is(equalTo(customer.getCustomerId()))));
	}
	
	@Test
	public void getCustomers_noHeader() throws Exception {
		mockMvc.perform(get("/customer"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(equalTo("Missing header: ibm-app-user"))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getCustomers_noDocument() throws Exception {
		final String id = "1";
		
		when(cloudantDb.find(Customer.class, id)).thenThrow(NoDocumentException.class);
		
		mockMvc.perform(get("/customer").header("ibm-app-user", id))
			.andExpect(status().is5xxServerError());
	}
	
	@Test
	public void getCustomers_good() throws Exception {
		final String id = "1";
		
		when(cloudantDb.find(Customer.class, id)).thenReturn(customer);
		
		mockMvc.perform(get("/customer").header("ibm-app-user", id))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.customerId", is(equalTo(customer.getCustomerId()))));
	}
	
	@Test
	public void getCustomers_jwtEnabled_success() throws Exception {
		final String id = "1";
		
		JWTChecker checker = mock(JWTChecker.class);
		when(checker.isEnabled()).thenReturn(true);
		when(checker.checkJWTHeader(any())).thenReturn("");
		
		controller = new CustomerController(cloudantDb, checker);
		
		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
				.build();
		
		when(cloudantDb.find(Customer.class, id)).thenReturn(customer);
		
		mockMvc.perform(get("/customer").header("ibm-app-user", id))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.customerId", is(equalTo(customer.getCustomerId()))));
	}
	
	@Test
	public void getCustomers_jwtEnabled_failure() throws Exception {
		final String id = "1";
		
		JWTChecker checker = mock(JWTChecker.class);
		when(checker.isEnabled()).thenReturn(true);
		when(checker.checkJWTHeader(any())).thenReturn("Invalid JWT");
		
		controller = new CustomerController(cloudantDb, checker);
		
		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
				.build();
		
		when(cloudantDb.find(Customer.class, id)).thenReturn(customer);
		
		mockMvc.perform(get("/customer").header("ibm-app-user", id))
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void create_customerIdExists() throws Exception {
		final String id = "1";
		
		when(cloudantDb.contains(id)).thenReturn(true);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(post("/customer").contentType(MediaType.APPLICATION_JSON).content(jsonString))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(equalTo("Id " + id + " already exists"))));
	}
	
	@Test
	public void create_customerNameExists() throws Exception {
		final String id = "1";
		final List<Customer> customerList = new ArrayList<Customer>();
		customerList.add(customer);
		
		when(cloudantDb.contains(id)).thenReturn(false);
		when(cloudantDb.findByIndex("{ \"selector\": { \"username\": \"" + customer.getUsername() + "\" } }", Customer.class)).thenReturn(customerList);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(post("/customer").contentType(MediaType.APPLICATION_JSON).content(jsonString))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(equalTo("Customer with name " + customer.getUsername() + " already exists"))));
	}
	
	@Test
	public void create_saveError() throws Exception {
		final String id = "1";
		when(cloudantDb.contains(id)).thenReturn(false);
		
		final List<Customer> customerList = new ArrayList<Customer>();
		when(cloudantDb.findByIndex("{ \"selector\": { \"username\": \"" + customer.getUsername() + "\" } }", Customer.class)).thenReturn(customerList);
		
		final String errorResponse = "Error";
		final Response response = mock(Response.class);
		when(response.getError()).thenReturn(errorResponse);
		
		when(cloudantDb.save(any())).thenReturn(response);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(post("/customer").contentType(MediaType.APPLICATION_JSON).content(jsonString))
			.andExpect(status().is5xxServerError())
			.andExpect(content().string(is(equalTo(errorResponse))));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void create_saveException() throws Exception {
		final String id = "1";
		when(cloudantDb.contains(id)).thenReturn(false);
		
		final List<Customer> customerList = new ArrayList<Customer>();
		when(cloudantDb.findByIndex("{ \"selector\": { \"username\": \"" + customer.getUsername() + "\" } }", Customer.class)).thenReturn(customerList);
		
		when(cloudantDb.save(any())).thenThrow(Exception.class);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(post("/customer").contentType(MediaType.APPLICATION_JSON).content(jsonString))
			.andExpect(status().is5xxServerError())
			.andExpect(content().string(startsWith("Error creating customer:")));
	}

	@Test
	public void create_saveSuccess() throws Exception {
		final String id = "1";
		when(cloudantDb.contains(id)).thenReturn(false);
		
		final List<Customer> customerList = new ArrayList<Customer>();
		when(cloudantDb.findByIndex("{ \"selector\": { \"username\": \"" + customer.getUsername() + "\" } }", Customer.class)).thenReturn(customerList);
		
		final String errorResponse = null;
		final Response response = mock(Response.class);
		when(response.getError()).thenReturn(errorResponse);
		when(response.getId()).thenReturn(id);
		
		when(cloudantDb.save(any())).thenReturn(response);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(post("/customer").contentType(MediaType.APPLICATION_JSON).content(jsonString))
			.andExpect(status().isCreated())
			.andExpect(header().string("location", endsWith("/customer/" + id)));
	}

	@Test
	public void create_saveSuccess_nullId() throws Exception {
		final String id = "1";
		when(cloudantDb.contains(id)).thenReturn(false);
		
		final List<Customer> customerList = new ArrayList<Customer>();
		when(cloudantDb.findByIndex("{ \"selector\": { \"username\": \"" + customer.getUsername() + "\" } }", Customer.class)).thenReturn(customerList);
		
		final String errorResponse = null;
		final Response response = mock(Response.class);
		when(response.getError()).thenReturn(errorResponse);
		when(response.getId()).thenReturn(id);
		
		when(cloudantDb.save(any())).thenReturn(response);
		
		customer.setCustomerId(null);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(post("/customer").contentType(MediaType.APPLICATION_JSON).content(jsonString))
			.andExpect(status().isCreated())
			.andExpect(header().string("location", endsWith("/customer/" + id)));
	}
	
	@Test
	public void update_noHeader() throws Exception {
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(put("/customer/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(equalTo("Missing header: ibm-app-user"))));
	}
	
	@Test
	public void update_noMatchId() throws Exception {
		final String id = customer.getCustomerId();
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(put("/customer/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)
				.header("ibm-app-user", "100"))
			.andExpect(status().isUnauthorized());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void update_noDocument() throws Exception {
		final String id = customer.getCustomerId();
		
		when(cloudantDb.find(Customer.class, id)).thenThrow(NoDocumentException.class);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(put("/customer/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)
				.header("ibm-app-user", id))
			.andExpect(status().isNotFound())
			.andExpect(content().string(is(equalTo("Customer with ID " + id + " not found"))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void update_error() throws Exception {
		final String id = customer.getCustomerId();
		
		when(cloudantDb.find(Customer.class, id)).thenThrow(Exception.class);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(put("/customer/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)
				.header("ibm-app-user", id))
			.andExpect(status().is5xxServerError())
			.andExpect(content().string(startsWith("Error updating customer:")));
	}
	
	@Test
	public void update_success() throws Exception {
		final String id = customer.getCustomerId();
		
		final Customer customerSpy = spy(Customer.class);
		when(cloudantDb.find(Customer.class, id)).thenReturn(customerSpy);
		
		//when(cloudantDb.save(any())).thenReturn(null);
		
		ObjectMapper jsonBuilder = new ObjectMapper();
		String jsonString = jsonBuilder.writeValueAsString(customer);
		System.out.println(jsonString);
		
		mockMvc.perform(put("/customer/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)
				.header("ibm-app-user", id))
			.andExpect(status().isOk());
		
		verify(customerSpy).setFirstName(customer.getFirstName());
		verify(customerSpy).setLastName(customer.getLastName());
		verify(customerSpy).setImageUrl(customer.getImageUrl());
		verify(customerSpy).setEmail(customer.getEmail());
		verify(customerSpy).setPassword(customer.getPassword());
		
		verify(cloudantDb).save(customerSpy);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_noDocument() throws Exception {
		final String id = "1";
		
		when(cloudantDb.find(Customer.class, id)).thenThrow(NoDocumentException.class);
		
		mockMvc.perform(delete("/customer/" + id))
			.andExpect(status().isNotFound())
			.andExpect(content().string(is(equalTo("Customer with ID " + id + " not found"))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void delete_error() throws Exception {
		final String id = "1";
		
		when(cloudantDb.find(Customer.class, id)).thenThrow(Exception.class);
		
		mockMvc.perform(delete("/customer/" + id))
			.andExpect(status().is5xxServerError())
			.andExpect(content().string(startsWith("Error deleting customer:")));
	}
	
	@Test
	public void delete_success() throws Exception {
		final String id = "1";
		
		final Customer customerSpy = spy(Customer.class);
		
		when(cloudantDb.find(Customer.class, id)).thenReturn(customerSpy);
		when(cloudantDb.remove(any())).thenReturn(null);
		
		mockMvc.perform(delete("/customer/" + id))
			.andExpect(status().isOk());
		
		verify(cloudantDb, times(1)).remove(customerSpy);
	}
}
