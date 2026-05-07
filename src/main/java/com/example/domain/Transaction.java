package com.example.domain;

import java.math.BigDecimal;

public class Transaction {
    
    private final Account account;
    private WithdrawalPostedEvent postedEvent; // State to check immutability

    public Transaction(Account account) {
        this.account = account;
    }

    public WithdrawalPostedEvent getPostedEvent() {
        return postedEvent;
    }

    public DomainEvent execute(PostWithdrawalCmd cmd) {
        // Invariant 1: Already Posted
        if (this.postedEvent != null) {
            throw new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // Invariant 2: Amount > 0
        if (cmd.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainError("Transaction amounts must be greater than zero.");
        }

        // Invariant 3: Valid Balance (No Overdraft)
        // Assuming a balance cannot go below 0
        if (account.getBalance().compareTo(cmd.amount()) < 0) {
            throw new DomainError("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply Logic
        account.debit(cmd.amount());
        
        this.postedEvent = new WithdrawalPostedEvent(
            cmd.accountNumber(), 
            cmd.amount(), 
            cmd.currency(), 
            account.getBalance()
        );

        return this.postedEvent;
    }
}