package com.example.domain.tellersession.model;

public class SessionNotAuthenticatedException extends RuntimeException {
    public SessionNotAuthenticatedException(String message) {
        super(message);
    }
}
