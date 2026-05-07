package com.vforce360.transaction.domain.model;

import com.vforce360.transaction.application.command.PostWithdrawalCmd;
import com.vforce360.transaction.domain.event.WithdrawalPostedEvent;
import com.vforce360.transaction.ports.AccountPort;
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

    // Default constructor for frameworks (e.g. JPA/Hibernate) if needed, 
    // though we are focusing on the domain logic here.
    public Transaction() {}

    // Constructor for creating a new Transaction (e.g. a pending entry)
    public Transaction(String accountNumber, BigDecimal amount, Currency currency) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.status = TransactionStatus.PENDING;
    }

    /**
     * Executes the PostWithdrawalCmd command.
     * Enforces invariants and applies resulting events.
     * 
     * TDD Green Phase Implementation.
     */
    public Object execute(PostWithdrawalCmd cmd, AccountPort accountPort) {
        // 1. Invariant: Transaction amounts must be greater than zero.
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amounts must be greater than zero.");
        }

        // 2. Invariant: Transactions cannot be altered or deleted once posted.
        if (this.status == TransactionStatus.POSTED) {
            throw new IllegalStateException("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.");
        }

        // 3. Invariant: A transaction must result in a valid account balance.
        // We rely on the AccountPort to fetch the current state to perform validation.
        BigDecimal currentBalance = accountPort.getBalance(this.accountNumber);
        BigDecimal resultingBalance = currentBalance.subtract(cmd.getAmount());

        if (resultingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("A transaction must result in a valid account balance (enforced via aggregate validation).");
        }

        // Apply logic: Update internal state and generate event
        // In this specific context, the command represents the act of posting.
        // So we transition the state to POSTED.
        this.status = TransactionStatus.POSTED;

        WithdrawalPostedEvent event = new WithdrawalPostedEvent(
            cmd.getAccountNumber(), 
            cmd.getAmount(), 
            cmd.getCurrency()
        );
        
        this.events.add(event);
        return event;
    }

    // Helper for the 'Already Posted' test setup
    public void markPosted() {
        this.status = TransactionStatus.POSTED;
    }

    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public TransactionStatus getStatus() { return status; }
    public List<Object> getEvents() { return events; }
}
