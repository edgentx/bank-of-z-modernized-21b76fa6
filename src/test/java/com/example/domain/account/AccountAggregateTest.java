package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AccountAggregate.
 */
class AccountAggregateTest {

    @Test
    void testCloseAccountSuccess() {
        // Setup
        String accountNumber = "ACC-001";
        AccountAggregate aggregate = new AccountAggregate(accountNumber);
        aggregate.setBalance(BigDecimal.ZERO);

        // Execute
        CloseAccountCmd cmd = new CloseAccountCmd(accountNumber);
        List events = aggregate.execute(cmd);

        // Verify
        assertFalse(events.isEmpty(), "Should emit an event");
        assertTrue(events.get(0) instanceof AccountClosedEvent, "Event should be AccountClosedEvent");
        assertEquals(AccountAggregate.AccountStatus.CLOSED, aggregate.getStatus());
    }

    @Test
    void testCloseAccountFailsIfBalanceIsNotZero() {
        // Setup
        String accountNumber = "ACC-002";
        AccountAggregate aggregate = new AccountAggregate(accountNumber);
        aggregate.setBalance(new BigDecimal("100.00"));

        // Execute
        CloseAccountCmd cmd = new CloseAccountCmd(accountNumber);

        // Verify
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("balance must be zero"));
    }

    @Test
    void testCloseAccountFailsIfNotActive() {
        // Setup
        String accountNumber = "ACC-003";
        AccountAggregate aggregate = new AccountAggregate(accountNumber);
        aggregate.setStatus(AccountAggregate.AccountStatus.CLOSED); // Already closed

        // Execute
        CloseAccountCmd cmd = new CloseAccountCmd(accountNumber);

        // Verify
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("not active"));
    }
}
