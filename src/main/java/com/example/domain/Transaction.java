package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Aggregate Root.
 * Handles business logic regarding transaction posting and invariants.
 */
public class Transaction {

    private final String id;
    private final String accountNumber;
    private final BigDecimal currentBalance; // Snapshot for validation context
    private final List<Object> uncommittedEvents = new ArrayList<>();
    private boolean posted = false;

    // Constructor for creating a NEW transaction to be posted
    public Transaction(String id, String accountNumber, BigDecimal currentBalance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
    }

    public String getId() {
        return id;
    }

    public List<Object> getUncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void clearEvents() {
        uncommittedEvents.clear();
    }

    /**
     * Execute method for the PostDepositCmd.
     * Enforces invariants before applying state change.
     */
    public void execute(PostDepositCmd cmd) {
        // Invariant: Transactions cannot be altered once posted
        if (this.posted) {
            throw new DomainException("Transactions cannot be altered or deleted once posted");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero");
        }

        // Calculate potential balance
        BigDecimal newBalance = this.currentBalance.add(cmd.amount());

        // Invariant: A transaction must result in a valid account balance
        // (Example Rule: Balance cannot be negative for this account type)
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("A transaction must result in a valid account balance (Insufficient Funds)");
        }

        // If invariants pass, apply the event
        apply(new DepositPostedEvent(
                this.id,
                cmd.accountNumber(),
                cmd.amount(),
                cmd.currency(),
                java.time.Instant.now()
        ));
    }

    private void apply(DepositPostedEvent event) {
        this.uncommittedEvents.add(event);
        this.posted = true; // Update state
    }

    public static class DomainException extends RuntimeException {
        public DomainException(String message) {
            super(message);
        }
    }
}