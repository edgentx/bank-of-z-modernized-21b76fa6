package com.example.domain.teller;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-18: Implement StartSessionCmd.
 *
 * Notes on Implementation:
 * - Tests are written to fail against an empty or stubbed implementation.
 * - The Domain Error is represented by RuntimeException (or specific subclass) for validation failures.
 * - Timeouts are determined by checking the 'authenticatedAt' timestamp against a 'now' reference.
 */
class TellerSessionAggregateTest {

    // Scenario: Successfully execute StartSessionCmd
    @Test
    void testExecuteStartSessionCmd_Success() {
        // Given
        String sessionId = "session-101";
        String tellerId = "teller-alice";
        String terminalId = "term-01";
        Instant authTime = Instant.now();
        
        var aggregate = new TellerSessionAggregate(sessionId);
        var cmd = new StartSessionCmd(sessionId, tellerId, terminalId, authTime);

        // When
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Should emit an event");
        assertTrue(events.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals(sessionId, event.sessionId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated
    @Test
    void testExecuteStartSessionCmd_Rejected_NotAuthenticated() {
        // Given
        // Violation: authenticatedAt is null implies the command wasn't constructed with valid auth context
        var aggregate = new TellerSessionAggregate("session-102");
        var cmd = new StartSessionCmd("session-102", "teller-bob", "term-02", null);

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("authenticated") || exception.getMessage().contains("required"));
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout after a configured period of inactivity
    @Test
    void testExecuteStartSessionCmd_Rejected_SessionTimeout() {
        // Given
        // Violation: authenticatedAt is 31 minutes ago (assuming 30 min timeout)
        Instant pastAuthTime = Instant.now().minus(Duration.ofMinutes(31));
        var aggregate = new TellerSessionAggregate("session-103");
        var cmd = new StartSessionCmd("session-103", "teller-charlie", "term-03", pastAuthTime);

        // When & Then
        // Expecting a domain error (RuntimeException or specific subclass)
        Exception exception = assertThrows(RuntimeException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("timeout") || exception.getMessage().contains("inactive"));
    }

    // Scenario: StartSessionCmd rejected — Navigation state must accurately reflect the current operational context
    @Test
    void testExecuteStartSessionCmd_Rejected_InvalidNavigationState() {
        // Given
        // Violation: This scenario assumes we might be restarting an existing aggregate that is in a bad state.
        // However, since we are initiating a session, the validation might need to be triggered by specific inputs
        // or we test a pre-condition that the aggregate prevents if it's already in an invalid state.
        // For this TDD test, we will assume the command or context implies a transition that is illegal.
        // Since the command only takes primitives, we'll check a null/blank constraint as a proxy for invalid context
        // or we might need to load the aggregate in a specific state if the story allows.
        // Given the prompt "Navigation state must accurately reflect...", we enforce strict state checking.
        
        // Interpretation: TellerID or TerminalID being blank implies invalid navigation/context readiness.
        var aggregate = new TellerSessionAggregate("session-104");
        var cmd = new StartSessionCmd("session-104", "", "term-04", Instant.now()); // Invalid Teller ID

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("state") || exception.getMessage().contains("required"));
    }

    // Edge Case: Unknown Command
    @Test
    void testExecute_UnknownCommand_ThrowsException() {
        var aggregate = new TellerSessionAggregate("session-999");
        var unknownCmd = new com.example.domain.shared.Command() {};

        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}
