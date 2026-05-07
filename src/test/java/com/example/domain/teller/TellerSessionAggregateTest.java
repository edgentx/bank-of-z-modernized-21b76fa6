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
 * TDD Red Phase Tests for Story S-18: TellerSession StartSessionCmd.
 */
class TellerSessionAggregateTest {

    // Common constants
    private static final String SESSION_ID = "TS-123";
    private static final String TELLER_ID = "T-42";
    private static final String TERMINAL_ID = "TERM-01";

    @Test
    void scenario_Successfully_execute_StartSessionCmd() {
        // Given a valid TellerSession aggregate
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        // And a valid tellerId is provided
        // And a valid terminalId is provided
        StartSessionCmd cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID);

        // When the StartSessionCmd command is executed
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then a session.started event is emitted
        assertFalse(events.isEmpty(), "Should emit an event");
        assertEquals(1, events.size(), "Should emit exactly one event");

        DomainEvent event = events.get(0);
        assertInstanceOf(SessionStartedEvent.class, event, "Event must be SessionStartedEvent");

        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(SESSION_ID, startedEvent.aggregateId());
        assertEquals(TELLER_ID, startedEvent.tellerId());
        assertEquals(TERMINAL_ID, startedEvent.terminalId());
        assertNotNull(startedEvent.occurredAt());
    }

    @Test
    void scenario_StartSessionCmd_rejected_unauthenticated() {
        // Given a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.
        // (Assume the aggregate state represents an unauthenticated context)
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        StartSessionCmd cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID);

        // When the StartSessionCmd command is executed
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        // Then the command is rejected with a domain error
        assertTrue(exception.getMessage().contains("authenticated"));
    }

    @Test
    void scenario_StartSessionCmd_rejected_timeout_violation() {
        // Given a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.
        // (Assume the aggregate state represents a timed-out context)
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        StartSessionCmd cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID);

        // When the StartSessionCmd command is executed
        // Then the command is rejected with a domain error
        // Implementation must enforce inactivity timeout checks (e.g. lastActivityAt)
        // For this test, we expect the behavior to eventually enforce this.
        // Since we are in Red Phase, we might throw this exception manually or assert on state.
        // However, the requirement is to reject on specific violation. We verify the exception type.
        
        // Note: The actual logic to trigger this specific exception will be part of the implementation.
        // We assume the aggregate checks validity.
        Exception exception = assertThrows(IllegalStateException.class, () -> {
             aggregate.execute(cmd);
        });
        
        // Verify specific error context if possible, or general rejection
        assertTrue(exception.getMessage().contains("timeout") || exception.getMessage().contains("inactive"));
    }

    @Test
    void scenario_StartSessionCmd_rejected_invalid_navigation_state() {
        // Given a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.
        // (e.g. trying to start a session while already in a transaction screen)
        TellerSessionAggregate aggregate = new TellerSessionAggregate(SESSION_ID);
        StartSessionCmd cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID);

        // When the StartSessionCmd command is executed
        // Then the command is rejected with a domain error
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("navigation") || exception.getMessage().contains("state"));
    }
}
