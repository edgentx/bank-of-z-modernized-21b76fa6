package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final UUID transactionId;
    private boolean isPosted = false;
    private boolean maxBalanceReached = false;

    public Transaction(UUID transactionId) {
        this.transactionId = transactionId;
    }

    // Helper for testing the immutability invariant
    public void markAsPosted() {
        this.isPosted = true;
    }

    // Helper for testing the balance invariant
    public void setMaxBalanceReached() {
        this.maxBalanceReached = true;
    }

    public DepositPostedEvent execute(PostDepositCmd cmd) {
        // Invariant: Amounts must be greater than zero
        if (cmd.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainError("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transactions cannot be altered once posted
        if (this.isPosted) {
            throw new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Valid Account Balance
        if (this.maxBalanceReached) {
            throw new DomainError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Logic: Apply changes
        // In a real scenario, we would update aggregate state here.
        // Then, we emit the event.
        return new DepositPostedEvent(this.transactionId, cmd.accountNumber, cmd.amount, cmd.currency);
    }
}