package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final String accountNumber;
    private BigDecimal balance;
    private final List<S10Event> uncommittedEvents = new ArrayList<>();
    private boolean isPosted = false;

    public Transaction(UUID id, String accountNumber, BigDecimal initialBalance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public UUID getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isPosted() {
        return isPosted;
    }

    public List<S10Event> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    /**
     * Core command execution logic enforcing invariants.
     */
    public S10Event execute(S10Command cmd) {
        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            throw new DomainViolationException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainViolationException("Transaction amounts must be greater than zero.");
        }

        // Calculate potential balance (Simplified aggregate validation)
        BigDecimal newBalance = this.balance.add(cmd.amount());

        // Invariant: A transaction must result in a valid account balance
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainViolationException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state change
        this.balance = newBalance;
        this.isPosted = true;

        // Create event
        S10Event event = new S10Event(
            UUID.randomUUID(),
            cmd.transactionId(),
            cmd.accountNumber(),
            cmd.amount(),
            cmd.currency(),
            java.time.Instant.now()
        );

        uncommittedEvents.add(event);
        return event;
    }

    public static class DomainViolationException extends RuntimeException {
        public DomainViolationException(String message) {
            super(message);
        }
    }
}