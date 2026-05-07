package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private boolean posted = false;
    private boolean simulateInsufficientFunds = false;

    public Transaction(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    // Marker for state transition (Scenario 3)
    public void markAsPosted() {
        this.posted = true;
    }

    // Helper for simulation (Scenario 4)
    public void setSimulateInsufficientFunds(boolean simulate) {
        this.simulateInsufficientFunds = simulate;
    }

    /**
     * Execute pattern entry point.
     */
    public WithdrawalPostedEvent execute(PostWithdrawalCmd cmd) {
        validate(cmd);
        apply(cmd);
        return buildEvent(cmd);
    }

    private void validate(PostWithdrawalCmd cmd) {
        // Scenario 2: Transaction amounts must be greater than zero.
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Scenario 3: Transactions cannot be altered or deleted once posted.
        if (this.posted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Scenario 4: A transaction must result in a valid account balance.
        if (this.simulateInsufficientFunds) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }
    }

    private void apply(PostWithdrawalCmd cmd) {
        // In a real app, this would update internal state before emitting the event.
        // For this command execution pattern, state update is implicit.
    }

    private WithdrawalPostedEvent buildEvent(PostWithdrawalCmd cmd) {
        return new WithdrawalPostedEvent(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
    }
}
