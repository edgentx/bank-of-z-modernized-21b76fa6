package com.example.domain;

import java.util.UUID;

/**
 * Event emitted when a transaction is successfully reversed.
 * Part of the S-12 Story implementation.
 */
public class TransactionReversedEvent implements S12Event {

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
