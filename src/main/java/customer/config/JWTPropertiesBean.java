package customer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="jwt")
public class JWTPropertiesBean {
	
	private boolean enabled;
	private String key;
	
	public JWTPropertiesBean() {
		super();
	}
	
	public JWTPropertiesBean(boolean enabled, String key) {
		super();
		this.enabled = enabled;
		this.key = key;
	}

	public boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(boolean value) {
		this.enabled = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String value) {
		this.key = value;
	}
}
