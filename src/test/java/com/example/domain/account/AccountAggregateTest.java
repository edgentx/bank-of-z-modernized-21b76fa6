package com.example.domain.account;

import com.example.domain.account.model.*;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-7: CloseAccountCmd.
 * These tests verify the behavior of the Account aggregate.
 */
class AccountAggregateTest {

    // Helper to create a valid active account with zero balance
    private AccountAggregate createActiveZeroAccount() {
        AccountAggregate account = new AccountAggregate("ACC-123", "123456", AccountType.SAVINGS);
        // Simulate the account being opened and active
        account.testOnlySetStatus(AccountStatus.ACTIVE);
        account.testOnlySetBalance(BigDecimal.ZERO);
        return account;
    }

    @Test
    void testExecute_CloseAccount_Success() {
        // Given a valid Account aggregate
        AccountAggregate account = createActiveZeroAccount();
        CloseAccountCmd cmd = new CloseAccountCmd("ACC-123", "123456");

        // When the CloseAccountCmd command is executed
        List events = account.execute(cmd);

        // Then a account.closed event is emitted
        assertFalse(events.isEmpty(), "Should emit an event");
        assertTrue(events.get(0) instanceof AccountClosedEvent, "Should be AccountClosedEvent");

        AccountClosedEvent event = (AccountClosedEvent) events.get(0);
        assertEquals("ACC-123", event.aggregateId());
        assertEquals("account.closed", event.type());
    }

    @Test
    void testExecute_CloseAccount_Rejected_IfBalanceIsNotZero() {
        // Given a Account aggregate with non-zero balance
        AccountAggregate account = createActiveZeroAccount();
        account.testOnlySetBalance(new BigDecimal("100.00"));
        
        CloseAccountCmd cmd = new CloseAccountCmd("ACC-123", "123456");

        // When the CloseAccountCmd command is executed
        // Then the command is rejected with a domain error
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Balance must be zero"));
    }

    @Test
    void testExecute_CloseAccount_Rejected_IfStatusNotActive() {
        // Given a Account aggregate that is not Active (e.g., already closed)
        AccountAggregate account = createActiveZeroAccount();
        account.testOnlySetStatus(AccountStatus.CLOSED);
        
        CloseAccountCmd cmd = new CloseAccountCmd("ACC-123", "123456");

        // When the CloseAccountCmd command is executed
        // Then the command is rejected with a domain error
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Account must be Active"));
    }

    @Test
    void testExecute_CloseAccount_Rejected_IfAccountNumberImmutable() {
        // Given a Account aggregate and a command with a different account number
        AccountAggregate account = createActiveZeroAccount();
        // Trying to close ACC-123 but providing number 999999 in command (Identity mismatch)
        CloseAccountCmd cmd = new CloseAccountCmd("ACC-123", "999999");

        // When the CloseAccountCmd command is executed
        // Then the command is rejected with a domain error
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Account number mismatch"));
    }

    @Test
    void testExecute_UnknownCommand_ThrowsException() {
        // Given an account
        AccountAggregate account = createActiveZeroAccount();
        
        // When executing an unsupported command
        Command badCmd = new Command() { public String toString() { return "BadCommand"; } };

        // Then UnknownCommandException is thrown
        assertThrows(UnknownCommandException.class, () -> {
            account.execute(badCmd);
        });
    }
}
