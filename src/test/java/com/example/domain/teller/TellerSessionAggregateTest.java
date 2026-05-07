package com.example.domain.teller;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for TellerSession Aggregate.
 * Covers scenarios S-18 for StartSessionCmd.
 */
class TellerSessionAggregateTest {

    @Test
    void testSuccessfullyExecuteStartSessionCmd() {
        // Given
        String sessionId = "sess-123";
        String tellerId = "teller-01";
        String terminalId = "term-05";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // Valid context: Authenticated, Idle context, Not stale
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId,
            tellerId,
            terminalId,
            true,  // isAuthenticated
            "IDLE", // navigationContext
            false  // isStale
        );

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "An event should be emitted");
        assertTrue(events.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertNotNull(event.occurredAt());

        // Verify Aggregate State
        assertTrue(aggregate.isActive());
        assertEquals(tellerId, aggregate.getTellerId());
    }

    @Test
    void testRejectedWhenNotAuthenticated() {
        // Given
        String sessionId = "sess-456";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // Violation: Not authenticated
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId, "teller-01", "term-01",
            false, "IDLE", false
        );

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertEquals("Authentication required to start session.", ex.getMessage());
    }

    @Test
    void testRejectedWhenSessionTimedOut() {
        // Given
        String sessionId = "sess-789";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // Violation: Stale session (Simulated via command flag)
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId, "teller-01", "term-01",
            true, "IDLE", true // isStale = true
        );

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertEquals("Session has timed out due to inactivity.", ex.getMessage());
    }

    @Test
    void testRejectedWhenNavigationStateInvalid() {
        // Given
        String sessionId = "sess-101";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // Violation: Invalid context (e.g., already in a transaction screen)
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId, "teller-01", "term-01",
            true, "TRANSACTION_IN_PROGRESS", false
        );

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertEquals("Navigation context must be IDLE or INIT to start session.", ex.getMessage());
    }
}
