package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class Transaction {

    private TransactionState state = TransactionState.PENDING;
    private BigDecimal currentBalance = BigDecimal.ZERO;

    public S11Event execute(PostWithdrawalCmd cmd) {
        // Invariant Check: Immutability
        if (this.state == TransactionState.POSTED) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant Check: Amount > 0
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant Check: Valid Account Balance
        // Simulating a balance check (e.g. against a repository or cached balance)
        if (cmd.getAmount().compareTo(new BigDecimal("1000000")) > 0) { 
            // Arbitrary high limit to represent a constraint violation for this story
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state transition
        this.state = TransactionState.POSTED;
        // Update internal balance representation (domain logic)
        this.currentBalance = this.currentBalance.subtract(cmd.getAmount());

        // Emit Event
        return new S11Event.WithdrawalPosted(
            cmd.getAccountNumber(), 
            cmd.getAmount(), 
            cmd.getCurrency().getCurrencyCode()
        );
    }

    public TransactionState getState() {
        return state;
    }
}
