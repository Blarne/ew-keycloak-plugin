/*
 * Copyright (c) 2019-2029 Karumien s.r.o.
 *
 * Karumien s.r.o. is not responsible for defects arising from 
 * unauthorized changes to the source code.
 */
package com.karumien.cloud.sso.spi;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jboss.logging.Logger;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.models.UserModel;

import com.karumien.cloud.sso.api.model.MessageParameter;
import com.karumien.cloud.sso.api.model.MessageRecipient;
import com.karumien.cloud.sso.api.model.MessageRequest;
import com.karumien.cloud.sso.api.model.ParameterType;

import freemarker.template.Configuration;
import freemarker.template.Template;

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

    
    private MessageRequest messageTest() {
        MessageRequest message = new MessageRequest();
        message.setMessageCode("TEST");
        
        List<MessageParameter> params = new ArrayList<>();
        MessageParameter mp = new MessageParameter();
        mp.setIsVariable(false);
        mp.setParameterType(ParameterType.BODY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);        
        mp.setValue(LocalDateTime.now().format(formatter));
        params.add(mp);   

        message.setParameters(params);
        return message;
    }
    
    private MessageRequest messageResetPassword(String link, String username) {
        MessageRequest message = new MessageRequest();
        message.setMessageCode("PASSWORDREQUESTKEYCLOAK");
        
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
        
        message.setParameters(params);
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
        
        boolean auth = "true".equals(config.get("auth"));
        boolean ssl = "true".equals(config.get("ssl"));
        boolean soap = "true".equals(config.get("starttls"));
        
        MessageRequest message = null;
        
        // Test message
        if ("TEST_MESSAGE".equals(subject)) {
            message = messageTest();
        }
        
        // Reset password message
        if ("RESET_PASSWORD".equals(subject)) {

            String link = htmlBody.substring(htmlBody.indexOf("<p>") + 3);
            link = link.substring(0, htmlBody.indexOf("</p>"));

            message = messageResetPassword(link, user.getUsername());
            
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
        message.setRecipients(recipients);
        
        String requestUrl = String.format("http%s://%s"+ (config.get("port") != null ? ":" + config.get("port"): "") +"%s", 
            (ssl ? "s" : ""), config.get("host"), config.get("replyToDisplayName"));

        log.info("SOAP: " + soap + ", " + requestUrl);
        if (auth) {
            log.info("client: " + config.get("user") + ", secret: " + config.get("password"));
        }
        
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setClassForTemplateLoading(NotificationServiceProvider.class, "templates");

        Map<String, Object> context = new HashMap<>();
        context.put("message", message);

        StringWriter out = new StringWriter();
        
        try {
            Template template = cfg.getTemplate("message-soap.ftl");
            template.process(message, out);
            
            log.info(out.getBuffer().toString());            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        log.info(environment + "->" + message);
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
