package de.kasperczyk.dinnerdiary.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class BadCredentialsListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(BadCredentialsListener.class);

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent) {
        String emailAddress = authenticationFailureBadCredentialsEvent.getAuthentication().getPrincipal().toString();
        LOG.error("Failed authentication attempt - provided email address '" + emailAddress + "' and the given password do not match");
    }
}
