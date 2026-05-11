package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for Account Aggregate (S-5).
 * These tests cover the acceptance criteria for OpenAccountCmd.
 */
class AccountAggregateTest {

    // Scenario: Successfully execute OpenAccountCmd
    @Test
    void shouldEmitAccountOpenedEventWhenValidCommandProvided() {
        // Given
        String id = "acc-123";
        AccountAggregate aggregate = new AccountAggregate(id);
        OpenAccountCmd cmd = new OpenAccountCmd(
            id,
            "cust-456",
            "CHECKING",
            new BigDecimal("500.00"),
            "10-20-30",
            null // Let it generate or be null if not strictly required by command logic
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof AccountOpenedEvent);

        AccountOpenedEvent event = (AccountOpenedEvent) events.get(0);
        assertEquals("account.opened", event.type());
        assertEquals(id, event.aggregateId());
        assertEquals("cust-456", event.customerId());
        assertEquals("CHECKING", event.accountType());
        assertEquals(0, new BigDecimal("500.00").compareTo(event.initialBalance()));
        assertEquals("10-20-30", event.sortCode());
        assertNotNull(event.occurredAt());
    }

    // Scenario: OpenAccountCmd rejected — Account balance cannot drop below the minimum required balance for its specific account type.
    @Test
    void shouldRejectCommandWhenInitialDepositIsBelowMinimumForAccountType() {
        // Given
        // Assuming "SAVINGS" requires a minimum balance (e.g., 100.00)
        AccountAggregate aggregate = new AccountAggregate("acc-savings-low");
        OpenAccountCmd cmd = new OpenAccountCmd(
            "acc-savings-low",
            "cust-789",
            "SAVINGS",
            new BigDecimal("50.00"), // Too low for Savings
            "10-20-30",
            null
        );

        // When & Then
        // The aggregate should throw an exception or reject the command.
        // Based on shared patterns, we expect an IllegalArgumentException.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("below minimum required balance"));
    }

    @Test
    void shouldAcceptCommandWhenInitialDepositMeetsMinimumForAccountType() {
        // Given
        AccountAggregate aggregate = new AccountAggregate("acc-savings-ok");
        OpenAccountCmd cmd = new OpenAccountCmd(
            "acc-savings-ok",
            "cust-789",
            "SAVINGS",
            new BigDecimal("100.00"), // Exact minimum
            "10-20-30",
            null
        );

        // When
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty());
        assertEquals(AccountAggregate.AccountStatus.ACTIVE, aggregate.getStatus());
    }

    // Scenario: OpenAccountCmd rejected — An account must be in an Active status to process withdrawals or transfers.
    // Note: This is an invariant on the AGGREGATE state. For OpenAccountCmd, this translates to:
    // "Cannot Open an account that is already Active/Closed/Frozen".
    // The OpenAccountCmd creates the account. If the aggregate already has state, it's a duplicate command.
    @Test
    void shouldRejectCommandIfAccountIsAlreadyActive() {
        // Given
        String id = "acc-duplicate";
        AccountAggregate aggregate = new AccountAggregate(id);
        OpenAccountCmd cmd1 = new OpenAccountCmd(id, "cust-1", "CHECKING", BigDecimal.ZERO, "10-20-30", null);
        
        // Execute first time to set state to ACTIVE
        aggregate.execute(cmd1);
        assertEquals(AccountAggregate.AccountStatus.ACTIVE, aggregate.getStatus());

        // When & Then - Try to open again
        OpenAccountCmd cmd2 = new OpenAccountCmd(id, "cust-1", "CHECKING", BigDecimal.TEN, "10-20-30", null);
        
        // The aggregate enforces that we can't 'open' an already active account.
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd2);
        });
        
        assertTrue(thrown.getMessage().contains("already initialized"));
    }

    // Scenario: OpenAccountCmd rejected — Account numbers must be uniquely generated and immutable.
    // This is a tricky invariant for an Aggregate to enforce itself without external dependencies.
    // However, we interpret this as: If a specific AccountNumber is provided in the command,
    // and the aggregate logic detects a collision (simulated here) or format violation, it rejects.
    // Or, standard TDD: Verify the event contains the generated number.
    @Test
    void shouldIncludeImmutableAccountNumberInEvent() {
        // Given
        AccountAggregate aggregate = new AccountAggregate("acc-num-gen");
        String expectedNum = "99999999";
        OpenAccountCmd cmd = new OpenAccountCmd(
            "acc-num-gen",
            "cust-gen",
            "CHECKING",
            BigDecimal.ZERO,
            "10-20-30",
            expectedNum // Pre-assigned number
        );

        // When
        var events = aggregate.execute(cmd);

        // Then
        AccountOpenedEvent event = (AccountOpenedEvent) events.get(0);
        assertEquals(expectedNum, event.accountNumber());
        // Once set, it is immutable because Event Sourcing events are immutable.
    }
}
