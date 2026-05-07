package com.example.domain;

/**
 * Domain-specific exception representing a violation of business invariants.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
