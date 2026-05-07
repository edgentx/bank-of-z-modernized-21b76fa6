package com.example.domain.teller;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test suite for S-19: NavigateMenuCmd.
 * These tests validate the command execution and invariants of the TellerSession Aggregate.
 */
class TellerSessionTest {

    // --- Scenario: Successfully execute NavigateMenuCmd ---
    @Test
    void whenNavigateMenuExecuted_thenEmitsMenuNavigatedEvent() {
        // Given a valid TellerSession aggregate
        String sessionId = "session-123";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Setup authenticated state
        aggregate.setLastActivity(Instant.now()); // Setup active state

        // And a valid menuId/action is provided
        String targetMenu = "TX_MENU";
        String targetAction = "DEPOSIT";
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, targetMenu, targetAction);

        // When the NavigateMenuCmd command is executed
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then a menu.navigated event is emitted
        assertFalse(events.isEmpty(), "Should emit an event");
        assertEquals(1, events.size(), "Should emit exactly one event");
        
        DomainEvent event = events.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(sessionId, navEvent.aggregateId());
        assertEquals(targetMenu, navEvent.menuId());
        assertEquals(targetAction, navEvent.action());
        assertNotNull(navEvent.occurredAt());
    }

    // --- Scenario: NavigateMenuCmd rejected — A teller must be authenticated ---
    @Test
    void givenUnauthenticatedSession_whenExecuteNavigateMenu_thenThrowsError() {
        // Given a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.
        TellerSessionAggregate aggregate = new TellerSessionAggregate("session-xyz");
        // aggregate.markAuthenticated() is NOT called.

        NavigateMenuCmd cmd = new NavigateMenuCmd("session-xyz", "MAIN_MENU", "OPEN");

        // When the NavigateMenuCmd command is executed
        // Then the command is rejected with a domain error
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("authenticated"));
    }

    // --- Scenario: NavigateMenuCmd rejected — Sessions must timeout ---
    @Test
    void givenExpiredSession_whenExecuteNavigateMenu_thenThrowsError() {
        // Given a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.
        TellerSessionAggregate aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Set last activity to 16 minutes ago (Timeout is 15)
        aggregate.setLastActivity(Instant.now().minus(16, ChronoUnit.MINUTES));

        NavigateMenuCmd cmd = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "OPEN");

        // When the NavigateMenuCmd command is executed
        // Then the command is rejected with a domain error
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("timeout"));
    }

    // --- Scenario: NavigateMenuCmd rejected — Navigation state must accurately reflect current context ---
    @Test
    void givenStaleNavigationState_whenExecuteNavigateMenu_thenThrowsError() {
        // Given a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.
        TellerSessionAggregate aggregate = new TellerSessionAggregate("session-stale");
        aggregate.markAuthenticated();
        aggregate.setLastActivity(Instant.now());
        
        // Simulate that the aggregate *thinks* it is already at the target context
        String existingMenu = "INQUIRY_MENU";
        String existingAction = "VIEW_BAL";
        aggregate.setCurrentContext(existingMenu, existingAction);

        // Command requests navigation to the exact same context
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-stale", existingMenu, existingAction);

        // When the NavigateMenuCmd command is executed
        // Then the command is rejected with a domain error
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Navigation state"));
    }

    @Test
    void givenUnknownCommand_whenExecute_thenThrowsException() {
        TellerSessionAggregate aggregate = new TellerSessionAggregate("session-unknown");
        aggregate.markAuthenticated();
        aggregate.setLastActivity(Instant.now());

        // Pass a dummy command object that isn't handled
        Command dummyCmd = new Command() {}; 

        assertThrows(UnknownCommandException.class, () -> aggregate.execute(dummyCmd));
    }
}
