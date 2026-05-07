package com.example.domain;

import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId {
        if (value == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }
}
