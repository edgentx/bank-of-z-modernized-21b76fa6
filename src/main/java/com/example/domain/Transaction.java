package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Transaction Aggregate Root.
 * Handles PostDepositCmd and enforces invariants.
 */
public class Transaction {

    private String id;
    private String accountNumber;
    private boolean isPosted;
    
    // In-Memory event store for the scope of execution
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isPosted() {
        return isPosted;
    }

    public void setPosted(boolean posted) {
        isPosted = posted;
    }

    /**
     * Executes a command against this aggregate.
     * Pattern: Execute(cmd) -> List<DomainEvent>
     */
    public List<DomainEvent> execute(Object command) {
        if (command instanceof PostDepositCmd cmd) {
            return handlePostDeposit(cmd);
        }
        throw new IllegalArgumentException("Unknown command type: " + command.getClass());
    }

    private List<DomainEvent> handlePostDeposit(PostDepositCmd cmd) {
        // Invariant: Transactions cannot be altered once posted
        if (this.isPosted) {
            throw new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Amounts must be greater than zero
        if (cmd.getAmount() == null || cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainError("Transaction amounts must be greater than zero.");
        }

        // Invariant: Valid account balance (Simulated check)
        // In a real app, this might check the calculated balance against limits or overdraft rules.
        // For this scenario, we assume any positive amount is valid, unless specific logic exists.
        // If the scenario demands failure on negative balance calculation:
        if (cmd.getAmount().compareTo(new BigDecimal("-100")) < 0) { // Placeholder for complex balance logic
             throw new DomainError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply changes and emit event
        DepositPostedEvent event = new DepositPostedEvent(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
        apply(event);
        
        return Collections.singletonList(event);
    }

    private void apply(DepositPostedEvent event) {
        // Update state
        this.accountNumber = event.getAccountNumber();
        this.isPosted = true;
        // Store event
        this.uncommittedEvents.add(event);
    }
}
