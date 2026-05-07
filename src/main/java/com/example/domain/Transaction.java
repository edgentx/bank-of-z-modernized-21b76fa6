package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction Aggregate Root.
 * Manages state consistency and invariant enforcement for transaction operations.
 */
public class Transaction {

    private final UUID transactionId;
    private final String accountNumber;
    private final DomainConfig config;
    private BigDecimal balance;
    private final List<DomainEvent> events = new LinkedList<>();
    private boolean posted = false;

    // Constructor for aggregate creation/initialization
    public Transaction(UUID transactionId, String accountNumber, DomainConfig config) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.config = config;
        this.balance = BigDecimal.ZERO;
    }

    // Getters required for testing and state inspection
    public UUID getId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<DomainEvent> getEvents() {
        return List.copyOf(events);
    }

    public boolean isPosted() {
        return posted;
    }

    /**
     * Sets balance directly. Used in test setup to simulate aggregate state hydration.
     * In a real repository hydration, this would be part of a factory or private loader.
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * Sets the posted flag. Used in test setup to simulate an immutable transaction.
     */
    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    /**
     * Executes the PostDepositCmd command.
     * Enforces invariants before applying state changes and emitting events.
     */
    public void execute(PostDepositCmd cmd) {
        // Validate pre-conditions
        if (posted) {
            throw new IllegalStateException("Transaction cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        if (cmd.amount() == null || cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Calculate potential new balance
        BigDecimal potentialBalance = this.balance.add(cmd.amount());

        // Validate account balance invariant
        if (potentialBalance.compareTo(config.maxAccountBalance()) > 0) {
            throw new IllegalArgumentException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state change
        this.balance = potentialBalance;
        this.posted = true;

        // Emit event
        DepositPostedEvent event = new DepositPostedEvent(
            cmd.transactionId(),
            cmd.accountNumber(),
            cmd.amount(),
            cmd.currency().getCurrencyCode()
        );
        this.events.add(event);
    }
}
