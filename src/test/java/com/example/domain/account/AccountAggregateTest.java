package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountAggregateTest {

    @Test
    void testOpenAccountSuccess() {
        String id = UUID.randomUUID().toString();
        AccountAggregate aggregate = new AccountAggregate(id);
        
        OpenAccountCmd cmd = new OpenAccountCmd(id, "cust-1", "SAVINGS", new BigDecimal("100.00"), "10-20-30");
        
        List<DomainEvent> events = aggregate.execute(cmd);
        
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent event = (AccountOpenedEvent) events.get(0);
        assertEquals("account.opened", event.type());
        assertEquals(id, event.aggregateId());
        assertEquals("cust-1", event.customerId());
        
        assertEquals(AccountAggregate.AccountStatus.ACTIVE, aggregate.getStatus());
        assertEquals("cust-1", aggregate.getCustomerId());
        assertEquals(new BigDecimal("100.00"), aggregate.getBalance());
    }

    @Test
    void testOpenAccountInsufficientFunds() {
        String id = UUID.randomUUID().toString();
        AccountAggregate aggregate = new AccountAggregate(id);
        
        // STUDENT account requires 100 min
        OpenAccountCmd cmd = new OpenAccountCmd(id, "cust-1", "STUDENT", new BigDecimal("50.00"), "10-20-30");
        
        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void testOpenAccountIdMismatch() {
        String aggId = UUID.randomUUID().toString();
        AccountAggregate aggregate = new AccountAggregate(aggId);
        
        // Command ID differs from Aggregate ID
        OpenAccountCmd cmd = new OpenAccountCmd(UUID.randomUUID().toString(), "cust-1", "SAVINGS", new BigDecimal("100"), "10-20-30");
        
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
