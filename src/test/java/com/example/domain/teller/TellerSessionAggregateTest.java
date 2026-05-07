package com.example.domain.teller;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test Class for S-19: TellerSession Aggregate.
 * RED PHASE: These tests fail against an empty/stub implementation.
 */
class TellerSessionAggregateTest {

    private TellerSessionAggregate aggregate;
    private static final String SESSION_ID = "sess-123";
    private static final String MENU_ID = "TX100";
    private static final String ACTION = "DEPOSIT";

    @BeforeEach
    void setUp() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup a valid state for the positive case
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    // --- SCENARIO 1: Successfully execute NavigateMenuCmd ---
    @Test
    void givenValidSession_whenExecuteNavigateMenuCmd_thenMenuNavigatedEventEmitted() {
        // Given
        NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, MENU_ID, ACTION);

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertTrue(events.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(MENU_ID, event.menuId());
        assertEquals(ACTION, event.action());
        assertNotNull(event.occurredAt());
    }

    // --- SCENARIO 2: Rejected — Not Authenticated ---
    @Test
    void givenUnauthenticatedSession_whenExecuteNavigateMenuCmd_thenThrowsDomainError() {
        // Given
        aggregate.setAuthenticated(false); // Violates invariant
        NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, MENU_ID, ACTION);

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("authenticated"));
    }

    // --- SCENARIO 3: Rejected — Session Timeout ---
    @Test
    void givenExpiredSession_whenExecuteNavigateMenuCmd_thenThrowsDomainError() {
        // Given
        // Set activity to 2 hours ago (assuming 30 min timeout)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
        NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, MENU_ID, ACTION);

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("timeout") || exception.getMessage().contains("inactive"));
    }

    // --- SCENARIO 4: Rejected — Invalid Context ---
    @Test
    void givenInvalidNavigationContext_whenExecuteNavigateMenuCmd_thenThrowsDomainError() {
        // Given
        // For example, navigating from a menu that doesn't allow "DEPOSIT" action
        // This simulates "Navigation state must accurately reflect..."
        NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, "INVALID_MENU", ACTION);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("context") || exception.getMessage().contains("menu"));
    }

    // --- BOUNDARY: Invalid Command Type ---
    @Test
    void givenUnknownCommand_whenExecute_thenThrowsUnknownCommandException() {
        // Given
        Object badCmd = new Object() {}; // Not a valid command record

        // When & Then (The pattern in existing aggregates suggests wrapping generic Command)
        // Since we can't pass raw Object, we expect the aggregate to handle navigation specifically.
        // If we passed a valid Command interface that isn't NavigateMenuCmd, it should explode.
        // (This is covered by the default switch case in the stub)
        assertThrows(UnknownCommandException.class, () -> {
            // We simulate calling execute with a command the aggregate doesn't know.
            // In a real test setup, we might need a FakeCommand record implementing Command.
            aggregate.execute(new FakeCmd());
        });
    }

    private record FakeCmd() implements com.example.domain.shared.Command {}
}
