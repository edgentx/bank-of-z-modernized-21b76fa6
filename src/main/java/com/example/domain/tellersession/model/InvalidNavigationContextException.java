package com.example.domain.tellersession.model;

/**
 * Domain Exception indicating a navigation attempt failed due to invalid session context (e.g., locked state).
 */
public class InvalidNavigationContextException extends RuntimeException {
    public InvalidNavigationContextException(String message) {
        super(message);
    }
}
