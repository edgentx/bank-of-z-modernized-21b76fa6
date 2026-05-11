package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountAggregateTest {

    @Test
    void testExecuteUpdateAccountStatusCmdSuccess() {
        String accountNumber = "ACC-001";
        AccountAggregate aggregate = new AccountAggregate(accountNumber);
        aggregate.hydrate(AccountAggregate.AccountStatus.ACTIVE, BigDecimal.valueOf(500), AccountAggregate.AccountType.SAVINGS);

        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(accountNumber, AccountAggregate.AccountStatus.FROZEN);
        List<DomainEvent> events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) events.get(0);
        assertEquals(accountNumber, event.aggregateId());
        assertEquals(AccountAggregate.AccountStatus.FROZEN, event.newStatus());
    }

    @Test
    void testRejectIfBalanceTooLow() {
        String accountNumber = "ACC-002";
        AccountAggregate aggregate = new AccountAggregate(accountNumber);
        // Balance 50, Savings Min is 100
        aggregate.hydrate(AccountAggregate.AccountStatus.ACTIVE, BigDecimal.valueOf(50), AccountAggregate.AccountType.SAVINGS);

        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(accountNumber, AccountAggregate.AccountStatus.FROZEN);
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(exception.getMessage().contains("Account balance cannot drop below the minimum"));
    }

    @Test
    void testRejectIfNotActive() {
        String accountNumber = "ACC-003";
        AccountAggregate aggregate = new AccountAggregate(accountNumber);
        aggregate.hydrate(AccountAggregate.AccountStatus.FROZEN, BigDecimal.valueOf(500), AccountAggregate.AccountType.SAVINGS);

        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(accountNumber, AccountAggregate.AccountStatus.CLOSED);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("An account must be in an Active status"));
    }
}
