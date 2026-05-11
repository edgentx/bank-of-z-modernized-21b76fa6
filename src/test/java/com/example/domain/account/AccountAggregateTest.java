package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase tests for S-7: CloseAccountCmd.
 * These tests verify the business logic and invariants of the Account aggregate.
 */
class AccountAggregateTest {

    // Helper to create a valid, active account
    private AccountAggregate createActiveAccount(String id) {
        AccountAggregate account = new AccountAggregate(id);
        account.setStatus(AccountAggregate.Status.ACTIVE);
        account.setBalance(BigDecimal.ZERO);
        return account;
    }

    @Test
    void shouldEmitAccountClosedEventWhenCommandIsValid() {
        // Given
        String id = "ACC-001";
        AccountAggregate account = createActiveAccount(id);
        CloseAccountCmd cmd = new CloseAccountCmd(id);

        // When
        List<DomainEvent> events = account.execute(cmd);

        // Then
        assertEquals(1, events.size(), "Should emit one event");
        assertTrue(events.get(0) instanceof AccountClosedEvent, "Event should be AccountClosedEvent");
        assertEquals(AccountAggregate.Status.CLOSED, account.getStatus(), "Account status should be CLOSED");
        assertEquals(id, events.get(0).aggregateId(), "Event aggregate ID should match");
    }

    @Test
    void shouldRejectCommandIfBalanceIsNonZero() {
        // Given - Violates: "Account balance cannot drop below the minimum required balance"
        // Note: Closing usually requires 0 balance.
        String id = "ACC-002";
        AccountAggregate account = createActiveAccount(id);
        account.setBalance(new BigDecimal("100.00")); // Non-zero balance
        CloseAccountCmd cmd = new CloseAccountCmd(id);

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("non-zero balance"), "Error message should mention balance issue");
    }

    @Test
    void shouldRejectCommandIfStatusIsNotActive() {
        // Given - Violates: "An account must be in an Active status"
        String id = "ACC-003";
        AccountAggregate account = new AccountAggregate(id);
        account.setStatus(AccountAggregate.Status.CLOSED); // Already closed
        account.setBalance(BigDecimal.ZERO);
        CloseAccountCmd cmd = new CloseAccountCmd(id);

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("ACTIVE"), "Error message should mention ACTIVE status requirement");
    }

    @Test
    void shouldRejectCommandIfAccountNumberMismatch() {
        // Given - Violates: "Account numbers must be uniquely generated and immutable"
        // Simulating an attempt to close a different account via the wrong aggregate instance
        String aggregateId = "ACC-004";
        String commandId = "ACC-999"; // Different ID
        
        AccountAggregate account = createActiveAccount(aggregateId);
        CloseAccountCmd cmd = new CloseAccountCmd(commandId);

        // When & Then
        // The aggregate enforces immutability by ensuring the command targets the specific aggregate instance
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("does not match"), "Error message should mention ID mismatch");
    }
}
