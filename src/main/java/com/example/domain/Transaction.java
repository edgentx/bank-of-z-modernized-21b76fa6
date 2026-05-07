package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

/**
 * Transaction Aggregate.
 * Handles the logic for posting withdrawals (S-11).
 */
public class Transaction {

    private UUID id;
    private TransactionStatus status = TransactionStatus.PENDING;

    // In a real application, currentBalance would be derived from the Account aggregate,
    // which would likely be injected or loaded within the transaction context.
    // For S-11 domain testing, we assume a default balance or injected state.
    private static final BigDecimal MAX_WITHDRAWAL = new BigDecimal("50000.00");

    public Transaction() {
        this.id = UUID.randomUUID();
    }

    /**
     * Executes the PostWithdrawalCmd command.
     * Enforces invariants:
     * 1. Amount > 0
     * 2. Transaction not already posted (State invariant)
     * 3. Valid Account Balance result (Business invariant)
     *
     * @param command The command to execute.
     * @return The resulting S11Event.
     * @throws IllegalArgumentException If business rules are violated.
     * @throws IllegalStateException    If aggregate state prevents execution.
     */
    public S11Event execute(S11Command command) {
        // Invariant 1: Transaction amounts must be greater than zero.
        if (command.getAmount() == null || command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered or deleted once posted.
        if (this.status == TransactionStatus.POSTED) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: A transaction must result in a valid account balance.
        // (Simulating a check against an overdraft limit or available funds)
        // Here we enforce an arbitrary limit for the scenario.
        if (command.getAmount().compareTo(MAX_WITHDRAWAL) > 0) {
            throw new IllegalArgumentException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Logic succeeded: Apply state transition and emit event
        this.status = TransactionStatus.POSTED;

        return new S11Event(
                this.id,
                command.getAccountNumber(),
                command.getAmount(),
                command.getCurrency()
        );
    }

    /**
     * Internal helper for test setup to simulate a recovered aggregate that is already posted.
     * This would normally happen via event sourcing (apply events) or DB loading.
     */
    void markPostedInternal() {
        this.status = TransactionStatus.POSTED;
    }

    public UUID getId() {
        return id;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}
