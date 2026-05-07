package com.example.domain;

import java.util.UUID;

public record TransactionId(UUID id) {
    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(String id) {
        return new TransactionId(UUID.fromString(id));
    }
}
