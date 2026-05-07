package com.example.domain;

import java.util.UUID;

/**
 * Command to reverse a previously posted transaction.
 * Part of the S-12 Story implementation.
 */
public class ReverseTransactionCmd implements S12Command {

    private final UUID originalTransactionId;

    public ReverseTransactionCmd(UUID originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    public UUID getOriginalTransactionId() {
        return originalTransactionId;
    }
}
