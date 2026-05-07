package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class Transaction {

    private final UUID transactionId;
    private final UUID accountId;
    private BigDecimal balance;
    private boolean isPosted;

    public Transaction(UUID transactionId, UUID accountId, BigDecimal balance, boolean isPosted) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.balance = balance;
        this.isPosted = isPosted;
    }

    public Transaction(TransactionSnapshot snapshot) {
        this(UUID.randomUUID(), snapshot.accountId(), snapshot.balance(), snapshot.isPosted());
    }

    public WithdrawalPostedEvent execute(PostWithdrawalCmd cmd) {
        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero");
        }

        // Invariant: A transaction must result in a valid account balance
        // Assuming "valid" means non-negative for this context (overdraft protection)
        if (this.balance.compareTo(cmd.amount()) < 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance");
        }

        // Apply event logic (synchronously for the aggregate)
        this.balance = this.balance.subtract(cmd.amount());
        this.isPosted = true;

        return new WithdrawalPostedEvent(
            this.transactionId,
            cmd.accountId(),
            cmd.amount(),
            cmd.currency()
        );
    }

    // Getters for testing/validation
    public UUID getAccountId() { return accountId; }
    public BigDecimal getBalance() { return balance; }
    public boolean isPosted() { return isPosted; }
}
