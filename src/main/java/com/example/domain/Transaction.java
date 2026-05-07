package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;

public class Transaction {

    private final TransactionId id;
    private TransactionStatus status;

    public Transaction(TransactionId id, TransactionStatus status) {
        this.id = id;
        this.status = status;
    }

    public DomainEvent execute(PostWithdrawalCmd cmd) {
        // Invariant: Transaction amounts must be greater than zero
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amounts must be greater than zero.");
        }

        // Invariant: Transactions cannot be altered once posted
        if (this.status == TransactionStatus.POSTED) {
            throw new DomainException("Transactions cannot be altered or deleted once posted.");
        }

        // Invariant: Result must result in valid account balance (Simulated check)
        if (cmd.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("A transaction must result in a valid account balance.");
        }

        // Logic to apply event
        this.status = TransactionStatus.POSTED; // State transition

        return new WithdrawalPostedEvent(
                java.util.UUID.randomUUID(),
                System.currentTimeMillis(),
                this.id.value().toString(),
                cmd.accountNumber(),
                cmd.amount(),
                cmd.currency()
        );
    }
}