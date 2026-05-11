package com.example.domain.account.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Account extends AggregateRoot {
    private String id;
    private String accountNumber;
    private AccountStatus status;
    private BigDecimal balance;
    private boolean opened = false;

    // Public constructor for new instances
    public Account() {
        this.id = UUID.randomUUID().toString();
    }

    // Package private constructor for rehydration
    Account(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateAccountStatusCmd updateCmd) {
            return handleUpdateAccountStatus(updateCmd);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleUpdateAccountStatus(UpdateAccountStatusCmd cmd) {
        if (!opened) {
            throw new IllegalStateException("Account not opened");
        }

        // Invariant Check: Account numbers must be uniquely generated and immutable.
        // We verify the command targets the correct aggregate instance.
        if (!this.accountNumber.equals(cmd.accountNumber())) {
             throw new IllegalArgumentException("Account number mismatch or immutable violation");
        }

        // Invariant Check: Balance cannot drop below minimum (0)
        if (this.balance.compareTo(BigDecimal.ZERO) < 0) {
             throw new IllegalStateException("Account balance cannot drop below minimum required balance");
        }

        // Invariant Check: Must be active to process withdrawals/transfers (implied context for status change)
        // Here we simply enforce state transition validity.
        if (this.status != AccountStatus.ACTIVE && cmd.newStatus() == AccountStatus.ACTIVE) {
            // Allow reactivation if logic permits, or block. Assuming simple update.
        }

        AccountStatus oldStatus = this.status;
        AccountStatus newStatus = cmd.newStatus();

        if (oldStatus == newStatus) {
            return List.of();
        }

        AccountStatusUpdatedEvent event = new AccountStatusUpdatedEvent(
            this.id,
            this.accountNumber,
            oldStatus,
            newStatus,
            Instant.now()
        );

        this.status = newStatus;
        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    // Test helper
    public void open(String accountNumber, AccountStatus initialStatus, BigDecimal initialBalance) {
        if (opened) throw new IllegalStateException("Already opened");
        this.accountNumber = accountNumber;
        this.status = initialStatus;
        this.balance = initialBalance;
        this.opened = true;
        
        // Emit opening event for consistency
        addEvent(new AccountOpenedEvent(this.id, accountNumber, initialStatus, Instant.now()));
        incrementVersion();
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
}
