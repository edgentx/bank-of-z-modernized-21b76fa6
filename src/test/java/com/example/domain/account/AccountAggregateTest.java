package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountAggregateTest {

    @Test
    void testCloseAccountSuccess() {
        AccountAggregate account = new AccountAggregate("ACC-123", BigDecimal.ZERO);
        CloseAccountCmd cmd = new CloseAccountCmd("ACC-123");

        List<DomainEvent> events = account.execute(cmd);

        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof AccountClosedEvent);
        assertEquals(AccountAggregate.Status.CLOSED, account.getStatus());
        assertEquals("ACC-123", ((AccountClosedEvent) events.get(0)).aggregateId());
    }

    @Test
    void testCloseAccountFailsIfBalanceNotZero() {
        AccountAggregate account = new AccountAggregate("ACC-123", new BigDecimal("100.00"));
        CloseAccountCmd cmd = new CloseAccountCmd("ACC-123");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> account.execute(cmd));
        assertTrue(ex.getMessage().contains("Account balance cannot drop below"));
    }

    @Test
    void testCloseAccountFailsIfNotActive() {
        AccountAggregate account = new AccountAggregate("ACC-123", BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.CLOSED); // Manually set for testing
        CloseAccountCmd cmd = new CloseAccountCmd("ACC-123");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> account.execute(cmd));
        assertTrue(ex.getMessage().contains("An account must be in an Active status"));
    }

    @Test
    void testCloseAccountFailsIfAccountNumberMismatch() {
        AccountAggregate account = new AccountAggregate("ACC-123", BigDecimal.ZERO);
        CloseAccountCmd cmd = new CloseAccountCmd("DIFFERENT-ID");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> account.execute(cmd));
        assertTrue(ex.getMessage().contains("Account numbers must be uniquely generated"));
    }
}
