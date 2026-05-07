package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {
    private String id;
    private boolean isPosted;
    private BigDecimal accountBalance;

    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.isPosted = false;
        this.accountBalance = BigDecimal.ZERO;
    }

    public void markAsPosted() {
        this.isPosted = true;
    }

    public void setAccountBalance(BigDecimal balance) {
        this.accountBalance = balance;
    }

    public Object execute(PostDepositCmd cmd) {
        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new DomainError("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            return new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: A transaction must result in a valid account balance
        // For example: Balance + Amount >= 0 (No overdraft allowed)
        BigDecimal projectedBalance = this.accountBalance.add(cmd.getAmount());
        if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
            return new DomainError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state change (in-memory)
        this.accountBalance = projectedBalance;
        this.isPosted = true;

        // Emit event
        return new DepositPostedEvent(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
    }

    public String getId() {
        return id;
    }

    public boolean isPosted() {
        return isPosted;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }
}
