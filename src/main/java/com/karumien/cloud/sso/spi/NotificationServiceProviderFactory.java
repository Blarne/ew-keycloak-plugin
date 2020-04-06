/*
 * Copyright (c) 2019-2029 Karumien s.r.o.
 *
 * Karumien s.r.o. is not responsible for defects arising from 
 * unauthorized changes to the source code.
 */
package com.karumien.cloud.sso.spi;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.email.EmailSenderProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * Event Listener factory for {@link NotificationServiceProvider}.
 *
 * @author <a href="miroslav.svoboda@karumien.com">Miroslav Svoboda</a>
 * @since 1.0, 6. 4. 2020 4:25:34 
 */
public class NotificationServiceProviderFactory implements EmailSenderProviderFactory  {

    private static final Logger log = Logger.getLogger("org.keycloak.events");
    private String environment = "dev";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EmailSenderProvider create(KeycloakSession session) {
        return new NotificationServiceProvider(environment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Scope config) {
        if (System.getenv("ENVIRONMENT") != null) {
            environment = System.getenv("ENVIRONMENT");
        }
        if (System.getProperty("spring.application.env") != null) {
            environment = System.getProperty("spring.application.env");
        }
        log.info("ENVIRONMENT="+environment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // do nothing
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
    public String getId() {
        return "default";
    }

    
}
