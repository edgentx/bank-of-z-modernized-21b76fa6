package com.example.domain.tellersession.model;

public class UnauthenticatedTellerException extends RuntimeException {
    public UnauthenticatedTellerException(String message) {
        super(message);
    }
}
