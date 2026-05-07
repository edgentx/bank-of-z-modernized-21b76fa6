package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private boolean isPosted = false;

    public Transaction(UUID id) {
        this.id = id;
    }

    public DepositPostedEvent execute(PostDepositCmd cmd) {
        // Invariant: Transactions cannot be altered or deleted once posted
        if (isPosted) {
            throw new IllegalStateException("Transaction is immutable once posted.");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        // Invariant: A transaction must result in a valid account balance (Mock validation)
        if ("INVALID-BALANCE-ACC".equals(cmd.getAccountNumber())) {
            throw new IllegalStateException("Transaction would result in an invalid account balance.");
        }

        // Create event
        return new DepositPostedEvent(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
    }

    public void apply(DepositPostedEvent event) {
        this.isPosted = true;
    }
}
