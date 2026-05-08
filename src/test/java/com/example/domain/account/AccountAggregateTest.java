package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountAggregateTest {

    @Test
    void testOpenAccountSuccessfully() {
        String accountId = "acc-123";
        AccountAggregate aggregate = new AccountAggregate(accountId);

        OpenAccountCmd cmd = new OpenAccountCmd(
                "cust-456",
                "STANDARD",
                new BigDecimal("100.00"),
                "10-20-30",
                "123456789"
        );

        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof AccountOpenedEvent);

        AccountOpenedEvent openedEvent = (AccountOpenedEvent) events.get(0);
        assertEquals("account.opened", openedEvent.type());
        assertEquals(accountId, openedEvent.aggregateId());
        assertEquals(new BigDecimal("100.00"), openedEvent.balance());
        assertEquals("cust-456", openedEvent.customerId());
    }

    @Test
    void testOpenAccountRejectsLowBalance() {
        String accountId = "acc-124";
        AccountAggregate aggregate = new AccountAggregate(accountId);

        OpenAccountCmd cmd = new OpenAccountCmd(
                "cust-457",
                "PREMIUM", // Requires 1000
                new BigDecimal("50.00"),
                "10-20-30",
                "987654321"
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Account balance cannot drop below the minimum required balance"));
    }

    @Test
    void testOpenAccountRejectsAlreadyActive() {
        String accountId = "acc-125";
        AccountAggregate aggregate = new AccountAggregate(accountId);

        // Open once
        OpenAccountCmd cmd1 = new OpenAccountCmd("cust-1", "STANDARD", BigDecimal.TEN, "sort-1", "num-1");
        aggregate.execute(cmd1);

        // Try to open again
        OpenAccountCmd cmd2 = new OpenAccountCmd("cust-1", "STANDARD", BigDecimal.TEN, "sort-1", "num-1");
        
        // Technically this falls under the "not in NONE state" check, which maps to the rejection requirement.
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd2);
        });
        
        assertTrue(exception.getMessage().contains("not in a valid state to be opened"));
    }
}
