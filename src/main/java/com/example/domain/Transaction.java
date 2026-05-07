package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private boolean isPosted;
    // Simulated constraint for balance validation
    private static final BigDecimal MAX_ALLOWED_AMOUNT = new BigDecimal("1000000.00");

    public Transaction(UUID id) {
        this.id = id;
        this.isPosted = false;
    }

    // Used for testing to simulate state where transaction is already posted
    public void markAsPosted() {
        this.isPosted = true;
    }

    public Object execute(PostDepositCmd cmd) {
        // Invariant: Transaction amounts must be greater than zero.
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transactions cannot be altered or deleted once posted.
        if (this.isPosted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: A transaction must result in a valid account balance (Simulated via aggregate limit)
        if (cmd.amount().compareTo(MAX_ALLOWED_AMOUNT) > 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state change
        this.isPosted = true;

        // Emit event
        return new DepostedEvent(this.id, cmd.accountNumber(), cmd.amount(), cmd.currency());
    }
}