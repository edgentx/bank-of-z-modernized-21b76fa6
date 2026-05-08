package com.example.domain.account;

import com.example.domain.account.model.*;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Account Aggregate.
 * Written in TDD Red Phase — Implementation does not exist yet.
 */
class AccountAggregateTest {

    // ========================================
    // Scenario: Successfully execute UpdateAccountStatusCmd
    // ========================================
    @Test
    void shouldEmitAccountStatusUpdatedEventWhenCommandIsValid() {
        // Given
        String accountId = "acc-123";
        AccountAggregate account = new AccountAggregate(accountId);
        account.hydrate(
            "123456789", 
            BigDecimal.valueOf(1000.00), 
            AccountStatus.ACTIVE, 
            AccountType.SAVINGS
        );
        
        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("123456789", AccountStatus.FROZEN);

        // When
        List<com.example.domain.shared.DomainEvent> events = account.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Expected at least one event to be emitted");
        assertEquals(1, events.size());

        com.example.domain.shared.DomainEvent event = events.get(0);
        assertTrue(event instanceof AccountStatusUpdatedEvent, "Expected AccountStatusUpdatedEvent");
        
        AccountStatusUpdatedEvent statusEvent = (AccountStatusUpdatedEvent) event;
        assertEquals("account.status.updated", statusEvent.type());
        assertEquals(accountId, statusEvent.aggregateId());
        assertEquals(AccountStatus.ACTIVE, statusEvent.oldStatus());
        assertEquals(AccountStatus.FROZEN, statusEvent.newStatus());
    }

    // ========================================
    // Scenario: Rejected — Balance below minimum
    // ========================================
    @Test
    void shouldRejectStatusUpdateIfBalanceIsBelowMinimumForType() {
        // Given: Account Balance cannot drop below the minimum required balance for its specific account type.
        // Assumption: SAVINGS requires a minimum balance. If balance is low, status update might be restricted.
        // Implementation details of the invariant will drive the exact logic, but the test expects rejection.
        String accountId = "acc-low-balance";
        AccountAggregate account = new AccountAggregate(accountId);
        
        // Hydrate with a low balance (e.g. $50) against a hypothetical minimum of $100
        account.hydrate(
            "987654321", 
            BigDecimal.valueOf(50.00), 
            AccountStatus.ACTIVE, 
            AccountType.SAVINGS
        );

        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("987654321", AccountStatus.FROZEN);

        // When & Then
        // The prompt says: "Given a Account aggregate that violates... When command is executed... rejected"
        // We assume the invariant check prevents the status change.
        assertThrows(IllegalStateException.class, () -> {
            account.execute(cmd);
        }, "Expected domain error for insufficient funds/min balance violation");
    }

    // ========================================
    // Scenario: Rejected — Account not active for transactions
    // ========================================
    @Test
    void shouldRejectStatusUpdateIfAccountIsNotActiveForTransactions() {
        // Given: An account must be in an Active status to process withdrawals or transfers.
        // This invariant implies that non-active accounts cannot change state arbitrarily or
        // that the command handler enforces active checks before processing.
        String accountId = "acc-inactive";
        AccountAggregate account = new AccountAggregate(accountId);
        
        // Account starts as FROZEN
        account.hydrate(
            "111222333", 
            BigDecimal.valueOf(500.00), 
            AccountStatus.FROZEN, 
            AccountType.CHECKING
        );

        // Trying to process a withdrawal-related status change or similar operation
        // Interpreting the AC strictly: The command execution logic should enforce that the account 
        // is currently Active to allow this specific state transition (if it represents a transactional operation)
        // or simply that the invariant is checked during execution.
        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("111222333", AccountStatus.CLOSED);

        // When & Then
        // The prompt requires rejection because the aggregate violates the invariant of being Active.
        assertThrows(IllegalStateException.class, () -> {
            account.execute(cmd);
        }, "Expected domain error: Account must be active to process this command/status change");
    }

    // ========================================
    // Scenario: Rejected — Account number immutable
    // ========================================
    @Test
    void shouldRejectStatusUpdateIfAccountNumberDoesNotMatch() {
        // Given: Account numbers must be uniquely generated and immutable.
        // The aggregate has account number 'AAA'. The command targets 'BBB'.
        String accountId = "acc-immutable";
        AccountAggregate account = new AccountAggregate(accountId);
        account.hydrate(
            "AAA-111", 
            BigDecimal.ZERO, 
            AccountStatus.ACTIVE, 
            AccountType.SAVINGS
        );

        // Command attempts to update status for a different account number on this aggregate
        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("BBB-222", AccountStatus.FROZEN);

        // When & Then
        // This violates the immutability/uniqueness integrity within the aggregate context
        assertThrows(IllegalArgumentException.class, () -> {
            account.execute(cmd);
        }, "Expected domain error: Account number mismatch/immutable violation");
    }
    
    @Test
    void shouldThrowUnknownCommandForUnsupportedCommand() {
        // Given
        AccountAggregate account = new AccountAggregate("id");
        // Hydrate to prevent NPEs in validation logic if any
        account.hydrate("000", BigDecimal.ZERO, AccountStatus.ACTIVE, AccountType.SAVINGS);

        // When
        Command unsupportedCmd = new Command() {}; // Anonymous command

        // Then
        assertThrows(UnknownCommandException.class, () -> account.execute(unsupportedCmd));
    }
}
