package com.karumien.cloud.sso.spi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthTokenProvider {
	private final Logger logger = LoggerFactory.getLogger(AuthTokenProvider.class);
	
	public static final String RESPONSE_KEY_ACCESS_TOKEN = "access_token";
	public static final String RESPONSE_KEY_EXPIRES_IN_SECONDS = "expires_in";
	public static final String CONFIG_KEY_AUTH_URL = "AUTH_URL";
	public static final String CONFIG_KEY_CLIENT_ID = "client_id";
	public static final String CONFIG_KEY_CLIENT_SECRET = "client_secret";
	public static final String CONFIG_KEY_SCOPE = "scope";
	public static final String CONFIG_KEY_API_GW = "api-gw";
	public static final String CONFIG_KEY_API_METHOD = "api-method";
		
	private static final AuthTokenProvider instance = new AuthTokenProvider();
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private String currentToken;
	private Instant tokenExpiration;
	
	/** to be sure that requestor is able to use old token before actual expiration, we call for new token in advance */
	private static final int TOKE_VAIDITY_SECONDS_REDUCE = 10;
	
	private AuthTokenProvider() { }
	
	public static final AuthTokenProvider getInstance() {
		return instance;
	}
	
	public void clearTokenCache() {
		logger.debug("clearing token cache");
		this.currentToken = null;
		this.tokenExpiration = null;
	}
	
	public String getAccessToken() throws IOException {
		Map<String, String> config = new HashMap<>();
		config.put(CONFIG_KEY_AUTH_URL, "https://login.microsoftonline.com/c97a84f5-62bf-4715-beca-fc25af9516f1/oauth2/v2.0/token");
//		config.put(CONFIG_KEY_CLIENT_ID, "b2703990-fb97-4def-940c-acebf41c1303");
//		config.put(CONFIG_KEY_CLIENT_SECRET, "wt0zIXX2Hsit~YyG1.G3U4-8.fQ~0rBM.-");
		config.put(CONFIG_KEY_SCOPE, "api://dih/.default");
		
		String clientId = System.getenv("MS_CLIENT_ID");
		String clientSecret = System.getenv("MS_CLIENT_SECRET");
		
		config.put(CONFIG_KEY_CLIENT_ID, clientId != null ? clientId : "b2703990-fb97-4def-940c-acebf41c1303");
		config.put(CONFIG_KEY_CLIENT_SECRET, clientSecret != null ? clientSecret : "wt0zIXX2Hsit~YyG1.G3U4-8.fQ~0rBM.-");
//		config.put(CONFIG_KEY_AUTH_URL, System.getenv("MS_AUTH_URL"));
//		config.put(CONFIG_KEY_SCOPE, System.getenv("API_GW_SCOPE"));
		return getAccessToken(config);
	}
	 
	private String getAccessToken(Map<String, String> config) throws IOException {
		if(currentToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration) ) {
			return currentToken;
		}
		
		logger.debug("Going to obtain a new token..");
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("grant_type", "client_credentials"));
		params.add(new BasicNameValuePair("client_id", config.get(CONFIG_KEY_CLIENT_ID)));
		params.add(new BasicNameValuePair("client_secret", config.get(CONFIG_KEY_CLIENT_SECRET)));
		params.add(new BasicNameValuePair("scope", config.get(CONFIG_KEY_SCOPE)));
		
		HttpPost post = new HttpPost(config.get(CONFIG_KEY_AUTH_URL));
		post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
				// convert JSON string to Map
				@SuppressWarnings("rawtypes")
				Map res = mapper.readValue(json, Map.class);
				
				currentToken = (String) res.get(RESPONSE_KEY_ACCESS_TOKEN);
				Integer validity = (Integer) res.get(RESPONSE_KEY_EXPIRES_IN_SECONDS);
				if(validity != null) {
					tokenExpiration = Instant.now().plus((validity-TOKE_VAIDITY_SECONDS_REDUCE), ChronoUnit.SECONDS);
				}
				logger.debug("New token validity is {} seconds. Expiration set to {}", validity, tokenExpiration);
				
				return currentToken;
			}
		}

		logger.warn("Something went wrong, no token available.");
		return null;
	}
	
	public static void main(String[] args) {
		try {
			String token1 = getInstance().getAccessToken();
			System.out.println("obtained access token: " + token1);
			Thread.sleep(10000);
			String token2 = getInstance().getAccessToken();
			boolean equals = token1.equals(token2);
			System.out.println("token2 equals original: " + equals);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
