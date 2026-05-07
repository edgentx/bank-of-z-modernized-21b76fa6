package com.example.domain.teller;

import com.example.domain.shared.Command;
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
 * TDD Test Suite for TellerSession Aggregate (Story S-18).
 * Covers StartSessionCmd execution and invariant validation.
 */
class TellerSessionAggregateTest {

    // --- Scenario: Successfully execute StartSessionCmd ---
    @Test
    void shouldEmitSessionStartedEventWhenValid() {
        // Given
        String sessionId = "TS-123";
        String tellerId = "T-456";
        String terminalId = "TERM-789";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, true, Instant.now().plusSeconds(3600));

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals("session.started", event.type());
    }

    // --- Scenario: StartSessionCmd rejected — A teller must be authenticated ---
    @Test
    void shouldRejectStartSessionWhenNotAuthenticated() {
        // Given
        String sessionId = "TS-123";
        String tellerId = "T-456";
        String terminalId = "TERM-789";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, false, Instant.now().plusSeconds(3600));

        // When
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));

        // Then
        assertTrue(ex.getMessage().contains("Authentication required"));
    }

    // --- Scenario: StartSessionCmd rejected — Sessions must timeout after configured period ---
    @Test
    void shouldRejectStartSessionWhenTimeoutIsInvalid() {
        // Given
        String sessionId = "TS-123";
        String tellerId = "T-456";
        String terminalId = "TERM-789";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // Timeout in the past violates the invariant logic (e.g. immediate expiry)
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, true, Instant.now().minusSeconds(10));

        // When
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));

        // Then
        assertTrue(ex.getMessage().contains("Session timeout"));
    }

    // --- Scenario: StartSessionCmd rejected — Navigation state must accurately reflect operational context ---
    @Test
    void shouldRejectStartSessionWhenOperationalContextIsInvalid() {
        // Given
        String sessionId = "TS-123";
        String tellerId = "T-456";
        String terminalId = "TERM-789";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // Null terminal ID violates navigation state context
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, null, true, Instant.now().plusSeconds(3600));

        // When
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));

        // Then
        assertTrue(ex.getMessage().contains("Navigation state"));
    }

    // --- Basic Invariants: Null/Blank IDs ---
    @Test
    void shouldRejectCommandWhenTellerIdIsInvalid() {
        // Given
        String sessionId = "TS-123";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        StartSessionCmd cmd = new StartSessionCmd(sessionId, null, "TERM-1", true, Instant.now().plusSeconds(3600));

        // When
        Exception ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));

        // Then
        assertTrue(ex.getMessage().contains("Teller ID required"));
    }

    @Test
    void shouldRejectUnknownCommand() {
        // Given
        String sessionId = "TS-123";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        Command unknownCmd = new Command() { };

        // When
        Exception ex = assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));

        // Then
        assertTrue(ex.getMessage().contains("Unknown command"));
    }
}