package com.vforce360.transaction.domain.model;

import com.vforce360.transaction.domain.command.PostDepositCmd;
import com.vforce360.transaction.domain.event.DepositPostedEvent;
import com.vforce360.transaction.domain.shared.AggregateRoot;
import com.vforce360.transaction.domain.shared.DomainError;
import com.vforce360.transaction.domain.shared.Result;
import com.vforce360.transaction.ports.AccountStatePort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction extends AggregateRoot {

    private final UUID transactionId;
    private final String accountNumber;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime postedAt;
    private boolean isPosted;

    // Constructor for new transactions
    public Transaction(UUID transactionId, String accountNumber) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.isPosted = false;
    }

    // Execute method as per pattern
    public Result<Void, DomainError> execute(PostDepositCmd cmd, AccountStatePort accountStatePort) {
        // Scenario: Transaction amounts must be greater than zero
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error(new DomainError("Transaction amounts must be greater than zero."));
        }

        // Scenario: Transactions cannot be altered or deleted once posted
        if (this.isPosted) {
            return Result.error(new DomainError("Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction."));
        }

        // Scenario: A transaction must result in a valid account balance
        // We check the invariant using the port to simulate external balance check
        BigDecimal currentBalance = accountStatePort.getBalance(this.accountNumber);
        BigDecimal projectedBalance = currentBalance.add(cmd.getAmount());
        
        // Simulating a business rule (e.g., max balance cap or negative balance prevention)
        if (projectedBalance.compareTo(new BigDecimal("1000000")) > 0) {
             return Result.error(new DomainError("A transaction must result in a valid account balance (enforced via aggregate validation)."));
        }

        // Apply state changes
        this.amount = cmd.getAmount();
        this.currency = cmd.getCurrency();
        this.postedAt = LocalDateTime.now();
        this.isPosted = true;

        // Emit event
        DepositPostedEvent event = new DepositPostedEvent(
            this.transactionId, 
            this.accountNumber, 
            this.amount, 
            this.currency,
            this.postedAt
        );
        
        // In a real scenario, this event would be stored in the uncommitted events list of AggregateRoot
        // For this snippet, we acknowledge emission.
        // this.raiseEvent(event);

        return Result.success(null);
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean isPosted() {
        return isPosted;
    }
}
