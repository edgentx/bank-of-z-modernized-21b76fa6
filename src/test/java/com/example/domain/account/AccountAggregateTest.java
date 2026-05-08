package com.example.domain.account;

import com.example.domain.account.model.*;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase: Account aggregate tests for CloseAccountCmd.
 * <p>
 * These tests will fail initially as the AccountAggregate implementation is missing.
 * Acceptance Criteria from S-7:
 * 1. Successfully execute CloseAccountCmd (balance zero, active status).
 * 2. Reject if balance is not zero (implies violation of min balance / close logic).
 * 3. Reject if account is not Active.
 * 4. Enforce immutable account number check.
 */
public class AccountAggregateTest {

    // --- Test Data Factory ---

    private final String VALID_ID = "acc-123";
    private final String VALID_ACCOUNT_NUMBER = "99-88-777";

    private AccountAggregate createActiveAccountWithZeroBalance() {
        // Create a fresh aggregate.
        // Since we are using a simplified constructor for testing,
        // we simulate a 'hydrated' state via the constructor if available,
        // or by re-using the Open command logic if the aggregate supports it.
        // Given S-7 is about Close, we assume an account exists.
        // We will create a mock state in the test helper.
        var account = new AccountAggregate(VALID_ID, VALID_ACCOUNT_NUMBER, AccountAggregate.Status.ACTIVE, BigDecimal.ZERO);
        return account;
    }

    // --- Scenario: Successfully execute CloseAccountCmd ---

    @Test
    void whenCloseAccountCmdExecuted_onActiveZeroBalanceAccount_emitsAccountClosedEvent() {
        // Given
        AccountAggregate account = createActiveAccountWithZeroBalance();
        CloseAccountCmd cmd = new CloseAccountCmd(VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_NUMBER);

        // When
        List<DomainEvent> events = account.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Should emit an event");
        assertTrue(events.get(0) instanceof AccountClosedEvent, "Event should be AccountClosedEvent");

        AccountClosedEvent event = (AccountClosedEvent) events.get(0);
        assertEquals("account.closed", event.type());
        assertEquals(VALID_ACCOUNT_NUMBER, event.accountNumber());
    }

    @Test
    void whenCloseAccountCmdExecuted_accountStatusBecomesClosed() {
        // Given
        AccountAggregate account = createActiveAccountWithZeroBalance();
        CloseAccountCmd cmd = new CloseAccountCmd(VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_NUMBER);

        // When
        account.execute(cmd);

        // Then
        assertEquals(AccountAggregate.Status.CLOSED, account.getStatus());
    }

    // --- Scenario: CloseAccountCmd rejected — Balance cannot drop below minimum (must be zero to close) ---

    @Test
    void whenCloseAccountCmdExecuted_withNonZeroBalance_throwsError() {
        // Given
        // Account with balance $50.00
        AccountAggregate account = new AccountAggregate(
                VALID_ID,
                VALID_ACCOUNT_NUMBER,
                AccountAggregate.Status.ACTIVE,
                new BigDecimal("50.00")
        );
        CloseAccountCmd cmd = new CloseAccountCmd(VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_NUMBER);

        // When / Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("balance") || exception.getMessage().contains("Balance"));
    }

    // --- Scenario: CloseAccountCmd rejected — Must be in Active status ---

    @Test
    void whenCloseAccountCmdExecuted_onNonActiveAccount_throwsError() {
        // Given
        AccountAggregate account = new AccountAggregate(
                VALID_ID,
                VALID_ACCOUNT_NUMBER,
                AccountAggregate.Status.DORMANT, // Not active
                BigDecimal.ZERO
        );
        CloseAccountCmd cmd = new CloseAccountCmd(VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_NUMBER);

        // When / Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Active") || exception.getMessage().contains("active"));
    }

    // --- Scenario: CloseAccountCmd rejected — Account numbers must be uniquely generated and immutable ---

    @Test
    void whenCloseAccountCmdExecuted_withMismatchedAccountNumber_throwsError() {
        // Given
        AccountAggregate account = createActiveAccountWithZeroBalance();
        // Trying to close with a different account number than the aggregate holds
        CloseAccountCmd cmd = new CloseAccountCmd(VALID_ACCOUNT_NUMBER, "DIFFERENT-NUMBER");

        // When / Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            account.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("immutable") || exception.getMessage().contains("number"));
    }

}
