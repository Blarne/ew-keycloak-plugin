package com.karumien.cloud.sso.spi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.karumien.cloud.sso.api.model.MessageRecipient;
import com.karumien.cloud.sso.api.model.MessageRequest;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MessageTest {

    public static void main(String[] args) throws IOException, TemplateException {

        NotificationServiceProvider p = new NotificationServiceProvider("UNIT");
        MessageRequest message = p.messageTest();
        List<MessageRecipient> recipients = new ArrayList<>();
        MessageRecipient recipient = new MessageRecipient();
        recipient.setName("jaja a paja");
        recipient.setAddress("jaja@seznam.cz");
        recipients.add(recipient);        
        
        message.setSource("unit");
        message.setLanguage("en");
        message.setRecipients(recipients);
        
        
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
//        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
        cfg.setDefaultEncoding("UTF-8");
//        cfg.setLocale(Locale.US);
        cfg.setClassForTemplateLoading(NotificationServiceProvider.class, "/templates");

        Map<String, Object> context = new HashMap<>();
        context.put("message", message);
        
        StringWriter out = new StringWriter();
        
        Template template = cfg.getTemplate("message-soap.ftl");
        //template.setClassicCompatible(true);
        template.process(context, out);
    }

}
