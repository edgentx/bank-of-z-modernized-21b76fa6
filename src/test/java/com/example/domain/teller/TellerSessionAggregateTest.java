package com.example.domain.teller;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-18: TellerSession StartSessionCmd
 */
class TellerSessionAggregateTest {

    private static final String SESSION_ID = "session-123";
    private static final String TELLER_ID = "teller-01";
    private static final String TERMINAL_ID = "term-42";

    @Test
    void testSuccessfullyExecuteStartSessionCmd() {
        // Given
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, true);

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        
        var event = events.get(0);
        assertInstanceOf(SessionStartedEvent.class, event);
        
        var startedEvent = (SessionStartedEvent) event;
        assertEquals("teller.session.started", startedEvent.type());
        assertEquals(SESSION_ID, startedEvent.aggregateId());
        assertEquals(TELLER_ID, startedEvent.tellerId());
        assertEquals(TERMINAL_ID, startedEvent.terminalId());
        assertNotNull(startedEvent.occurredAt());
    }

    @Test
    void testStartSessionCmdRejected_TellerNotAuthenticated() {
        // Given
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, false); // Not authenticated

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("authenticated"));
    }

    @Test
    void testStartSessionCmdRejected_OperationalContextInvalid() {
        // Given
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        // Invalid terminal ID (blank)
        var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, "", true);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("Terminal ID"));
    }

    @Test
    void testStartSessionCmdRejected_InvalidCommand() {
        // Given
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        // Unknown command type
        Object badCmd = new Object();

        // When & Then
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute((com.example.domain.shared.Command) badCmd);
        });
    }
}