package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class Transaction {

    private TransactionId id;
    private AccountNumber accountNumber;
    private BigDecimal currentBalance;
    private String currencyCode;
    private boolean isPosted;

    // Public no-arg constructor for infrastructure/proxies
    public Transaction() {
    }

    /**
     * Hydrates the aggregate (simulating reconstruction from events).
     * Used in tests to set up state.
     */
    public void hydrate(TransactionId id, AccountNumber accountNumber, BigDecimal currentBalance, String currencyCode) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.currencyCode = currencyCode;
        this.isPosted = false;
    }

    /**
     * Marks this transaction as already posted.
     * Used to test invariants about immutability.
     */
    public void markPosted() {
        this.isPosted = true;
    }

    /**
     * Executes a command against this aggregate.
     * Pattern: Execute(cmd) -> Event or throws Exception
     */
    public S11Event execute(PostWithdrawalCmd cmd) throws DomainException {
        // Invariant 1: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // Invariant 2: Transactions cannot be altered once posted
        if (this.isPosted) {
            throw new DomainException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 3: Valid account balance (enforced via aggregate validation)
        // Assuming this aggregate maintains the balance.
        BigDecimal projectedBalance = this.currentBalance.subtract(cmd.amount());
        if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Success: Apply state change and emit event
        this.currentBalance = projectedBalance;
        this.isPosted = true;

        return new WithdrawalPostedEvent(
            this.id != null ? this.id : new TransactionId("generated-tx"),
            cmd.accountNumber(),
            cmd.amount(),
            cmd.currencyCode(),
            this.currentBalance
        );
    }

    public TransactionId getId() {
        return id;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
}
