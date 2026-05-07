package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transaction {
    private final TransactionId id;
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();
    private boolean isPosted = false;

    // Max allowed balance invariant constant for demonstration
    private static final BigDecimal MAX_BALANCE = new BigDecimal("1000000000.00");

    public Transaction(TransactionId id) {
        this.id = id;
    }

    public void execute(PostDepositCmd cmd) {
        // Invariant 1: Transaction amounts must be greater than zero.
        if (cmd.amount().amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered or deleted once posted.
        if (this.isPosted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: A transaction must result in a valid account balance (enforced via aggregate validation).
        // Simulating a check against a hypothetical max balance limit or other business logic.
        if (cmd.amount().amount().compareTo(MAX_BALANCE) > 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation). Amount exceeds max balance.");
        }

        // Business Logic: Apply State Change
        // In a real CQRS/Event Sourcing model, we would apply the event to update state.
        // Here, we mark the aggregate as posted.
        this.isPosted = true;

        // Raise Event
        DepositPostedEvent event = DepositPostedEvent.create(this.id, cmd.accountNumber(), cmd.amount());
        this.uncommittedEvents.add(event);
    }

    public List<DomainEvent> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    public TransactionId getId() {
        return id;
    }
}
