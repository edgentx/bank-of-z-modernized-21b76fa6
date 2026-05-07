package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final String accountNumber;
    private final BigDecimal currentBalance;
    private final TransactionStatus status;

    public Transaction(UUID id, String accountNumber, BigDecimal currentBalance, TransactionStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * Execute pattern entry point.
     */
    public S11Event execute(S11Command command) {
        // Dispatch to specific handler
        return handlePostWithdrawal(command);
    }

    private S11Event handlePostWithdrawal(S11Command cmd) {
        // Invariant 1: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered once posted
        if (this.status == TransactionStatus.POSTED) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: Transaction must result in a valid account balance (e.g. no overdraft)
        BigDecimal resultingBalance = this.currentBalance.subtract(cmd.getAmount());
        if (resultingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Success: Create event
        return new S11Event(
            this.id,
            cmd.getAccountNumber(),
            cmd.getAmount(),
            cmd.getCurrency(),
            resultingBalance
        );
    }
}
