/*
 * Copyright (c) 2019-2029 Karumien s.r.o.
 *
 * Karumien s.r.o. is not responsible for defects arising from 
 * unauthorized changes to the source code.
 */
package com.karumien.cloud.sso.spi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.models.UserModel;

import com.karumien.cloud.sso.api.model.MessageParameter;
import com.karumien.cloud.sso.api.model.MessageRecipient;
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
    
    private static final List<String> SUPPORTED = Arrays.asList("RESET_PASSWORD");
    
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Map<String, String> config, UserModel user, String subject, String textBody, String htmlBody) throws EmailException {
        
        if (!SUPPORTED.contains(subject)) {
            return;
        }
        
        //log.info(htmlBody);
        
        htmlBody = htmlBody.substring(htmlBody.indexOf("<p>") + 3);
        htmlBody = htmlBody.substring(0, htmlBody.indexOf("</p>"));
        
//        String[] keys = subject.split(",");
//        log.info(keys);
//        String[] vals = htmlBody.split("</p><p>");
//        log.info(vals);
//        
//        Map<String, String> inputs = new HashMap<>();
//        for (int i = 0; i < keys.length; i++) {
//            inputs.put(keys[i], vals[i]);
//        }
        
//        log.info(subject);
//        log.info(textBody);
//        log.info(htmlBody);
//        log.info(environment);
//        for(String key : config.keySet()) {
//            log.info(key + " = " + config.get(key));
//        }
//        log.info(user);
        
        MessageRequest message = new MessageRequest();
        message.setSource("Keycloak");
        message.setLanguage(user.getFirstAttribute("locale"));
        message.setMessageCode("PASSWORDREQUESTKEYCLOAK");
        
        List<MessageRecipient> recipients = new ArrayList<>();
        MessageRecipient recipient = new MessageRecipient();
        recipient.setName(user.getFirstName() + " " + user.getLastName());
        recipient.setAddress(user.getEmail());
        recipients.add(recipient);

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
        mp.setValue(htmlBody);
        params.add(mp);   
        
        mp = new MessageParameter();
        mp.setIsVariable(false);
        mp.setParameterType(ParameterType.BODY);
        mp.setValue(user.getUsername());
        params.add(mp);   
        
        message.setRecipients(recipients);
        message.setParameters(params);
        
        log.info(environment + "->" + message);
    }
    
}
