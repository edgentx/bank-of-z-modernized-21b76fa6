package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private boolean posted = false;
    private final List<Object> uncommittedEvents = new ArrayList<>();

    public Transaction(UUID id) {
        this.id = id;
    }

    public S10Event execute(PostDepositCmd cmd) {
        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero");
        }

        // Invariant: Transactions cannot be altered or deleted once posted
        // (In this simple aggregate, we check a flag. In real CQRS/ES, we check version/state)
        if (this.posted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction");
        }

        // Invariant: A transaction must result in a valid account balance
        // (Simulated validation - e.g. limit check)
        if (cmd.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
             throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation)");
        }

        // Apply logic
        this.posted = true; // In a real system, balance calculation happens here

        // Create event
        DepositPostedEvent event = new DepositPostedEvent(
            cmd.getAccountNumber(), 
            cmd.getAmount(), 
            cmd.getCurrency()
        );
        
        uncommittedEvents.add(event);
        return event;
    }

    public List<Object> getUncommittedEvents() {
        return uncommittedEvents;
    }

    public void markChangesAsCommitted() {
        uncommittedEvents.clear();
    }

    // Inner class for the Event to keep it cohesive or separate file. S10Event.
    // The prompt asks for S10Event type. We'll use this as the return type interface/impl.
    public static class DepositPostedEvent implements S10Event {
        private final UUID accountNumber;
        private final BigDecimal amount;
        private final Currency currency;

        public DepositPostedEvent(UUID accountNumber, BigDecimal amount, Currency currency) {
            this.accountNumber = accountNumber;
            this.amount = amount;
            this.currency = currency;
        }

        public UUID getAccountNumber() { return accountNumber; }
        public BigDecimal getAmount() { return amount; }
        public Currency getCurrency() { return currency; }
    }
}
