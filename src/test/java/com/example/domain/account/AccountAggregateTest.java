package com.example.domain.account;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountAggregateTest {

    private static final BigDecimal MIN_BALANCE_SAVINGS = new BigDecimal("100.00");
    private static final String CUSTOMER_ID = "cust-123";
    private static final String SORT_CODE = "10-20-30";

    @Test
    void testExecuteOpenAccountCmd_Success() {
        // Given
        String accountId = "acc-new-1";
        AccountAggregate aggregate = new AccountAggregate(accountId);
        OpenAccountCmd cmd = new OpenAccountCmd(
            accountId,
            CUSTOMER_ID,
            "SAVINGS",
            new BigDecimal("500.00"),
            SORT_CODE
        );

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        
        DomainEvent event = events.get(0);
        assertTrue(event instanceof AccountOpenedEvent, "Expected AccountOpenedEvent");
        
        AccountOpenedEvent openedEvent = (AccountOpenedEvent) event;
        assertEquals("account.opened", openedEvent.type());
        assertEquals(accountId, openedEvent.aggregateId());
        assertEquals(CUSTOMER_ID, openedEvent.customerId());
        assertEquals("SAVINGS", openedEvent.accountType());
        assertEquals(new BigDecimal("500.00"), openedEvent.balance());
        assertEquals(SORT_CODE, openedEvent.sortCode());
        assertNotNull(openedEvent.occurredAt());
    }

    @Test
    void testExecuteOpenAccountCmd_InsufficientInitialDeposit_Rejected() {
        // Given
        String accountId = "acc-new-2";
        AccountAggregate aggregate = new AccountAggregate(accountId);
        
        // A deposit below the minimum required balance for the account type
        OpenAccountCmd cmd = new OpenAccountCmd(
            accountId,
            CUSTOMER_ID,
            "SAVINGS", // Requires 100.00
            new BigDecimal("50.00"), // Only 50.00 provided
            SORT_CODE
        );

        // When & Then
        // Note: The domain rule is that the balance cannot drop below the minimum.
        // Opening with 50.00 violates the invariant for SAVINGS.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("below minimum"));
    }

    @Test
    void testExecuteOpenAccountCmd_InvalidStatus_Rejected() {
        // Given
        // This test validates the invariant: "An account must be in an Active status to process withdrawals or transfers."
        // While this is primarily for subsequent commands, the Aggregate construction or state validation
        // should prevent processing if the aggregate is not in a valid state to accept new commands.
        // Here we assume the aggregate defaults to a non-active state or requires specific initialization.
        
        String accountId = "acc-frozen-1";
        AccountAggregate aggregate = new AccountAggregate(accountId);
        
        // Simulate an aggregate that is not Active (e.g., logic inside execute checks status)
        OpenAccountCmd cmd = new OpenAccountCmd(
            accountId,
            CUSTOMER_ID,
            "CHECKING",
            new BigDecimal("100.00"),
            SORT_CODE
        );

        // When & Then
        // This assertion enforces the design rule that we cannot proceed if status checks fail
        Exception exception = assertThrows(IllegalStateException.class, () -> {
             aggregate.execute(cmd);
        });
        
        assertTrue(exception.getMessage().contains("Active status"));
    }

    @Test
    void testExecuteOpenAccountCmd_DuplicateAccountNumber_Rejected() {
        // Given
        String accountId = "acc-dup-1";
        AccountAggregate aggregate = new AccountAggregate(accountId);
        
        OpenAccountCmd cmd = new OpenAccountCmd(
            accountId,
            CUSTOMER_ID,
            "CHECKING",
            new BigDecimal("100.00"),
            SORT_CODE
        );

        // When & Then
        // Invariants state: Account numbers must be uniquely generated and immutable.
        // If the ID generation logic fails or detects a collision, it must reject.
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(exception.getMessage().contains("unique") || exception.getMessage().contains("immutable"));
    }

    @Test
    void testExecuteUnknownCommand_ThrowsException() {
        // Given
        String accountId = "acc-unknown";
        AccountAggregate aggregate = new AccountAggregate(accountId);
        Command unknownCmd = new Command() {}; // Anonymous invalid command

        // When & Then
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(unknownCmd);
        });
    }
}
