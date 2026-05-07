package com.example.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Transaction Aggregate Root.
 * Handles the logic for posting withdrawals (S-11).
 */
public class Transaction {

    private final UUID id;
    private final String accountNumber;
    private final BigDecimal currentBalance;
    private final String currency;
    private final TransactionStatus status;

    // Constructor for creating/reconstituting the aggregate
    public Transaction(UUID id, String accountNumber, BigDecimal currentBalance, String currency, TransactionStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.currency = currency;
        this.status = status;
    }

    /**
     * Executes the PostWithdrawalCmd command.
     * Returns S11Event on success, throws DomainException on invariant violation.
     */
    public S11Event execute(S11Command cmd) {
        // Invariant: Transactions cannot be altered once posted.
        if (this.status == TransactionStatus.POSTED) {
            throw new DomainException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant: Transaction amounts must be greater than zero.
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transaction must result in a valid account balance (Aggregate Validation).
        // Assuming this simple aggregate holds the current balance state for the operation context
        // (or validates against the account state passed to it). For this feature, we validate
        // that the withdrawal does not overdraft the available balance represented in the aggregate.
        if (cmd.getAmount().compareTo(this.currentBalance) > 0) {
            throw new DomainException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // If invariants hold, emit the event.
        return new S11Event(
                UUID.randomUUID().toString(),
                cmd.getAccountNumber(),
                cmd.getAmount(),
                cmd.getCurrency(),
                TransactionStatus.POSTED
        );
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
}
