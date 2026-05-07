package com.example.domain.teller;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-18: StartSessionCmd
 * These tests are written expecting the initial implementation to fail or not exist.
 */
class TellerSessionAggregateTest {

    @Test
    void testExecute_StartSessionCmd_Success() {
        // Given
        String sessionId = "session-123";
        String tellerId = "teller-01";
        String terminalId = "term-A";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Setup to bypass auth check for happy path
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);

        // When
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals("session.started", event.type());
    }

    @Test
    void testExecute_StartSessionCmd_Rejected_NotAuthenticated() {
        // Given
        String sessionId = "session-456";
        String tellerId = "teller-01";
        String terminalId = "term-A";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT markAuthenticated
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("A teller must be authenticated"));
    }

    @Test
    void testExecute_StartSessionCmd_Rejected_InvalidNavigationState() {
        // Given
        String sessionId = "session-789";
        String tellerId = "teller-01";
        String terminalId = ""; // Invalid terminal ID
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);

        // When & Then
        // The specific error depends on how the aggregate validates terminalId inside the command vs state.
        // Based on the stub implementation, it checks the passed command context.
        Exception ex = assertThrows(Exception.class, () -> {
            aggregate.execute(cmd);
        });
        
        assertTrue(ex.getMessage().contains("Navigation state") || ex.getMessage().contains("terminal ID"));
    }
}