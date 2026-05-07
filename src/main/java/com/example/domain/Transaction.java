package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;
    private Status status;

    public enum Status {
        PENDING,
        POSTED
    }

    public Transaction(UUID id, String accountNumber, BigDecimal amount, String currency) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.status = Status.PENDING;
    }

    public S11Event execute(S11Command cmd) {
        // 1. Check invariants regarding state
        if (this.status == Status.POSTED) {
            return new S11Event.TransactionRejected(
                    cmd.transactionId(),
                    "Transactions cannot be altered or deleted once posted",
                    java.time.Instant.now()
            );
        }

        // 2. Check business rules regarding inputs
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return new S11Event.TransactionRejected(
                    cmd.transactionId(),
                    "Transaction amounts must be greater than zero",
                    java.time.Instant.now()
            );
        }

        // 3. Check aggregate validation (Balance)
        // Assuming cmd.currentBalance represents the balance fetched from the Account aggregate
        BigDecimal balanceAfter = cmd.currentBalance().subtract(cmd.amount());
        if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
            return new S11Event.TransactionRejected(
                    cmd.transactionId(),
                    "A transaction must result in a valid account balance (insufficient funds)",
                    java.time.Instant.now()
            );
        }

        // If all valid, apply state transition and return event
        // Note: In a real aggregate, we might mutate state here (this.status = POSTED)
        // For this test, we return the event.
        return new S11Event.WithdrawalPosted(
                cmd.transactionId(),
                cmd.accountNumber(),
                cmd.amount(),
                cmd.currency(),
                balanceAfter,
                java.time.Instant.now()
        );
    }

    public UUID getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Status getStatus() { return status; }
}
