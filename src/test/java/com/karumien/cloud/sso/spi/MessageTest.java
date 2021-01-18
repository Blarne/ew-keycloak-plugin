package com.karumien.cloud.sso.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karumien.cloud.sso.api.model.MessageRecipient;
import com.karumien.cloud.sso.api.model.MessageRecipients;
import com.karumien.cloud.sso.api.model.MessageRequest;
 
public class MessageTest {
	
	private static final String SEND_API_METHOD = "/soap2rest/message-sender/InsertMessageRequest";

    public static void main(String[] args) throws IOException  {

        NotificationServiceProvider p = new NotificationServiceProvider("UNIT");
        MessageRequest message = p.messageTest();
        List<MessageRecipient> recipients = new ArrayList<>();
        MessageRecipient recipient = new MessageRecipient();
        recipient.setName("IvoS");
        recipient.setAddress("ismajstrla@gmail.com");
        recipients.add(recipient);        
        
        message.setSource("CIAM");
        message.setLanguage("en");
        
        MessageRecipients mrs = new MessageRecipients();
        mrs.setMessageRecipient(recipients);
        
        message.setRecipients(mrs);
         
//        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
////        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
//        cfg.setDefaultEncoding("UTF-8");
////        cfg.setLocale(Locale.US);
//        cfg.setClassForTemplateLoading(NotificationServiceProvider.class, "/templates");

        Map<String, Object> context = new HashMap<>();
        context.put("message", message);
////        
////        StringWriter out = new StringWriter();
//        
//        Template template = cfg.getTemplate("message-soap.ftl");
//        //template.setClassicCompatible(true);
//        template.process(context, out);
        
        ObjectMapper mapper = new ObjectMapper(); 
        mapper.setSerializationInclusion(Include.NON_NULL);
        
        String req = "{ \"messages\": { \"message\": [ " + mapper.writeValueAsString(message) + " ] } } ";
        System.out.println(req);
        //added for test send
        send(req); 
    }

	private static void send(String out) {
		try {
			TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();

			BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
					.setConnectionManager(connectionManager).build();

			String requestUrl = System.getenv("API_GW_URL") + SEND_API_METHOD; //"https://api-test.wag-test.local"
			HttpPost post = new HttpPost(requestUrl);
			post.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate");
			post.addHeader(HttpHeaders.USER_AGENT, "local test");
			post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			
			String token = AuthTokenProvider.getInstance().getAccessToken();
			post.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+ token);
			post.setEntity(new StringEntity(out));
			
			CloseableHttpResponse response = httpClient.execute(post);
			System.out.println(response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}

}
// { "messages": { "message": [ {"language":"en", "clientName":" ", "clientNo":" ","messageCode":"TEST","parameters":{ "messageParameter": [{"parameterType":"Body","isVariable":false,"value":"18.08.2020 19:16"}]},"recipients": { "messageRecipient" : [{"address":"ismajstrla@gmail.com","name":"IvoS"}]},"source":"CIAM"} ] } } 