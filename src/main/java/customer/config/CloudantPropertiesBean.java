package customer.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="spring.application.cloudant")
public class CloudantPropertiesBean {
	
	private String username;
	private String password;
	private String host;
	private String protocol;
	private int port;
	private String database;
	
	public CloudantPropertiesBean() {
		super();
	}
	public CloudantPropertiesBean(String username, String password, String host, String protocol, int port,
			String database) {
		super();
		this.username = username;
		this.password = password;
		this.host = host;
		this.protocol = protocol;
		this.port = port;
		this.database = database;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	
	public URL buildURL() throws MalformedURLException {
		return new URL(getProtocol() +"://"+ getHost() + ":" + getPort());
	}
}
