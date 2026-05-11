package com.example.domain.account.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * Account Aggregate
 * Handles account lifecycle and status updates.
 * S-6: Implement UpdateAccountStatusCmd
 */
public class Account extends AggregateRoot {

    private final String accountNumber;
    private AccountStatus status = AccountStatus.PENDING; // Default state

    public Account(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String id() {
        return accountNumber;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateAccountStatusCmd c) {
            return updateStatus(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> updateStatus(UpdateAccountStatusCmd cmd) {
        // Invariant: Account Number Immutable
        // The command's account ID must match the aggregate ID. 
        // Since the prompt implies `cmd.accountId()` failed previously, and this is a method on the aggregate, 
        // we assume the command carries the target ID. 
        if (!this.accountNumber.equals(cmd.accountNumber())) {
            throw new IllegalArgumentException("Account number mismatch or modification attempted");
        }

        // Invariant: Account balance and status restrictions (Placeholder for S-6 logic)
        // "Account balance cannot drop below the minimum required balance for its specific account type"
        // "An account must be in an Active status to process withdrawals or transfers"
        // For this feature, we primarily handle the state transition logic requested by the command.
        // The specific balance logic would likely involve a balance field not present in this stub 
        // but we can acknowledge the rule.
        
        AccountStatus oldStatus = this.status;
        AccountStatus newStatus = cmd.newStatus();

        // Example business rule: Prevent closing if active (just as a placeholder for invariants)
        // if (oldStatus == AccountStatus.ACTIVE && newStatus == AccountStatus.CLOSED) {
        //    throw new IllegalStateException("Cannot close active account directly");
        // }

        var event = new AccountStatusUpdatedEvent(
            this.accountNumber, 
            oldStatus, 
            newStatus, 
            Instant.now()
        );

        // Apply state change
        this.status = newStatus;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public AccountStatus getStatus() {
        return status;
    }
}
