package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction Aggregate Root.
 */
public class Transaction {

    private final UUID id;
    private boolean posted = false;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    
    // In a real app, we'd store uncommitted events here
    private final List<Object> uncommittedEvents = new ArrayList<>();

    public Transaction(UUID id) {
        this.id = id;
    }

    /**
     * Public Execute method following the Execute(cmd) pattern.
     */
    public void execute(Object command) {
        if (command instanceof PostDepositCmd cmd) {
            validateAndPostDeposit(cmd);
        }
        // Handle other commands here if necessary
    }

    private void validateAndPostDeposit(PostDepositCmd cmd) {
        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.posted) {
            throw new ValidationError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount() == null || cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationError("Transaction amounts must be greater than zero.");
        }

        // Invariant: A transaction must result in a valid account balance (Aggregate validation)
        // Example Rule: Single deposit cannot exceed 1 Billion (arbitrary validation logic)
        if (cmd.getAmount().compareTo(new BigDecimal("1000000000")) > 0) {
            throw new ValidationError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply the state change
        applyDepositPosted(cmd);
    }

    private void applyDepositPosted(PostDepositCmd cmd) {
        this.accountNumber = cmd.getAccountNumber();
        this.amount = cmd.getAmount();
        this.currency = cmd.getCurrency();
        this.posted = true;

        // Record the event
        S10Event event = new S10Event(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
        this.uncommittedEvents.add(event);
    }

    // --- Accessors and Test Helper Methods ---

    public boolean isPosted() {
        return posted;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
    
    /**
     * Internal helper for testing the "already posted" scenario.
     * In a real repository, we would rehydrate the aggregate from events 
     * leading to a posted state.
     */
    public void markAsPostedInternal() {
        this.posted = true;
    }
}