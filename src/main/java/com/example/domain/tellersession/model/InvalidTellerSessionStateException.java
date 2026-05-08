package com.example.domain.tellersession.model;

/**
 * Specific domain exception for TellerSession state violations.
 * Used to distinguish business rule violations from system errors.
 */
public class InvalidTellerSessionStateException extends RuntimeException {
    public InvalidTellerSessionStateException(String message) {
        super(message);
    }
}
