package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class Transaction {

    private BigDecimal availableBalance = BigDecimal.ZERO;
    private boolean isPosted = false;

    // Default constructor
    public Transaction() {
    }

    // Allow setting balance for testing the overdraft invariant
    public void setAvailableBalance(BigDecimal balance) {
        this.availableBalance = balance;
    }

    // Mark as posted for testing immutability invariant
    public void markAsPosted() {
        this.isPosted = true;
    }

    public WithdrawalPostedEvent execute(PostWithdrawalCmd cmd) {
        // Invariant 1: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            throw new DomainViolationException("Transactions cannot be altered or deleted once posted");
        }

        // Invariant 2: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainViolationException("Transaction amounts must be greater than zero");
        }

        // Invariant 3: A transaction must result in a valid account balance
        // Note: In a real scenario, this would likely fetch the current balance from an Account aggregate or repository.
        // Here we check against the aggregate's held state for simplicity of the unit test.
        BigDecimal projectedBalance = availableBalance.subtract(cmd.amount());
        if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainViolationException("A transaction must result in a valid account balance (Insufficient funds)");
        }

        // Apply state changes
        this.availableBalance = projectedBalance;
        this.isPosted = true;

        // Emit Event
        return new WithdrawalPostedEvent(cmd.accountNumber(), cmd.amount(), cmd.currency());
    }
}
