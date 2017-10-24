package customer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

import customer.config.CloudantPropertiesBean;

@Component
public class CustomerDatabaseBuilder {
    private static Logger logger =  LoggerFactory.getLogger(CustomerDatabaseBuilder.class);
    
    @Autowired
    private CloudantPropertiesBean cloudantProperties;
    @Autowired
    private CloudantDatabaseBuilder cloudantClientBuilder;
    
    public Database createDatabase() throws MalformedURLException {
        logger.debug(cloudantProperties.toString());
        
    		Database cloudant = cloudantClientBuilder.build(cloudantProperties);
        
        // create the design document if it doesn't exist
        if (!cloudant.contains("_design/username_searchIndex")) {
            
            final Map<String, Object> view_ddoc = new HashMap<String, Object>();
            view_ddoc.put("_id", "_design/username_searchIndex");
            view_ddoc.put("indexes", putMap(
            		"usernames", putMap(
                		"index", "function(doc){index(\"usernames\", doc.username); }")));
            
            cloudant.save(view_ddoc);        
        }
        
        return cloudant;
    }
	protected Map<String, Object> putMap(String key, Object value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		return map;
	}
}
