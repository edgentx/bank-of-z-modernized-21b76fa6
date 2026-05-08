package com.example.domain.tellersession.model;

public class SessionTimeoutViolationException extends RuntimeException {
    public SessionTimeoutViolationException(String message) {
        super(message);
    }
}
