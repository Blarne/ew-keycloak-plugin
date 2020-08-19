/*
 * Copyright (c) 2019-2029 Karumien s.r.o.
 *
 * Karumien s.r.o. is not responsible for defects arising from 
 * unauthorized changes to the source code.
 */
package com.karumien.cloud.sso.spi;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.logging.Logger;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.models.UserModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karumien.cloud.sso.api.model.MessageParameter;
import com.karumien.cloud.sso.api.model.MessageParameters;
import com.karumien.cloud.sso.api.model.MessageRecipient;
import com.karumien.cloud.sso.api.model.MessageRecipients;
import com.karumien.cloud.sso.api.model.MessageRequest;
import com.karumien.cloud.sso.api.model.ParameterType;

/**
 * Sending emails over Notification Service.
 *
 * @author <a href="miroslav.svoboda@karumien.com">Miroslav Svoboda</a>
 * @since 1.0, 6. 4. 2020 4:25:34 
 */
public class NotificationServiceProvider implements EmailSenderProvider {

    private static final Logger log = Logger.getLogger(NotificationServiceProvider.class);
    private final static String DEFAULT_DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    
    private static final List<String> SUPPORTED = Arrays.asList("RESET_PASSWORD", "TEST_MESSAGE");

    
    private String environment;
    
    public NotificationServiceProvider(String environmet) {
        this.environment = environmet;
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // do nothing
    }

    
    protected MessageRequest messageTest() {
        MessageRequest message = new MessageRequest();
        message.setMessageCode("TEST");
        message.setClientName(" ");
        message.setClientNo(" ");
        
        List<MessageParameter> params = new ArrayList<>();
        MessageParameter mp = new MessageParameter();
        mp.setIsVariable(false);
        mp.setParameterType(ParameterType.BODY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);        
        mp.setValue(LocalDateTime.now().format(formatter));
        params.add(mp);
        
        
        MessageParameters mps = new MessageParameters();
        mps.setMessageParameter(params);

        message.setParameters(mps);
        return message;
    }
    
    protected MessageRequest messageResetPassword(String link, String username, Integer validity) {
        MessageRequest message = new MessageRequest();
        message.setMessageCode("PASSWORDREQUESTKEYCLOAK");
        message.setClientName(" ");
        message.setClientNo(" ");
        
        List<MessageParameter> params = new ArrayList<>();
        MessageParameter mp = new MessageParameter();
        mp.setIsVariable(false);
        mp.setParameterType(ParameterType.BODY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);        
        mp.setValue(LocalDateTime.now().format(formatter));
        params.add(mp);   
        
        mp = new MessageParameter();
        mp.setIsVariable(false);
        mp.setParameterType(ParameterType.BODY);
        mp.setValue(link);
        params.add(mp);   
        
        mp = new MessageParameter();
        mp.setIsVariable(false);
        mp.setParameterType(ParameterType.BODY);
        mp.setValue(username);
        params.add(mp);   
        
        mp = new MessageParameter();
        mp.setIsVariable(false);
        mp.setParameterType(ParameterType.BODY);
        mp.setValue(String.valueOf(validity));
        params.add(mp);   
        
        MessageParameters mps = new MessageParameters();
        mps.setMessageParameter(params);

        message.setParameters(mps);
        return message; 
    }
        
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Map<String, String> config, UserModel user, String subject, String textBody, String htmlBody) throws EmailException {
        
        if (!SUPPORTED.contains(subject)) {
            return;
        }
        
        // boolean auth = "true".equals(config.get("auth"));
        boolean ssl = true; // "true".equals(config.get("ssl"));
        
        MessageRequest message = null;
        
        // Test message
        if ("TEST_MESSAGE".equals(subject)) {
            message = messageTest();
        }
        
        // Reset password message
        if ("RESET_PASSWORD".equals(subject)) {

            String[] data = htmlBody.split("<p>");
            message = messageResetPassword(data[0], user.getUsername(), minutesToHours(data[1]));
        }

        if (ssl && !"PROD".equalsIgnoreCase(environment)) {
            disableCertificatesValidation();
        }
        
        List<MessageRecipient> recipients = new ArrayList<>();
        MessageRecipient recipient = new MessageRecipient();
        recipient.setName(user.getFirstName() + " " + user.getLastName());
        recipient.setAddress(user.getEmail());
        recipients.add(recipient);        
        
        message.setSource(config.get("fromDisplayName"));
        message.setLanguage(user.getFirstAttribute("locale"));
      

        MessageRecipients mrs = new MessageRecipients();
        mrs.setMessageRecipient(recipients);
        
        message.setRecipients(mrs);
        
        String requestUrl = "https://api-test.wag-test.local/soap2rest/message-sender/InsertMessageRequest";
        		
//        		String.format("http%s://%s"+ (config.get("port") != null ? ":" + config.get("port"): "") +"%s", 
//            (ssl ? "s" : ""), config.get("host"), config.get("replyToDisplayName"));
//
//        if (auth) {
//            log.info("client: " + config.get("user") + ", secret: " + config.get("password"));
//        }
        
//        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
//        cfg.setDefaultEncoding("UTF-8");
//        cfg.setClassForTemplateLoading(NotificationServiceProvider.class, "/templates");

        Map<String, Object> context = new HashMap<>();
        context.put("message", message);
        
        createAndSend(context, config, message, requestUrl, true);

        log.info(environment + "->" + message);
    }

	private void createAndSend(Map<String, Object> context, Map<String, String> config, MessageRequest message, String requestUrl, boolean firstAttempt) {
		try {
//            Template template = cfg.getTemplate("message-rest.ftl");
//            template.setClassicCompatible(true);
//            template.process(context, out);
//            
//            log.info(out.toString());     
//            
            HttpPost post = new HttpPost(requestUrl);
            
            post.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate");
            post.addHeader(HttpHeaders.USER_AGENT, "Java Apache HttpClient / " + config.get("fromDisplayName"));
            post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            post.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+ getAccessToken(config));
            
            ObjectMapper mapper = new ObjectMapper(); 
            
            post.setEntity(new StringEntity(" { messages: [ " + mapper.writeValueAsString(message) + " ] } "));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {
                log.info(response.getStatusLine().getStatusCode());
                if(response.getStatusLine().getStatusCode() == 401 && firstAttempt) {
                	//only one repeat to avoid deep recursion. If the second attempt with fresh token is also 401 it doesn't make sense to try again anyway.
                	AuthTokenProvider.getInstance().clearTokenCache();
                	createAndSend(context, config, message, requestUrl, false);
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
	}
    
    private String getAccessToken(Map<String, String> config) throws ClientProtocolException, IOException {
		return AuthTokenProvider.getInstance().getAccessToken();
	}

	private Integer minutesToHours(String minutes) {
        try {
        return Integer.valueOf(minutes) / 60;
        } catch (Exception e) {
            return 12;
        }
    }

    public static void disableCertificatesValidation() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }
    
}
