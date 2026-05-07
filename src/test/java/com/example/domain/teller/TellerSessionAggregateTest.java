package com.example.domain.teller;

import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.SessionStartedEvent;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for TellerSessionAggregate.
 * Covers acceptance criteria for StartSessionCmd.
 */
class TellerSessionAggregateTest {

    // Scenario: Successfully execute StartSessionCmd
    @Test
    void testSuccessfulStartSession() {
        // Arrange
        String sessionId = "session-123";
        String tellerId = "teller-01";
        String terminalId = "term-42";
        Duration timeout = Duration.ofMinutes(30);
        Instant authTime = Instant.now();

        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, timeout, authTime);

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals("session.started", event.type());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated
    @Test
    void testRejectedWhenNotAuthenticated() {
        // Arrange
        String sessionId = "session-456";
        // authenticatedAt is null implies not authenticated in this context
        StartSessionCmd cmd = new StartSessionCmd(sessionId, "teller-01", "term-42", Duration.ofMinutes(30), null);
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> aggregate.execute(cmd)
        );
        
        assertTrue(exception.getMessage().contains("must be authenticated"));
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout
    @Test
    void testRejectedWhenTimeoutInvalid() {
        // Arrange
        String sessionId = "session-789";
        // Zero or Negative duration violates the invariant
        Duration invalidTimeout = Duration.ZERO;
        StartSessionCmd cmd = new StartSessionCmd(
            sessionId, 
            "teller-01", 
            "term-42", 
            invalidTimeout, 
            Instant.now()
        );
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> aggregate.execute(cmd)
        );

        assertTrue(exception.getMessage().contains("timeout"));
    }

    // Scenario: StartSessionCmd rejected — Navigation state
    @Test
    void testRejectedWhenNavigationStateInvalid() {
        // Arrange
        String sessionId = "session-nav-01";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate invalid navigation context via test hook
        aggregate.markNavigationContextInvalid();

        StartSessionCmd cmd = new StartSessionCmd(
            sessionId, 
            "teller-01", 
            "term-42", 
            Duration.ofMinutes(30), 
            Instant.now()
        );

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> aggregate.execute(cmd)
        );

        assertTrue(exception.getMessage().contains("Navigation state"));
    }
}
