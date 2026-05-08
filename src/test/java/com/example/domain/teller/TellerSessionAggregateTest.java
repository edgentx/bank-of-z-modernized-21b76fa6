package com.example.domain.teller;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TellerSessionAggregateTest {

    @Test
    public void testExecuteEndSessionCmd_Success() {
        // Given
        String sessionId = "SESSION-123";
        String tellerId = "TELLER-42";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // Simulate an authenticated session state (bypassing Init command for isolation)
        // In a real scenario, we'd load from events or invoke Init.
        // For TDD, we assume the internal state can be set or is initially valid for this command context.
        // We will validate that IF it is valid, the command works.
        
        // However, since EndSessionCmd is the focus, we assume the aggregate is created.
        EndSessionCmd cmd = new EndSessionCmd(sessionId, Instant.now());

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should emit exactly one event");
        assertTrue(events.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) events.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Test
    public void testExecuteEndSessionCmd_Rejected_NotAuthenticated() {
        // Given
        String sessionId = "SESSION-UNAUTH";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // State: No teller ID implies not authenticated for this session context
        EndSessionCmd cmd = new EndSessionCmd(sessionId, Instant.now());

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("Teller must be authenticated"));
    }

    @Test
    public void testExecuteEndSessionCmd_Rejected_SessionTimedOut() {
        // Given
        String sessionId = "SESSION-TIMEOUT";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // State: Last activity time is way in the past
        // We rely on the constructor or state setter to establish this "violated" state
        // or the command execution logic checks system time vs last activity.
        EndSessionCmd cmd = new EndSessionCmd(sessionId, Instant.now());

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("Session has timed out"));
    }

    @Test
    public void testExecuteEndSessionCmd_Rejected_NavigationStateInvalid() {
        // Given
        String sessionId = "SESSION-NAV-ERR";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // State: Current screen is null or invalid context
        EndSessionCmd cmd = new EndSessionCmd(sessionId, Instant.now());

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertTrue(ex.getMessage().contains("Navigation state is invalid"));
    }
}
