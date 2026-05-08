package com.example.exceptions;

public class DomainError extends RuntimeException {
    public DomainError(String message) {
        super(message);
    }
}
