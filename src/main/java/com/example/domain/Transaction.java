package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class Transaction {

    private final String id;
    private final String accountNumber;
    private final BigDecimal currentBalance;
    private final Currency currency;
    private boolean isPosted = false;

    /**
     * Constructor for creating/rehydrating the aggregate.
     */
    public Transaction(String id, String accountNumber, BigDecimal currentBalance, Currency currency) {
        if (id == null) throw new DomainError("ID cannot be null");
        this.id = id;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance != null ? currentBalance : BigDecimal.ZERO;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean isPosted() {
        return isPosted;
    }

    /**
     * Helper for testing purposes to simulate an already posted transaction.
     * In a real event-sourced model, this would be derived from past events.
     */
    public void markAsPosted() {
        this.isPosted = true;
    }

    /**
     * Execute pattern entry point.
     */
    public DepositPostedEvent execute(PostDepositCmd cmd) {
        // 1. Validate Invariants based on current state and command content
        
        // Invariant: Transaction amounts must be greater than zero.
        if (cmd.amount() == null || cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainError("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transactions cannot be altered once posted.
        if (this.isPosted) {
            throw new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Account balance validation.
        // Assumption: System rule is balance cannot exceed 1,000,000,000 for this example.
        BigDecimal potentialNewBalance = this.currentBalance.add(cmd.amount());
        if (potentialNewBalance.compareTo(new BigDecimal("1000000000")) > 0) {
             throw new DomainError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // 2. Apply changes (emit event)
        // In a true CQRS/ES system, this might mutate state and return an event to be appended.
        // Here we simply return the event as per requirements.
        this.isPosted = true; // Local state transition

        return new DepositPostedEvent(
            this.id,
            cmd.accountNumber(),
            cmd.amount(),
            cmd.currency()
        );
    }
}
