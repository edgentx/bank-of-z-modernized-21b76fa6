package com.vforce360.transaction.domain.model;

import com.vforce360.transaction.application.command.PostWithdrawalCmd;
import com.vforce360.transaction.domain.event.WithdrawalPostedEvent;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.ArrayList;
import java.util.List;

// Domain Aggregate Root
public class Transaction {

    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;
    private TransactionStatus status;
    private List<Object> events = new ArrayList<>();

    // Constructor for creating a new Transaction (e.g. a pending entry)
    public Transaction(String accountNumber, BigDecimal amount, Currency currency) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.status = TransactionStatus.PENDING;
    }

    // TDD Red Phase: This method is a stub to allow compilation of tests.
    // It is designed to fail the specific acceptance criteria.
    public Object execute(PostWithdrawalCmd cmd) {
        // 1. Check Amount > 0
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // 2. Check Posted Invariant
        // In a real implementation, this would check this.status == POSTED
        // For TDD stub, we assume we haven't implemented the logic to fail yet, or we explicitly fail to pass the test.
        // However, the stub needs to allow the 'Happy Path' test to run until it hits the missing logic.
        // But wait, TDD Red means the tests MUST fail. 
        // The tests require specific behaviors. 
        // 
        // To satisfy the prompt "Fail when run against an empty implementation":
        // We provide the minimal structure.
        
        // Happy Path Stub:
        // Returns null to fail the "resultingEvent instanceof WithdrawalPostedEvent" check
        return null; 
    }

    // Helper for the 'Already Posted' test setup
    public void markPosted() {
        this.status = TransactionStatus.POSTED;
    }

    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public TransactionStatus getStatus() { return status; }
}
