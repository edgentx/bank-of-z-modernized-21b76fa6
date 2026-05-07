package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private UUID id;
    private String accountNumber;
    private BigDecimal currentBalance;
    private boolean posted;

    public Transaction() {
        this.id = UUID.randomUUID();
        this.posted = false;
        this.currentBalance = BigDecimal.ZERO;
    }

    // Command Handler (Execute pattern)
    public WithdrawalPostedEvent execute(PostWithdrawalCmd cmd) {
        // Invariant 1: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered once posted
        if (this.posted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: Valid account balance (e.g., sufficient funds)
        // Assuming a simplified debit check where balance cannot go below zero
        BigDecimal projectedBalance = this.currentBalance.subtract(cmd.getAmount());
        if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance.");
        }

        // Apply state changes
        this.accountNumber = cmd.getAccountNumber();
        this.currentBalance = projectedBalance;
        this.markAsPosted(); // The act of posting makes it immutable

        // Emit Event
        return new WithdrawalPostedEvent(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
    }

    // Internal state modifier for the transition
    private void markAsPosted() {
        this.posted = true;
    }

    // --- Getters and Setters used primarily for testing/infrastructure ---

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public boolean isPosted() {
        return posted;
    }

    // Exposed for test setup
    public void markAsPostedForTest() {
        this.posted = true;
    }
}
