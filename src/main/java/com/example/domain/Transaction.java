package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class Transaction {

    private String id;
    private BigDecimal currentBalance;
    private boolean isPosted;

    public Transaction() {
        this.currentBalance = BigDecimal.ZERO;
        this.isPosted = false;
    }

    public S11Event execute(S11Command cmd) {
        // Invariant: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Transaction amounts must be greater than zero
        if (cmd.getAmount() == null || cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant: A transaction must result in a valid account balance
        // Assuming this aggregate tracks the balance or validates against provided context.
        // For this scenario, we validate against internal state.
        if (currentBalance.subtract(cmd.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Logic for successful debit
        // In a real system, this might persist state or transition the aggregate.
        // Here we update the balance locally for the validation context of subsequent calls, though the scenario implies single shot.
        this.currentBalance = this.currentBalance.subtract(cmd.getAmount());
        
        return new S11Event("withdrawal.posted", cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
    }

    // Helpers for testing
    public void markPosted() {
        this.isPosted = true;
    }

    public void setCurrentBalance(BigDecimal balance) {
        this.currentBalance = balance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
}
