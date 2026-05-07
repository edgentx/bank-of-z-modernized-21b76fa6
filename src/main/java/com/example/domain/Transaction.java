package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private boolean posted = false;
    private BigDecimal maxBalance = BigDecimal.valueOf(1000000); // Default limit
    private final List<Object> uncommittedEvents = new ArrayList<>();

    public Transaction(UUID id) {
        this.id = id;
    }

    // Getter for ID
    public UUID getId() {
        return id;
    }

    // Invariant Helper for Testing
    public void setMaxBalance(BigDecimal max) {
        this.maxBalance = max;
    }

    // Invariant Helper for Testing
    public void markPosted() {
        this.posted = true;
    }

    public List<Object> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }

    public void execute(PostDepositCmd cmd) {
        // 1. Check Invariant: Cannot alter posted transactions
        if (this.posted) {
            throw new DomainException("Transaction cannot be altered once posted.");
        }

        // 2. Check Invariant: Amount must be > 0
        if (cmd.amount == null || cmd.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // 3. Check Invariant: Valid Account Balance (Simulation)
        // In a real aggregate, we would load the current balance. Here we check against the limit.
        if (cmd.amount.compareTo(maxBalance) > 0) {
            throw new DomainException("A transaction must result in a valid account balance.");
        }

        // Apply
        apply(new DepositPostedEvent(this.id, cmd.accountNumber, cmd.amount, cmd.currency));
        this.posted = true; // Update state
    }

    private void apply(DepositPostedEvent event) {
        uncommittedEvents.add(event);
    }
}