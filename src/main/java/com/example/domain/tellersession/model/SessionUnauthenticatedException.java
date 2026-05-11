package com.example.domain.tellersession.model;

public class SessionUnauthenticatedException extends RuntimeException {
    public SessionUnauthenticatedException(String message) {
        super(message);
    }
}
