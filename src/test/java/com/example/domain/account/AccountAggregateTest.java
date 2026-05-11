package com.example.domain.account;

import com.example.domain.account.model.*;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountAggregateTest {

    // Scenario: Successfully execute OpenAccountCmd
    @Test
    void shouldOpenAccountWhenValidCommandProvided() {
        // Given
        String accountId = "ACC-123";
        String customerId = "CUST-999";
        AccountType type = AccountType.SAVINGS;
        BigDecimal deposit = new BigDecimal("600.00"); // Savings min is 500
        String sortCode = "10-20-30";

        AccountAggregate aggregate = new AccountAggregate(accountId);
        OpenAccountCmd cmd = new OpenAccountCmd(accountId, customerId, type, deposit, sortCode);

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Should emit an event");
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof AccountOpenedEvent);

        AccountOpenedEvent event = (AccountOpenedEvent) events.get(0);
        assertEquals(accountId, event.aggregateId());
        assertEquals(customerId, event.customerId());
        assertEquals(type, event.accountType());
        assertEquals(deposit, event.initialBalance());

        // Verify Aggregate State
        assertEquals(AccountAggregate.AccountStatus.ACTIVE, aggregate.getStatus());
        assertEquals(deposit, aggregate.getBalance());
    }

    // Scenario: OpenAccountCmd rejected — Account balance cannot drop below the minimum required balance
    @Test
    void shouldRejectCommandIfInitialDepositIsBelowMinimum() {
        // Given
        String accountId = "ACC-FAIL-1";
        String customerId = "CUST-001";
        AccountType type = AccountType.SAVINGS; // Min is 500
        BigDecimal deposit = new BigDecimal("400.00"); // Below min

        AccountAggregate aggregate = new AccountAggregate(accountId);
        OpenAccountCmd cmd = new OpenAccountCmd(accountId, customerId, type, deposit, "10-10-10");

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("below minimum"));
        assertEquals(AccountAggregate.AccountStatus.PENDING_OPEN, aggregate.getStatus());
    }

    // Scenario: OpenAccountCmd rejected — An account must be in an Active status to process withdrawals or transfers.
    // Note: Since this is an OPEN command, the account is not yet ACTIVE.
    // However, the criteria says "must be in Active status to process...".
    // This usually applies to subsequent commands. For the OPEN command, this is a N/A or conceptual invariant.
    // But we can test that attempting to Open an ALREADY ACTIVE account fails.
    @Test
    void shouldRejectCommandIfAccountIsAlreadyActive() {
        // Given
        String accountId = "ACC-ACTIVE";
        AccountAggregate aggregate = new AccountAggregate(accountId);
        
        // Execute first open successfully
        OpenAccountCmd firstCmd = new OpenAccountCmd(
            accountId, "CUST-1", AccountType.CHECKING, 
            new BigDecimal("200.00"), "10-10-10"
        );
        aggregate.execute(firstCmd);
        
        // Attempt to open again
        OpenAccountCmd secondCmd = new OpenAccountCmd(
            accountId, "CUST-1", AccountType.CHECKING, 
            new BigDecimal("200.00"), "10-10-10"
        );

        // When & Then
        // We expect an exception because we can't reopen an active account
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(secondCmd);
        });
    }

    // Scenario: OpenAccountCmd rejected — Account numbers must be uniquely generated and immutable.
    @Test
    void shouldRejectCommandIfAccountIdMismatch() {
        // Given
        String aggregateId = "ACC-AAA";
        String cmdId = "ACC-BBB";
        
        AccountAggregate aggregate = new AccountAggregate(aggregateId);
        OpenAccountCmd cmd = new OpenAccountCmd(
            cmdId, "CUST-1", AccountType.CHECKING, 
            new BigDecimal("200.00"), "10-10-10"
        );

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("mismatch"));
    }
}