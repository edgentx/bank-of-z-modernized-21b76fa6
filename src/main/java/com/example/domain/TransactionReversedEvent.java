package com.example.domain;

import java.util.UUID;

public class TransactionReversedEvent {
    private final UUID transactionId;
    private final UUID originalTransactionId;

    public TransactionReversedEvent(UUID transactionId, UUID originalTransactionId) {
        this.transactionId = transactionId;
        this.originalTransactionId = originalTransactionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getOriginalTransactionId() {
        return originalTransactionId;
    }
}