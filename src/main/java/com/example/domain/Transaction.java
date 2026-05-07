package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction Aggregate Root for S-10.
 * Handles the business logic for posting deposits and enforcing invariants.
 */
public class Transaction {

    private final UUID transactionId;
    private final List<Object> uncommittedEvents = new ArrayList<>();
    
    // Aggregate state
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    
    // Account balance snapshot (simplified for aggregate validation)
    private BigDecimal currentAccountBalance; 

    public enum TransactionStatus {
        PENDING, POSTED
    }

    // Constructor for creating a new Transaction
    public Transaction(UUID transactionId, BigDecimal currentAccountBalance) {
        this.transactionId = transactionId;
        this.currentAccountBalance = currentAccountBalance;
        this.status = TransactionStatus.PENDING;
    }

    /**
     * Executes the PostDepositCmd command.
     * Uses the Execute(cmd) pattern.
     */
    public List<Object> execute(PostDepositCmd cmd) {
        if (!this.transactionId.equals(cmd.transactionId())) {
            throw new IllegalArgumentException("Command ID does not match Aggregate ID");
        }

        // Invariant 1: Transaction amounts must be greater than zero.
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered once posted.
        if (this.status == TransactionStatus.POSTED) {
            throw new DomainException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: Valid account balance check
        // Simulating a balance check: new balance must not be negative (e.g. overdraft protection not enabled)
        BigDecimal projectedBalance = this.currentAccountBalance.add(cmd.amount());
        if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
             throw new DomainException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state changes (Mutating the aggregate state)
        this.accountNumber = cmd.accountNumber();
        this.amount = cmd.amount();
        this.currency = cmd.currency();
        this.status = TransactionStatus.POSTED;
        this.currentAccountBalance = projectedBalance;

        // Create event
        DepositPosted event = DepositPosted.create(cmd);
        this.uncommittedEvents.add(event);

        return List.copyOf(uncommittedEvents);
    }

    public UUID getId() {
        return transactionId;
    }

    public BigDecimal getCurrentBalance() {
        return currentAccountBalance;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    // Test helpers to simulate specific aggregate states (Given steps)
    public void markAsPosted() {
        this.status = TransactionStatus.POSTED;
    }

    public void setCurrentBalance(BigDecimal balance) {
        this.currentAccountBalance = balance;
    }

    public static class DomainException extends RuntimeException {
        public DomainException(String message) {
            super(message);
        }
    }
}
