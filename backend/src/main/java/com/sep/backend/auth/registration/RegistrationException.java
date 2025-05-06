package com.sep.backend.auth.registration;

import org.springframework.security.core.AuthenticationException;

public class RegistrationException extends AuthenticationException {
    public RegistrationException(String message) {
        super(message);
    }
}
