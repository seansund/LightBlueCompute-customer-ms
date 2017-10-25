package customer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cloudant.client.api.Database;

import customer.config.CloudantPropertiesBean;

public class CustomerDatabaseBuilderTest {
	@Mock CloudantPropertiesBean cloudantProperties;
	@Mock CloudantDatabaseBuilder cloudantDbBuilder;
	@InjectMocks CustomerDatabaseBuilder dbBuilder;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void canary() {
		assertThat(true, is(true));
	}
	
	@Test
	public void buildDatabase_indexExists() throws MalformedURLException {
		Database expected = mock(Database.class);
		
		when(cloudantDbBuilder.build(any())).thenReturn(expected);
		when(expected.contains("_design/username_searchIndex")).thenReturn(true);
		
		Database actual = dbBuilder.createDatabase();
		
		assertThat(actual, is(expected));
		verify(expected, times(0)).save(any());
	}
	
	@Test
	public void buildDatabase_indexNotExists() throws MalformedURLException {
		Database expected = mock(Database.class);
		
		final String indexId = "_design/username_searchIndex";
		
		when(cloudantDbBuilder.build(any())).thenReturn(expected);
		when(expected.contains(indexId)).thenReturn(false);
		when(expected.save(any())).thenReturn(null);
		
		Database actual = dbBuilder.createDatabase();
		
		assertThat(actual, is(expected));
		
		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Map> savedCaptor = ArgumentCaptor.forClass(Map.class);
		verify(expected, times(1)).save(savedCaptor.capture());
		assertThat(savedCaptor.getValue().get("_id"), is(equalTo(indexId)));
		assertThat(savedCaptor.getValue().containsKey("indexes"), is(true));
	}
}
