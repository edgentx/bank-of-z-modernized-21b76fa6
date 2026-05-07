package com.example.domain;

/**
 * Domain Exception representing a violation of business rules.
 */
public class DomainError extends RuntimeException {

    public DomainError(String message) {
        super(message);
    }
}