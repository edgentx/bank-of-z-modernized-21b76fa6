package com.example.domain.tellersession.model;

/**
 * Domain exception indicating a violation of TellerSession invariants.
 */
public class InvalidTellerSessionStateException extends RuntimeException {
    public InvalidTellerSessionStateException(String message) {
        super(message);
    }
}
