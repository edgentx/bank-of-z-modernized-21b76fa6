package com.example.domain;

public class DomainViolationException extends RuntimeException {
    public DomainViolationException(String message) {
        super(message);
    }
}
