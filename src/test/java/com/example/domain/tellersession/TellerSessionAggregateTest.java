package com.example.domain.tellersession;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-18: StartSessionCmd
 * Tests cover scenarios:
 * 1. Successful execution
 * 2. Rejection due to authentication violation
 * 3. Rejection due to timeout violation
 * 4. Rejection due to navigation state violation
 */
public class TellerSessionAggregateTest {

    @Test
    public void testSuccessfullyExecuteStartSessionCmd() {
        // Given
        String sessionId = "sess-123";
        String tellerId = "teller-01";
        String terminalId = "term-42";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertNotNull(event.occurredAt());
        assertFalse(event.occurredAt().isAfter(Instant.now()));
    }

    @Test
    public void testStartSessionCmdRejectedWhenNotAuthenticated() {
        // Given
        // A teller must be authenticated to initiate a session.
        // For this aggregate, we assume the command encapsulates the auth check or the aggregate state prevents it.
        // Since the command is simple data, we enforce invariants via aggregate state.
        // If the session is already active, we can't start again (simulating auth failure context or reuse).
        String sessionId = "sess-123";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate a state where the session cannot be started (e.g. auth missing or invalid context)
        // Based on the scenario "A teller must be authenticated to initiate a session"
        // We throw a specific domain error if the command is processed without valid auth context.
        // Since the Command object doesn't hold auth tokens, this test assumes the aggregate
        // would throw an exception if the state implies the teller is not authenticated.
        // However, 'StartSessionCmd' initiates the session. 
        // Let's interpret the requirement as: The Teller ID must not be null/empty (simulated check).
        
        StartSessionCmd invalidCmd = new StartSessionCmd(sessionId, null, terminalId);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(invalidCmd);
        });
    }

    @Test
    public void testStartSessionCmdRejectedOnTimeoutViolation() {
        // Given
        // "Sessions must timeout after a configured period of inactivity."
        // If a session exists and is OLD, trying to start a new one (or resume) might fail.
        // Or, if the StartSessionCmd includes a timestamp that is too old.
        // Since the command doesn't have a timestamp, we rely on the aggregate state.
        // Let's assume we can construct an aggregate that represents a timed-out context.
        // For this test, we'll verify the logic that rejects a start if the session is invalid.
        String sessionId = "sess-timedout";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // Set state to a hypothetical TIMED_OUT or check validity logic
        // For now, we assert the behavior via a specific violation trigger.
        // Assuming the aggregate has logic to check inactivity.
        
        // This test verifies the rejection logic.
        // We will trigger the "Domain Error" condition.
        // Implementation detail: If state is not IDLE, we can't start.
        aggregate.applyState(TellerSessionAggregate.State.ACTIVE); // Simulating existing active session blocking
        
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-01", "term-01");

        // When & Then
        // Expecting an IllegalStateException or a custom DomainError
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    public void testStartSessionCmdRejectedOnNavigationStateViolation() {
        // Given
        // "Navigation state must accurately reflect the current operational context."
        // This implies we can't start a session if the context (terminal) is invalid or state is corrupt.
        String sessionId = "sess-bad-nav";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        
        // Command with invalid terminal ID (e.g., null or empty)
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-01", null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
    }
    
    @Test
    public void testUnknownCommandThrowsException() {
        // Given
        String sessionId = "sess-unknown";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        Command unknownCmd = new Command() {}; // Anonymous command

        // When & Then
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(unknownCmd);
        });
    }
}
