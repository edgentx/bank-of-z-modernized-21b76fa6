package com.example.domain.teller;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test Suite for S-18: TellerSession StartSessionCmd.
 * These tests MUST FAIL initially (Red Phase) until the aggregate implementation is added.
 */
class TellerSessionTest {

    // Scenario: Successfully execute StartSessionCmd
    @Test
    void shouldEmitSessionStartedEvent_whenValidCommandProvided() {
        // Given
        String sessionId = "TS-123";
        String tellerId = "USER-001";
        String terminalId = "T-TERM-01";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, Instant.now().plusSeconds(3600));

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Should emit an event");
        assertTrue(events.get(0) instanceof SessionStartedEvent, "Should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertNotNull(event.occurredAt());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated
    // The description implies the Command should carry auth state, or we simulate the aggregate checking a precondition.
    // Assuming the Command carries the flag based on 'violates: A teller must be authenticated'.
    @Test
    void shouldThrowError_whenTellerNotAuthenticated() {
        // Given
        String sessionId = "TS-404";
        // isAuthenticated = false
        StartSessionCmd cmd = new StartSessionCmd("U-1", "T-1", Instant.now(), false);
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // When / Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("authenticated"), "Error message should mention authentication");
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout after a configured period
    // Violation: The requested timeout period exceeds the system configuration.
    @Test
    void shouldThrowError_whenTimeoutConfigInvalid() {
        // Given
        String sessionId = "TS-500";
        // Requesting 10 hours, assuming max is 1 hour (3600s)
        StartSessionCmd cmd = new StartSessionCmd("U-1", "T-1", Instant.now().plusSeconds(36000), true);
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("timeout") || ex.getMessage().contains("inactive"), 
            "Error message should mention timeout policy");
    }

    // Scenario: StartSessionCmd rejected — Navigation state must be accurate
    // Violation: Navigation state provided is invalid or null.
    @Test
    void shouldThrowError_whenNavigationStateInvalid() {
        // Given
        String sessionId = "TS-NAV";
        // isValidNavigationState = false
        StartSessionCmd cmd = new StartSessionCmd("U-1", "T-1", Instant.now().plusSeconds(3600), true, false);
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // When / Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("Navigation") || ex.getMessage().contains("context"), 
            "Error message should mention Navigation state");
    }

    @Test
    void shouldRejectUnknownCommands() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate("TS-UNK");
        
        // When / Then
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(new Object() {}); // Fake command
        });
    }
}
