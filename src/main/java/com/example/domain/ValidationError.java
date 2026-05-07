package com.example.domain;

/**
 * Domain exception representing a validation failure or invariant violation.
 */
public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(message);
    }
}