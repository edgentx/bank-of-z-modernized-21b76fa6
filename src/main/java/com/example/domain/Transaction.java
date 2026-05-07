package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private BigDecimal currentBalance = BigDecimal.ZERO;
    private boolean isPosted = false;

    public Transaction(UUID id) {
        this.id = id;
    }

    public Object execute(PostWithdrawalCmd cmd) {
        // 1. Invariant: Amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // 2. Invariant: Cannot alter posted transactions
        if (this.isPosted) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // 3. Invariant: Valid account balance
        // Assuming simple debit logic for this aggregate context
        if (this.currentBalance.compareTo(cmd.getAmount()) < 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply state transition
        this.currentBalance = this.currentBalance.subtract(cmd.getAmount());
        this.isPosted = true;

        // Emit Event
        return new WithdrawalPostedEvent(this.id, cmd.getAccountNumber(), cmd.getAmount(), cmd.getCurrency());
    }

    public void markAsPosted() {
        this.isPosted = true;
    }

    public void setCurrentBalance(BigDecimal balance) {
        this.currentBalance = balance;
    }
}
