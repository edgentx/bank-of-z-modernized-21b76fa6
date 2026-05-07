package com.example.domain;

/**
 * Base class for Domain Exceptions.
 * Consolidates validation and domain logic errors.
 */
public class DomainError extends RuntimeException {

    public DomainError(String message) {
        super(message);
    }

    public DomainError(String message, Throwable cause) {
        super(message, cause);
    }
}
