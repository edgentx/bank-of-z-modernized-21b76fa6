package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {
    private final UUID id;
    private final String accountNumber;
    private final BigDecimal currentBalance;
    private final String currency;
    private final TransactionStatus status;

    public Transaction(UUID id, String accountNumber, BigDecimal currentBalance, String currency, TransactionStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.currency = currency;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public WithdrawalPosted execute(PostWithdrawalCmd cmd) {
        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero");
        }

        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.status == TransactionStatus.POSTED) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction");
        }

        // Invariant: A transaction must result in a valid account balance
        BigDecimal projectedBalance = this.currentBalance.subtract(cmd.getAmount());
        if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation)");
        }

        // Apply state change and emit event
        return new WithdrawalPosted(cmd.getTransactionId(), cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
    }
}
