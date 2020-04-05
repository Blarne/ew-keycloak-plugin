/*
 * Copyright (c) 2019-2029 Karumien s.r.o.
 *
 * Karumien s.r.o. is not responsible for defects arising from
 * unauthorized changes to the source code.
 */
package com.karumien.cloud.sso.spi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

/**
 * Store last login time information to users attribute {@link LastLoginEventListenerProvider#ATTR_LAST_LOGIN}.
 *
 * @author <a href="miroslav.svoboda@karumien.com">Miroslav Svoboda</a>
 * @since 1.0, 5. 4. 2020 22:18:44
 */
public class LastLoginEventListenerProvider implements EventListenerProvider
// , ServerInfoAwareProviderFactory
{
//    private static final Logger log = Logger.getLogger(LastLoginEventListenerProvider.class);
    private final static String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Map<EventType, String> EVENT_MAP = new HashMap<>();
    
    static {
        EVENT_MAP.put(EventType.LOGIN, "lastLogin");
        EVENT_MAP.put(EventType.LOGIN_ERROR, "lastLoginError");
        EVENT_MAP.put(EventType.LOGOUT, "lastLogout");
    }


    private KeycloakSession session;
    // private List<Event> events;

    public LastLoginEventListenerProvider(KeycloakSession session, List<Event> events) {
        this.session = session;
        // this.events = events;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(Event event) {
    
        if (event.getType() != EventType.LOGIN && event.getType() != EventType.LOGIN_ERROR && event.getType() != EventType.LOGOUT) {
            return;
        }

        RealmModel realm = session.realms().getRealm(event.getRealmId());
        UserModel user = session.users().getUserById(event.getUserId(), realm);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
        user.setSingleAttribute(EVENT_MAP.get(event.getType()), LocalDateTime.now().format(formatter) + ", " + event.getIpAddress()+ ", " 
                + event.getSessionId() + ", " + event.getClientId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        session = null;
    }

    // @Override
    // public Map<String, String> getOperationalInfo() {
    // // TODO Auto-generated method stub
    // return null;
    // }

}
