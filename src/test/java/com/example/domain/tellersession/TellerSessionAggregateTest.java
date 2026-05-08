package com.example.domain.tellersession;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase for S-20: EndSessionCmd on TellerSession.
 * 
 * Stories covered:
 * - Successfully execute EndSessionCmd
 * - Rejection: Authentication required
 * - Rejection: Inactivity timeout
 * - Rejection: Navigation state context validity
 */
class TellerSessionAggregateTest {

    private static final String TELLER_ID = "tell-100";
    private static final String SESSION_ID = "sess-200";
    private static final String INVALID_NAV_STATE = "UNKNOWN_MENU";

    // --- HAPPY PATH ---

    @Test
    void givenValidSession_whenExecuteEndSessionCmd_thenEmitsSessionEndedEvent() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        // Simulate valid state by injecting via package-private test hooks (once implemented)
        // For Red phase, we assume the aggregate can be constructed or hydrated.
        
        EndSessionCmd cmd = new EndSessionCmd(SESSION_ID);

        // When
        List actualEvents = aggregate.execute(cmd);

        // Then
        assertNotNull(actualEvents, "Event list should not be null");
        assertEquals(1, actualEvents.size(), "Should emit exactly one event");
        assertTrue(actualEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) actualEvents.get(0);
        assertEquals(SESSION_ID, event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    // --- AUTHENTICATION FAILURE ---

    @Test
    void givenUnauthenticatedTeller_whenExecuteEndSessionCmd_thenThrowsIllegalState() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        // In Red phase, default constructor implies no auth state set.
        EndSessionCmd cmd = new EndSessionCmd(SESSION_ID);

        // When & Then
        // Expecting specific IllegalStateException or DomainException for auth failure
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("authenticated"));
    }

    // --- TIMEOUT FAILURE ---

    @Test
    void givenExpiredSession_whenExecuteEndSessionCmd_thenThrowsIllegalState() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        // Hydrate aggregate with a state that implies expiration (e.g. lastActive too old)
        // For this test, we rely on the implementation checking `lastActiveTime`.
        
        EndSessionCmd cmd = new EndSessionCmd(SESSION_ID);

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("timeout") || exception.getMessage().contains("inactive"));
    }

    // --- NAVIGATION STATE FAILURE ---

    @Test
    void givenInvalidNavigationState_whenExecuteEndSessionCmd_thenThrowsIllegalState() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        // Simulate state where navigation is invalid (e.g. deep in a transaction modal)
        
        EndSessionCmd cmd = new EndSessionCmd(SESSION_ID);

        // When & Then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Navigation") || exception.getMessage().contains("state"));
    }

    @Test
    void givenUnknownCommand_whenExecute_thenThrowsUnknownCommandException() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        Command badCmd = new Command() {}; // Anonymous command

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(badCmd));
    }
}
