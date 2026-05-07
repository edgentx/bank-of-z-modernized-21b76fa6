package com.example.domain.teller;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test Suite for S-18: StartSessionCmd
 * Tests are written for the Red phase (expecting implementation to make them pass).
 */
class TellerSessionAggregateTest {

    // 1. Scenario: Successfully execute StartSessionCmd
    @Test
    void shouldEmitSessionStartedEventWhenValid() {
        // Given
        var aggregateId = "session-123";
        var tellerId = "teller-001";
        var terminalId = "term-42";
        var cmd = new StartSessionCmd(
            aggregateId,
            tellerId,
            terminalId,
            true,  // isAuthenticated
            Instant.now(), // lastActivityAt
            "HOME" // Initial valid state
        );
        var aggregate = new TellerSessionAggregate(aggregateId);

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SessionStartedEvent);
        
        var event = (SessionStartedEvent) events.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregateId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertEquals("HOME", event.navigationState());
        assertNotNull(event.occurredAt());
        
        // Verify Aggregate State
        assertTrue(aggregate.isSessionActive());
        assertEquals(1, aggregate.getVersion());
    }

    // 2. Scenario: StartSessionCmd rejected — A teller must be authenticated to initiate a session.
    @Test
    void shouldRejectIfNotAuthenticated() {
        // Given
        var aggregateId = "session-123";
        var cmd = new StartSessionCmd(
            aggregateId,
            "teller-001",
            "term-42",
            false, // NOT authenticated
            Instant.now(),
            "HOME"
        );
        var aggregate = new TellerSessionAggregate(aggregateId);

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("A teller must be authenticated"));
    }

    // 3. Scenario: StartSessionCmd rejected — Sessions must timeout after a configured period of inactivity.
    @Test
    void shouldRejectIfTimedOut() {
        // Given
        var aggregateId = "session-123";
        // Simulate a timestamp that is definitely older than the default 30 min timeout
        var pastActivity = Instant.now().minusSeconds(3600); // 1 hour ago
        var cmd = new StartSessionCmd(
            aggregateId,
            "teller-001",
            "term-42",
            true,
            pastActivity, 
            "HOME"
        );
        var aggregate = new TellerSessionAggregate(aggregateId);

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Sessions must timeout"));
    }

    // 4. Scenario: StartSessionCmd rejected — Navigation state must accurately reflect the current operational context.
    @Test
    void shouldRejectIfNavigationStateInvalid() {
        // Given
        var aggregateId = "session-123";
        var cmd = new StartSessionCmd(
            aggregateId,
            "teller-001",
            "term-42",
            true,
            Instant.now(),
            "INVALID_STATE" // Not HOME
        );
        var aggregate = new TellerSessionAggregate(aggregateId);

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Navigation state must accurately reflect"));
    }

    // Edge: Unknown command
    @Test
    void shouldThrowUnknownCommandForUnsupportedCmd() {
        // Given
        var aggregate = new TellerSessionAggregate("s-1");
        var unsupportedCmd = new Object() implements com.example.domain.shared.Command {};

        // When & Then
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(unsupportedCmd);
        });
    }
}