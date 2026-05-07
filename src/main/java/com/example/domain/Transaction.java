package com.example.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final List<S10Event> uncommittedEvents = new ArrayList<>();
    private boolean posted = false;

    public Transaction(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public List<S10Event> getUncommittedEvents() {
        return uncommittedEvents;
    }

    public void execute(PostDepositCmd cmd) {
        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainError("Transaction amounts must be greater than zero");
        }

        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.posted) {
            throw new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: A transaction must result in a valid account balance
        // (Mocked logic: assume valid for now, as we don't have Account aggregate state here)
        // In a real app, we would check the account balance.
        // if (resultsInInvalidBalance(cmd)) { throw ... }

        apply(cmd);
        
        // Mark as posted
        this.posted = true;
    }

    private void apply(PostDepositCmd cmd) {
        DepositPostedEvent event = new DepositPostedEvent(
            cmd.getTransactionId(),
            cmd.getAmount(),
            cmd.getCurrency(),
            cmd.getAccountNumber()
        );
        this.uncommittedEvents.add(event);
    }
}
