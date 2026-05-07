package com.example.domain;

public class DomainError extends RuntimeException {
    public DomainError(String message) {
        super(message);
    }
}