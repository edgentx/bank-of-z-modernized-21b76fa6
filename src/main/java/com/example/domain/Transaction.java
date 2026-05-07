package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private boolean isPosted = false;
    private BigDecimal maximumAllowedBalance = BigDecimal.valueOf(1000000); // Default large limit
    private final List<Object> uncommittedEvents = new ArrayList<>();

    public Transaction(UUID id) {
        this.id = id;
    }

    // Accessors for testing/validation purposes
    public boolean isPosted() {
        return isPosted;
    }

    public void setMaximumAllowedBalance(BigDecimal max) {
        this.maximumAllowedBalance = max;
    }

    public void markAsPosted() {
        this.isPosted = true;
    }

    public List<Object> getUncommittedEvents() {
        return uncommittedEvents;
    }

    public boolean hasUncommittedEvents() {
        return !uncommittedEvents.isEmpty();
    }

    /**
     * Execute pattern dispatcher.
     */
    public void execute(Object command) {
        if (command instanceof PostDepositCmd) {
            apply((PostDepositCmd) command);
        } else {
            throw new IllegalArgumentException("Unknown command type: " + command.getClass());
        }
    }

    private void apply(PostDepositCmd cmd) {
        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainError("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transactions cannot be altered once posted
        if (this.isPosted) {
            throw new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Valid account balance (Aggregate Validation)
        // For this aggregate, we enforce a maximum balance cap to demonstrate the invariant.
        if (cmd.getAmount().compareTo(maximumAllowedBalance) > 0) {
            throw new DomainError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // If invariants pass, emit event
        raiseEvent(new DepositPostedEvent(
            this.id,
            cmd.getAccountNumber(),
            cmd.getAmount(),
            cmd.getCurrency()
        ));
    }

    private void raiseEvent(Object event) {
        uncommittedEvents.add(event);
        // In a real app, we would also mutate the state here (e.g., applyEvent)
        this.isPosted = true; 
    }
}
