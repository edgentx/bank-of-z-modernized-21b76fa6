package com.example.domain.account.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class AccountAggregate extends AggregateRoot {

    private final String accountId;
    private String customerId;
    private AccountStatus status;
    private BigDecimal balance = BigDecimal.ZERO;

    public AccountAggregate(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String id() {
        return accountId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateAccountStatusCmd c) {
            return updateStatus(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> updateStatus(UpdateAccountStatusCmd cmd) {
        // Invariant: Command ID must match Aggregate ID
        if (!cmd.accountId().equals(this.accountId)) {
            throw new IllegalArgumentException("Command ID mismatch");
        }

        // Invariant: Immutability / Integrity check
        // Simulating check: Account numbers must be unique/immutable (implicitly enforced by ID check)

        // Invariant: Logic based on Acceptance Criteria
        // "Account must be in an Active status to process withdrawals or transfers" -> 
        // If we are trying to update FROM Active TO Frozen, we are essentially stopping processing.
        // The scenario implies that state changes are restricted if balance is low or status is incompatible.
        
        if (this.status != AccountStatus.ACTIVE && cmd.newStatus() == AccountStatus.ACTIVE) {
            // Reactivating a closed/frozen account might have specific rules, 
            // but the prompt implies rejection if violating invariants.
            // Let's assume we can only update if not currently CLOSED
            if (this.status == AccountStatus.CLOSED) {
                throw new IllegalStateException("Cannot modify status of a Closed account");
            }
        }

        // Scenario: "Account balance cannot drop below minimum..." for UpdateAccountStatusCmd.
        // Context: Usually, preventing status change if balance is wrong (e.g. can't Close if debt).
        // Assuming minimum balance is 0 for simplicity, unless specific type logic exists.
        // Let's assume we cannot close if balance < 0 (overdrawn).
        if (cmd.newStatus() == AccountStatus.CLOSED && this.balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Cannot close account with negative balance");
        }

        var event = new AccountStatusUpdatedEvent(this.accountId, cmd.newStatus(), Instant.now());
        this.status = cmd.newStatus();
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Internal state mutator for testing/reconstruction
    public void apply(AccountOpenedEvent event) {
        this.customerId = event.customerId();
        this.status = event.status();
        // In a real app, we might load balance from DB or initial event
        this.balance = BigDecimal.ZERO; 
    }

    public void setBalance(BigDecimal amount) {
        this.balance = amount;
    }

    public AccountStatus getStatus() {
        return status;
    }
}
