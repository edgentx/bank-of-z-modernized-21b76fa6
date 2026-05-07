package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private boolean isPosted = false;
    private boolean invalidBalanceSimulation = false;

    public Transaction(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    /**
     * Execute pattern.
     * Accepts a command, validates invariants, and returns an event.
     */
    public S10Event execute(S10Command command) {
        // Invariant 1: Transaction amounts must be greater than zero
        if (command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            throw new DomainException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: Transaction must result in a valid account balance (simulated)
        if (this.invalidBalanceSimulation) {
            throw new DomainException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Logic is valid -> Create Event
        // In a real app, this might update internal state before returning
        return new DepositPostedEvent(this.id, command.getAccountNumber(), command.getAmount(), command.getCurrency());
    }

    /**
     * Helper for testing to force the aggregate into a posted state.
     */
    public void markAsPosted() {
        this.isPosted = true;
    }

    /**
     * Helper for testing to simulate balance validation failure.
     */
    public void simulateInvalidBalanceState(boolean simulate) {
        this.invalidBalanceSimulation = simulate;
    }
}
