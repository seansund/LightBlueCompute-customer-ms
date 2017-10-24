package customer;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

import customer.config.CloudantPropertiesBean;

@Component
public class CloudantDatabaseBuilder {
    private static Logger logger =  LoggerFactory.getLogger(CloudantDatabaseBuilder.class);
    private ClientBuilder clientBuilder;
    
	public Database build(CloudantPropertiesBean cloudantProperties) throws MalformedURLException {
        logger.info("Connecting to cloudant at: " + cloudantProperties.buildURL());
		final CloudantClient cloudantClient = clientBuilder(cloudantProperties.buildURL())
		        .username(cloudantProperties.getUsername())
		        .password(cloudantProperties.getPassword())
		        .build();
		
		return cloudantClient.database(cloudantProperties.getDatabase(), true);
	}
	protected ClientBuilder clientBuilder(URL url) {
		if (clientBuilder == null) {
			clientBuilder = ClientBuilder.url(url);
		}
		return clientBuilder;
	}
}
