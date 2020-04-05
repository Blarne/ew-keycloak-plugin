/*
 * Copyright (c) 2019-2029 Karumien s.r.o.
 *
 * Karumien s.r.o. is not responsible for defects arising from 
 * unauthorized changes to the source code.
 */
package com.karumien.cloud.sso.spi;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * Event Listener factory for {@link LastLoginEventListenerProvider}.
 *
 * @author <a href="miroslav.svoboda@karumien.com">Miroslav Svoboda</a>
 * @since 1.0, 5. 4. 2020 23:26:34 
 */
public class LastLoginEventListenerProviderFactory implements EventListenerProviderFactory {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return "last-login-event-listener";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new LastLoginEventListenerProvider(session, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Config.Scope config) {
        // do nothing     
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
    
}
