package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class Transaction {
    private BigDecimal currentBalance;
    private boolean isPosted = false;

    // Default constructor
    public Transaction() {
    }

    public void setCurrentBalance(BigDecimal balance) {
        this.currentBalance = balance;
    }

    public void markAsPosted() {
        this.isPosted = true;
    }

    public Object execute(PostWithdrawalCmd cmd) {
        // Invariant 1: Transaction amounts must be greater than zero.
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered once posted.
        if (this.isPosted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: Transaction must result in a valid account balance (non-negative in this context).
        BigDecimal newBalance = this.currentBalance.subtract(cmd.getAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state change (in-memory)
        this.currentBalance = newBalance;
        this.isPosted = true; // Withdrawal is immediately posted

        // Emit Event
        return new WithdrawalPosted(
            cmd.getAccountNumber(),
            cmd.getAmount(),
            cmd.getCurrency(),
            UUID.randomUUID().toString()
        );
    }
}
