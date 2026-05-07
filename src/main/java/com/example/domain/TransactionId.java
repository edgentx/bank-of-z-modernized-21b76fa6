package com.example.domain;

public record TransactionId(String value) {
    public TransactionId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("ID cannot be blank");
    }
}
