package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

/**
 * Transaction Aggregate Root.
 * Handles state changes via Command execution.
 */
public class Transaction {

    private final UUID id;
    private final DomainConfig config;
    private BigDecimal balance = BigDecimal.ZERO;
    private boolean isPosted = false;

    // Protected constructor for reconstruction/repositories if needed,
    // but we expose a public one for the aggregate creation.
    public Transaction(UUID id, DomainConfig config) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        if (config == null) throw new IllegalArgumentException("Config cannot be null");
        this.id = id;
        this.config = config;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Used by test setup to simulate a specific pre-condition.
     * In production, this would be derived from event sourcing history.
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isPosted() {
        return isPosted;
    }

    /**
     * Executes a command against this aggregate.
     * @param cmd The command to execute.
     * @return The resulting event.
     * @throws DomainError if invariants are violated.
     */
    public DepositPostedEvent execute(PostDepositCmd cmd) {
        // Validate IDs match if applicable, or purely aggregate based logic.
        // Here we assume the cmd targets this aggregate.

        // 1. Validate Amount
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainError("Transaction amounts must be greater than zero.");
        }

        // 2. Validate Immutability (Once posted, no more updates)
        // The scenario implies that a Transaction can only be acted upon once if it results in a 'Posted' state.
        if (this.isPosted) {
            throw new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // 3. Calculate Potential Balance
        BigDecimal newBalance = this.balance.add(cmd.amount());

        // 4. Validate Account Balance Limit
        if (newBalance.compareTo(config.getMaxTransactionAmount()) > 0) {
            throw new DomainError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply State Change (Mutating the aggregate)
        this.balance = newBalance;
        this.isPosted = true;

        // Return Event
        return new DepositPostedEvent(
            this.id,
            cmd.accountNumber(),
            cmd.amount(),
            cmd.currency(),
            this.balance
        );
    }

    // Visitor/Dispatch pattern support can be added here if needed for generic Command handling
    public DepositPostedEvent execute(Object cmd) {
        if (cmd instanceof PostDepositCmd pdc) {
            return execute(pdc);
        }
        throw new UnsupportedOperationException("Unknown command type: " + cmd.getClass().getSimpleName());
    }
}
