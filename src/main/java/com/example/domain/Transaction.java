package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction Aggregate Root.
 * Handles the logic for debiting funds via PostWithdrawalCmd.
 */
public class Transaction {

    private final UUID transactionId;
    private String accountNumber;
    private BigDecimal currentBalance;
    private BigDecimal amount;
    private String currency;
    private boolean isPosted;
    private final List<Object> uncommittedEvents = new ArrayList<>();

    // Constructor for new Transaction creation
    public Transaction(UUID transactionId, BigDecimal currentBalance) {
        this.transactionId = transactionId;
        this.currentBalance = currentBalance;
        this.isPosted = false;
    }

    public UUID getId() {
        return transactionId;
    }

    public List<Object> getUncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    /**
     * Execute method implementing the PostWithdrawalCmd logic.
     */
    public WithdrawalPostedEvent execute(PostWithdrawalCmd cmd) {
        // Invariant: Transactions cannot be altered once posted
        if (this.isPosted) {
            throw new DomainViolationException(
                "Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction."
            );
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount() == null || cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainViolationException("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transaction must result in a valid account balance (e.g., non-negative for standard accounts)
        // Assuming valid means >= 0 for this domain context.
        BigDecimal projectedBalance = this.currentBalance.subtract(cmd.getAmount());
        if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainViolationException(
                "A transaction must result in a valid account balance (enforced via aggregate validation)."
            );
        }

        // Apply state changes
        this.accountNumber = cmd.getAccountNumber();
        this.amount = cmd.getAmount();
        this.currency = cmd.getCurrency();
        this.currentBalance = projectedBalance;
        this.isPosted = true;

        // Create event
        WithdrawalPostedEvent event = new WithdrawalPostedEvent(
            this.transactionId,
            this.accountNumber,
            this.amount,
            this.currency,
            this.currentBalance
        );

        uncommittedEvents.add(event);
        return event;
    }

    // Getters for testing/verification
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public boolean isPosted() { return isPosted; }
    public BigDecimal getCurrentBalance() { return currentBalance; }

    /**
     * Helper to simulate a state where the transaction is already posted.
     * In a real app, this would be loaded from the event store.
     */
    public void markAsPostedAlready(BigDecimal existingBalance) {
        this.isPosted = true;
        this.amount = BigDecimal.ONE; // Dummy value
        this.accountNumber = "EXISTING";
        this.currentBalance = existingBalance;
    }
}