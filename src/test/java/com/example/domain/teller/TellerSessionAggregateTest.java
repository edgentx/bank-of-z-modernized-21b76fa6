package com.example.domain.teller;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-20: EndSessionCmd.
 *
 * Context:
 * - TellerSession (Aggregate)
 * - EndSessionCmd (Command)
 * - SessionEndedEvent (Event)
 *
 * Invariants Enforced:
 * 1. AuthZ: Teller must be authenticated (cannot end a session that was never started/invalid).
 * 2. Timeout: Session cannot be ended if it has already timed out (system must reject).
 * 3. Nav State: Navigation state must be accurate (cannot end in a transient/mismatched state).
 */
class TellerSessionAggregateTest {

    // Valid standard data setup
    private UUID validSessionId;
    private String validTellerId;
    private Instant now;
    private Duration defaultTimeout;

    @BeforeEach
    void setUp() {
        validSessionId = UUID.randomUUID();
        validTellerId = "TELLER_100";
        now = Instant.now();
        defaultTimeout = Duration.ofMinutes(15);
    }

    // --- SCENARIO 1: Successfully execute EndSessionCmd ---
    @Test
    void whenEndSessionCmdExecuted_thenSessionEndedEventIsEmitted() {
        // Given
        // We assume a constructor TellerSession(sessionId, tellerId, lastActivity, timeout)
        // We assume a method setNavigationState to set the valid context.
        TellerSession session = new TellerSession(validSessionId, validTellerId, now, defaultTimeout);
        session.setNavigationState("IDLE"); // Valid state
        EndSessionCmd cmd = new EndSessionCmd(validSessionId, validTellerId);

        // When
        List<DomainEvent> events = session.execute(cmd);

        // Then
        assertEquals(1, events.size(), "Should emit exactly one event");
        assertTrue(events.get(0) instanceof SessionEndedEvent, "Event type must be SessionEndedEvent");

        SessionEndedEvent endedEvent = (SessionEndedEvent) events.get(0);
        assertEquals(validSessionId, endedEvent.sessionId());
        assertEquals(validTellerId, endedEvent.tellerId());
        assertNotNull(endedEvent.occurredAt());
    }

    // --- SCENARIO 2: EndSessionCmd rejected — A teller must be authenticated ---
    @Test
    void whenTellerNotAuthenticated_thenCommandIsRejected() {
        // Given
        // Simulate an unauthenticated state. In this domain, we can model this by
        // passing a NULL or BLANK tellerId, or a specific unauthenticated flag.
        TellerSession session = new TellerSession(validSessionId, null, now, defaultTimeout); 
        EndSessionCmd cmd = new EndSessionCmd(validSessionId, validTellerId);

        // When & Then
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("authenticated"));
    }

    // --- SCENARIO 3: EndSessionCmd rejected — Sessions must timeout ---
    @Test
    void whenSessionTimedOut_thenCommandIsRejected() {
        // Given
        // Simulate a session where 'lastActivity' was 16 minutes ago, but timeout is 15 minutes.
        Instant oldActivity = now.minus(defaultTimeout).minusSeconds(60);
        TellerSession session = new TellerSession(validSessionId, validTellerId, oldActivity, defaultTimeout);
        
        EndSessionCmd cmd = new EndSessionCmd(validSessionId, validTellerId);

        // When & Then
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("timeout") || ex.getMessage().contains("inactive"));
    }

    // --- SCENARIO 4: EndSessionCmd rejected — Navigation state must accurately reflect current operational context ---
    @Test
    void whenNavigationStateInvalid_thenCommandIsRejected() {
        // Given
        // Simulate a session where the Nav State is inconsistent (e.g., null or unknown).
        TellerSession session = new TellerSession(validSessionId, validTellerId, now, defaultTimeout);
        // We assume a method or constructor allows us to force an invalid state for testing invariants.
        session.setNavigationState("UNKNOWN_CONTEXT"); 

        EndSessionCmd cmd = new EndSessionCmd(validSessionId, validTellerId);

        // When & Then
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("Navigation") || ex.getMessage().contains("state"));
    }

    // --- NEGATIVE: Unknown Command ---
    @Test
    void whenUnknownCommand_thenThrowsUnknownCommandException() {
        TellerSession session = new TellerSession(validSessionId, validTellerId, now, defaultTimeout);
        
        // Create a fake command that isn't EndSessionCmd
        Command invalid = new Command() {};

        assertThrows(UnknownCommandException.class, () -> session.execute(invalid));
    }

    // --- HELPER Mock/Record for testing (Required for compilation) ---
    private interface Command {} // minimal duplicate to ensure file compiles if shared isn't imported immediately
}
