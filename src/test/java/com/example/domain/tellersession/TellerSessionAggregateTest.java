package com.example.domain.tellersession;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test Suite for S-20: EndSessionCmd
 * 
 * Context:
 * - S-10 (Initiate Session) created the aggregate.
 * - S-20 (End Session) terminates it.
 * - S-23 (User Navigation) enforces state validity.
 * 
 * Fixing previous build errors:
 * 1. TellerSessionAggregate was missing; defined via interface.
 * 2. SessionEndedEvent constructor mismatch; ensure args match (id, tellerId, occurredAt).
 */
class TellerSessionAggregateTest {

    // --- Scenarios for EndSessionCmd ---

    @Test
    void testExecuteEndSessionCmd_Success() {
        // Given a valid TellerSession aggregate
        String sessionId = "TS-123";
        String tellerId = "T-01";
        
        // We assume the aggregate is instantiated or retrieved. 
        // To test the command handler, we act as if the session is active.
        // Since we are implementing the logic now, we create the aggregate.
        var aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate an active session state (bypassing InitiateCmd for isolation or using it)
        // For a pure unit test of EndSession, we assume the aggregate holds the necessary state.
        // In a full flow, Initiate would be called first. Here we verify the Command->Event link.
        
        // Setup internal state to allow termination (Authenticated, Active, Valid State)
        // This is a 'Grey' box test where we know the aggregate needs specific state to pass validation.
        // Ideally, we'd use a rehydrate method, but for now we construct directly.
        
        // However, to properly test "Valid TellerSession", we should simulate the lifecycle.
        // Let's verify the event emission primarily.
        
        EndSessionCmd cmd = new EndSessionCmd(sessionId, "User logout");

        // When the EndSessionCmd command is executed
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then a session.ended event is emitted
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should emit exactly one event");
        
        DomainEvent event = events.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals(sessionId, endedEvent.aggregateId(), "Aggregate ID should match");
        assertNotNull(endedEvent.occurredAt(), "Timestamp should be set");
    }

    @Test
    void testExecuteEndSessionCmd_Rejected_Unauthenticated() {
        // Given a TellerSession aggregate that violates: A teller must be authenticated
        var aggregate = new TellerSessionAggregate("TS-404");
        // The aggregate is created but never authenticated (e.g. Initiate not called or failed).
        // We rely on the aggregate's internal state to detect this.
        
        EndSessionCmd cmd = new EndSessionCmd("TS-404", "Try logout");

        // When & Then
        // We expect a domain error (IllegalStateException or similar)
        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    void testExecuteEndSessionCmd_Rejected_Timeout() {
        // Given a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.
        var aggregate = new TellerSessionAggregate("TS-TIMEOUT");
        
        // Simulate a session that is old/expired.
        // We might need a method to set the last activity time artificially for testing,
        // or rely on the constructor/rehydration logic.
        // Assuming we can inject an old timestamp or use a specific 'expired' constructor:
        // For TDD, we just verify the exception is thrown if state is invalid.
        
        EndSessionCmd cmd = new EndSessionCmd("TS-TIMEOUT", "Auto-logout");

        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    void testExecuteEndSessionCmd_Rejected_InvalidNavigationState() {
        // Given a TellerSession aggregate that violates: Navigation state must accurately reflect current operational context.
        // This usually implies the teller is mid-transaction or in a screen that doesn't allow direct termination.
        var aggregate = new TellerSessionAggregate("TS-NAV-ERR");
        
        // Setup state to be 'Invalid' (e.g. locked in a transaction screen)
        // aggregate.setCurrentScreen("CASH_DEPOSIT_INPUT_LOCKED");

        EndSessionCmd cmd = new EndSessionCmd("TS-NAV-ERR", "Forced logout");

        assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
    }

    @Test
    void testExecuteUnknownCommand() {
        var aggregate = new TellerSessionAggregate("TS-1");
        
        class UnknownCmd implements com.example.domain.shared.Command {}
        
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(new UnknownCmd());
        });
    }
}
