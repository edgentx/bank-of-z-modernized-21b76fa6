package com.example.domain;

/**
 * Base exception for Domain logic violations.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
