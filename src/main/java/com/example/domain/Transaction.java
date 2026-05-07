package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private TransactionState state;
    private final List<Object> uncommittedEvents = new ArrayList<>();

    // Needed for testing scenarios where we inject specific validation behavior
    protected Transaction(UUID id, TransactionState state) {
        this.id = id;
        this.state = state;
    }

    public void execute(PostDepositCmd cmd) {
        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.state == TransactionState.POSTED) {
            throw new DomainException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // Invariant: A transaction must result in a valid account balance
        // This method can be overridden in test setup or implemented using a repository/service injection
        validateBalance(cmd);

        // Apply state changes
        this.state = TransactionState.POSTED;

        // Emit Event
        DepositPostedEvent event = new DepositPostedEvent(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
        this.uncommittedEvents.add(event);
    }

    // Hook for test scenarios enforcing specific failures
    protected void validateBalance(PostDepositCmd cmd) {
        // Default implementation assumes valid balance if no specific business logic is provided yet
        // Actual logic would check a balance store via repository
    }

    public List<Object> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }
}
