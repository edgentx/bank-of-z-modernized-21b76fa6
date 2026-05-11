package com.example.domain.tellersession.model;

public class SessionInactiveException extends RuntimeException {
    public SessionInactiveException(String message) {
        super(message);
    }
}
