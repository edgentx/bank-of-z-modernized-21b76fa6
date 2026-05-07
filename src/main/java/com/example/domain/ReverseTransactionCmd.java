package com.example.domain;

import java.util.UUID;

public class ReverseTransactionCmd {
    private final UUID originalTransactionId;

    public ReverseTransactionCmd(UUID originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public UUID getOriginalTransactionId() {
        return originalTransactionId;
    }
}