package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Transaction Aggregate Root.
 * Implements Execute(cmd) pattern.
 */
public class Transaction {

    private final UUID transactionId;
    private final String accountNumber;
    private final DomainConfig config;
    private final List<Object> events = new ArrayList<>();
    
    private BigDecimal currentBalance;
    private boolean posted = false;

    public Transaction(UUID transactionId, String accountNumber, DomainConfig config) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.config = config;
        this.currentBalance = BigDecimal.ZERO;
    }

    /**
     * Executes a command against this aggregate.
     * Throws DomainException if invariants are violated.
     * Returns void; check getEvents() for emitted events on success.
     */
    public void execute(PostDepositCmd cmd) {
        // 1. Invariant: Amount must be > 0
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // 2. Invariant: Cannot alter posted transactions
        if (this.posted) {
            throw new DomainException("Transactions cannot be altered or deleted once posted.");
        }

        // 3. Invariant: Transaction amount within limits
        if (cmd.amount().compareTo(config.maxTransactionAmount()) > 0) {
             throw new DomainException("Transaction amount exceeds maximum allowed amount.");
        }

        // 4. Invariant: Resulting balance is valid
        // Assuming this aggregate tracks the balance for the validation context
        BigDecimal newBalance = this.currentBalance.add(cmd.amount());
        if (newBalance.compareTo(config.maxAccountBalance()) > 0) {
            throw new DomainException("A transaction must result in a valid account balance (exceeds max).");
        }

        // Apply state change
        this.currentBalance = newBalance;

        // Emit Event
        DepositPostedEvent event = new DepositPostedEvent(
            cmd.accountNumber(),
            cmd.amount(),
            cmd.currency(),
            java.time.LocalDateTime.now()
        );
        
        this.events.add(event);
    }

    // Used for testing to simulate persisted state
    public void markPosted() {
        this.posted = true;
    }

    public List<Object> getEvents() {
        return List.copyOf(events);
    }

    public UUID getId() {
        return transactionId;
    }

    public BigDecimal getBalance() {
        return currentBalance;
    }
}
