package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction Aggregate Root.
 * Handles business logic regarding transaction postings and validations.
 */
public class Transaction {

    private final UUID id;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;
    private TransactionStatus status;
    private final List<Object> uncommittedEvents = new ArrayList<>();

    // Mocking balance check for validation purposes
    // In a real app, this would come from the Account aggregate or a read model
    private BigDecimal currentAccountBalance; 

    public enum TransactionStatus {
        PENDING, POSTED
    }

    // Private constructor for factory methods or reconstruction
    private Transaction(UUID id, String accountNumber, BigDecimal amount, String currency, TransactionStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    /**
     * Factory method to create a new pending transaction context for testing commands.
     * Note: In this domain context, the 'Transaction' entity acts as the handler for the command.
     */
    public static Transaction create(UUID id, String accountNumber, BigDecimal amount, String currency, BigDecimal currentBalance) {
        var tx = new Transaction(id, accountNumber, amount, currency, TransactionStatus.PENDING);
        tx.currentAccountBalance = currentBalance;
        return tx;
    }

    /**
     * Execute the PostDepositCmd command.
     * Enforces invariants and emits resulting events.
     */
    public void execute(PostDepositCmd cmd) {
        // Invariant: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainViolationException("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transactions cannot be altered once posted
        if (this.status == TransactionStatus.POSTED) {
            throw new DomainViolationException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Valid account balance (Simulation of balance check)
        // Assuming arbitrary limits or validation logic for the account balance
        if (this.currentAccountBalance.add(cmd.amount()).compareTo(new BigDecimal("1000000")) > 0) {
             // Simplified check for S-10 scenario: "transaction must result in a valid account balance"
             throw new DomainViolationException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state change
        this.status = TransactionStatus.POSTED;
        
        // Record Event
        var event = DepositPostedEvent.create(this.id, cmd.accountNumber(), cmd.amount(), cmd.currency());
        this.uncommittedEvents.add(event);
    }

    public List<Object> getUncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public TransactionStatus getStatus() {
        return status;
    }
    
    public UUID getId() {
        return id;
    }

    public static class DomainViolationException extends RuntimeException {
        public DomainViolationException(String message) {
            super(message);
        }
    }
}
