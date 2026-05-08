package com.example.domain.account;

import com.example.domain.account.model.*;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountAggregateTest {

    @Test
    public void testOpenAccountSuccess() {
        AccountAggregate aggregate = new AccountAggregate("test-acc-1");
        OpenAccountCmd cmd = new OpenAccountCmd(
            "test-acc-1",
            "customer-1",
            AccountAggregate.AccountType.CHECKING,
            new BigDecimal("100.00"),
            "123456"
        );

        List<DomainEvent> events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent event = (AccountOpenedEvent) events.get(0);
        assertEquals("test-acc-1", event.aggregateId());
        assertEquals("customer-1", event.customerId());
        assertEquals(AccountAggregate.AccountType.CHECKING, event.accountType());
        assertEquals(new BigDecimal("100.00"), event.initialDeposit());
        
        assertEquals(AccountAggregate.Status.ACTIVE, aggregate.getStatus());
    }

    @Test
    public void testOpenAccountFailsIfBelowMinimumBalance() {
        AccountAggregate aggregate = new AccountAggregate("test-acc-2");
        // Savings requires 100 min
        OpenAccountCmd cmd = new OpenAccountCmd(
            "test-acc-2",
            "customer-1",
            AccountAggregate.AccountType.SAVINGS,
            new BigDecimal("50.00"),
            "123456"
        );

        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }

    @Test
    public void testOpenAccountFailsIfAlreadyOpened() {
        AccountAggregate aggregate = new AccountAggregate("test-acc-3");
        OpenAccountCmd cmd1 = new OpenAccountCmd(
            "test-acc-3",
            "customer-1",
            AccountAggregate.AccountType.CHECKING,
            BigDecimal.ZERO,
            "123456"
        );
        aggregate.execute(cmd1);

        OpenAccountCmd cmd2 = new OpenAccountCmd(
            "test-acc-3",
            "customer-1",
            AccountAggregate.AccountType.CHECKING,
            BigDecimal.ZERO,
            "123456"
        );
        
        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd2));
    }
}
