package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Transaction Aggregate Root.
 * Handles logic for posting withdrawals.
 */
public class Transaction {

    private final UUID transactionId;
    private BigDecimal currentBalance;
    private String currency;
    private boolean isPosted = false;

    // Constructor for creating a new aggregate
    public Transaction(UUID transactionId) {
        if (transactionId == null) throw new IllegalArgumentException("Transaction ID cannot be null");
        this.transactionId = transactionId;
    }

    /**
     * Testing hook to simulate an existing state for the aggregate.
     * In a real application, this state would be rebuilt from EventSourcing
     * or loaded from a database.
     */
    public void loadStateForTest(BigDecimal balance, String currency) {
        this.currentBalance = balance;
        this.currency = currency;
    }

    /**
     * Testing hook to simulate an immutable posted state.
     */
    public void markAsPosted() {
        this.isPosted = true;
    }

    /**
     * Execute method implementing the Command pattern.
     * Dispatches the specific command logic and returns an Event.
     */
    public S11Event execute(S11Command command) {
        if (command instanceof S11Command.PostWithdrawalCmd cmd) {
            return handlePostWithdrawal(cmd);
        }
        throw new UnsupportedOperationException("Unknown command type: " + command.getClass().getSimpleName());
    }

    private S11Event handlePostWithdrawal(S11Command.PostWithdrawalCmd cmd) {
        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            throw new IllegalStateException("Cannot post withdrawal: Transaction is already posted and immutable.");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        // Contextual Invariant: A transaction must result in a valid account balance
        // For this aggregate, we assume the 'currentBalance' represents the available funds.
        // (In a full system, we might fetch the account, but per the prompt, we rely on aggregate validation).
        if (cmd.amount().compareTo(this.currentBalance) > 0) {
            throw new IllegalStateException("Transaction rejected: Insufficient funds for valid account balance.");
        }

        // Apply state change (in-memory for the test, in reality we might persist or apply event)
        this.currentBalance = this.currentBalance.subtract(cmd.amount());
        this.isPosted = true; // Assuming this aggregate instance represents the single transaction being executed

        return new S11Event.WithdrawalPosted(
            UUID.randomUUID(),
            java.time.Instant.now(),
            this.transactionId,
            cmd.accountId(),
            cmd.amount(),
            cmd.currency()
        );
    }
}
