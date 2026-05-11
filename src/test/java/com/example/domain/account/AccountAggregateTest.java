package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for Account Aggregate (Story S-5).
 * These tests validate the 'Execute' pattern and invariants.
 */
class AccountAggregateTest {

    @Test
    void testSuccessfullyExecuteOpenAccountCmd() {
        // Given
        String accountId = "acc-123";
        String customerId = "cust-456";
        String accountType = "CHECKING";
        BigDecimal initialDeposit = new BigDecimal("500.00");
        String sortCode = "10-20-30";
        
        AccountAggregate aggregate = new AccountAggregate(accountId);
        OpenAccountCmd cmd = new OpenAccountCmd(accountId, customerId, accountType, initialDeposit, sortCode, null);

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "An event should be emitted");
        assertEquals("account.opened", events.get(0).type());
        assertEquals(accountId, events.get(0).aggregateId());

        // Verify Aggregate State
        assertEquals(customerId, aggregate.getCustomerId());
        assertEquals(accountType, aggregate.getAccountType());
        assertEquals(initialDeposit, aggregate.getBalance());
        assertEquals(sortCode, aggregate.getSortCode());
        assertNotNull(aggregate.getAccountNumber(), "Account number should be generated");
        assertEquals(AccountAggregate.AccountStatus.ACTIVE, aggregate.getStatus());
    }

    @Test
    void testOpenAccountCmd_Rejected_MinimumBalance() {
        // Given: A requirement that Savings accounts need $100 min balance
        String accountId = "acc-124";
        String customerId = "cust-456";
        String accountType = "SAVINGS";
        BigDecimal initialDeposit = new BigDecimal("50.00"); // Violates min balance
        String sortCode = "10-20-30";

        AccountAggregate aggregate = new AccountAggregate(accountId);
        OpenAccountCmd cmd = new OpenAccountCmd(accountId, customerId, accountType, initialDeposit, sortCode, null);

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Minimum balance"));
    }

    @Test
    void testOpenAccountCmd_Rejected_NegativeInitialDeposit() {
        // Given
        AccountAggregate aggregate = new AccountAggregate("acc-125");
        OpenAccountCmd cmd = new OpenAccountCmd("acc-125", "cust-456", "CHECKING", new BigDecimal("-10.00"), "10-20-30", null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    void testOpenAccountCmd_Rejected_ImmutabilityOfId() {
        // The command ID matches the aggregate ID. If they don't match, the aggregate constructor logic handles it.
        // Here we verify null ID rejection.
        AccountAggregate aggregate = new AccountAggregate("valid-id");
        OpenAccountCmd cmd = new OpenAccountCmd(null, "cust-456", "CHECKING", BigDecimal.ZERO, "10-20-30", null);

        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
    }
}
