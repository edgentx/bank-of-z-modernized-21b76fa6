package com.example.domain;

import java.util.UUID;

public class S12Event {
    private final String type;
    private final UUID transactionId;
    private final UUID originalTransactionId;

    public S12Event(UUID transactionId, UUID originalTransactionId) {
        this.type = "TransactionReversed";
        this.transactionId = transactionId;
        this.originalTransactionId = originalTransactionId;
    }

    public String getType() {
        return type;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getOriginalTransactionId() {
        return originalTransactionId;
    }
}
