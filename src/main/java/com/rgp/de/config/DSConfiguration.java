package com.rgp.de.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public final class DSConfiguration implements CommandLineRunner {

	@Value("${docusign}")
	public String dsPrivateKey;

	public static final String CLIENT_ID;

	public static final String IMPERSONATED_USER_GUID;

	public static final String TARGET_ACCOUNT_ID;

	public static final String OAUTH_REDIRECT_URI = "https://www.docusign.com";

	public static String PRIVATE_KEY;

	public static final String AUTHENTICATION_URL = "https://account-d.docusign.com";

	public static final String DS_AUTH_SERVER;

	public static final String API = "restapi/v2";

	public static final String PERMISSION_SCOPES = "signature%20impersonation";

	public static final String JWT_SCOPE = "signature";

	public static final String AUD() {
		if (DS_AUTH_SERVER != null && DS_AUTH_SERVER.startsWith("https://"))
			return DS_AUTH_SERVER.substring(8);
		else if (DS_AUTH_SERVER != null && DS_AUTH_SERVER.startsWith("http://"))
			return DS_AUTH_SERVER.substring(7);

		return DS_AUTH_SERVER;
	}

	static {

		// Try load from environment variables
		Map<String, String> config = loadFromEnv();

		if (config == null) {
			// Try load from properties file
			config = loadFromProperties();
		}

		CLIENT_ID = fetchValue(config, "DS_CLIENT_ID");
		IMPERSONATED_USER_GUID = fetchValue(config, "DS_IMPERSONATED_USER_GUID");
		TARGET_ACCOUNT_ID = fetchValue(config, "DS_TARGET_ACCOUNT_ID");
		DS_AUTH_SERVER = fetchValue(config, "DS_AUTH_SERVER"); // use account.docusign.com for production
	}

	/**
	 * fetch configuration value by key.
	 *
	 * @param config preloaded configuration key/value map
	 * @param name   key of value
	 * @return value as string or default empty string
	 */
	private static String fetchValue(Map<String, String> config, String name) {
		String val = config.get(name);

		if ("DS_TARGET_ACCOUNT_ID".equals(name) && "FALSE".equals(val)) {
			return null;
		}

		return ((val != null) ? val : "");
	}

	/**
	 * This method check if environment variables exists and load it into Map
	 *
	 * @return Map of key/value of environment variables if exists otherwise, return
	 *         null
	 */
	private static Map<String, String> loadFromEnv() {
		String clientId = System.getenv("DS_CLIENT_ID");

		if (clientId != null && clientId.length() > 0) {
			return System.getenv();
		}

		return null;
	}

	/**
	 * This method load properties located in config.properties file in the working
	 * directory.
	 *
	 * @return Map of key/value of properties
	 */
	private static Map<String, String> loadFromProperties() {
		Properties properties = new Properties();
		InputStream input = null;

		try {
			input = DSConfiguration.class.getResourceAsStream("/application.properties");
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException("can not load configuration file", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					throw new RuntimeException("error occurs will closing input stream: ", e);
				}
			}
		}

		Set<Map.Entry<Object, Object>> set = properties.entrySet();
		Map<String, String> mapFromSet = new HashMap<String, String>();

		for (Map.Entry<Object, Object> entry : set) {
			mapFromSet.put((String) entry.getKey(), (String) entry.getValue());
		}
		return mapFromSet;
	}

	@Override
	public void run(String... varl) throws Exception {
		PRIVATE_KEY = dsPrivateKey;
	}
}
