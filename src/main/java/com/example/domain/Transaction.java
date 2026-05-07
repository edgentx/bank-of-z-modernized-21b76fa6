package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private BigDecimal currentBalance;
    private boolean isPosted = false;

    // Invariant: Maximum balance allowed
    private static final BigDecimal MAX_BALANCE = new BigDecimal("1000.00");

    public Transaction(UUID id) {
        this.id = id;
    }

    // Method to set balance (for testing purposes)
    public void setCurrentBalance(BigDecimal balance) {
        this.currentBalance = balance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public boolean isPosted() {
        return isPosted;
    }

    public void markAsPosted() {
        this.isPosted = true;
    }

    /**
     * Execute pattern implementation.
     */
    public DepositPostedEvent execute(PostDepositCommand cmd) {
        // Invariant Check 1: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionError("Transaction amounts must be greater than zero.");
        }

        // Invariant Check 2: Transactions cannot be altered once posted
        if (this.isPosted) {
            throw new TransactionError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Calculate prospective balance
        BigDecimal prospectiveBalance = this.currentBalance.add(cmd.getAmount());

        // Invariant Check 3: Must result in valid account balance
        if (prospectiveBalance.compareTo(MAX_BALANCE) > 0) {
            throw new TransactionError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply State Change
        this.currentBalance = prospectiveBalance;
        this.isPosted = true; // Assuming a transaction is immutable/final once posted in this context

        // Emit Event
        return new DepositPostedEvent(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency(), this.currentBalance);
    }
}