package com.example.domain.teller;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import com.example.domain.teller.model.SessionStartedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for TellerSession (S-18).
 * These tests enforce the acceptance criteria:
 * 1. Successful execution emits SessionStartedEvent.
 * 2. Authenticated Teller invariant.
 * 3. Timeout invariant.
 * 4. Navigation State invariant.
 */
public class TellerSessionTest {

    // ========================= SCENARIO 1: Success =========================
    @Test
    void whenStartSessionCmdIsValid_thenEmitSessionStartedEvent() {
        // Given
        String sessionId = "TS-123";
        String tellerId = "T-01";
        String terminalId = "TM-05";
        TellerSession session = new TellerSession(sessionId);
        StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId, true); // isAuthenticated = true

        // When
        List<DomainEvent> events = session.execute(cmd);

        // Then
        assertEquals(1, events.size(), "Should emit exactly one event");
        assertTrue(events.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    // ========================= SCENARIO 2: Auth Rejection =========================
    @Test
    void whenTellerNotAuthenticated_thenThrowDomainError() {
        // Given
        String sessionId = "TS-404";
        TellerSession session = new TellerSession(sessionId);
        // isAuthenticated = false violates invariant
        StartSessionCmd cmd = new StartSessionCmd("T-01", "TM-05", false);

        // When & Then
        // We expect a specific exception type or domain error, but for now we check for an Exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("authenticated"));
    }

    // ========================= SCENARIO 3: Timeout Rejection =========================
    @Test
    void whenSessionIsTimedOut_thenThrowDomainError() {
        // Given
        // For simplicity, we pass a 'timedOut' flag or simulate it via state if the aggregate supported history loading.
        // Assuming the StartSessionCmd carries context or the aggregate state defaults to timed out for this test.
        // Here we assume the command has a flag to simulate this violation scenario.
        TellerSession session = new TellerSession("TS-ERR");
        // Violation: Session must timeout after configured period of inactivity.
        // We interpret this as checking if the session is ALREADY in a timed-out state or invalid state.
        StartSessionCmd cmd = new StartSessionCmd("T-01", "TM-05", true, true); // isTimedOut = true

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("timeout"));
    }

    // ========================= SCENARIO 4: Navigation State Rejection =========================
    @Test
    void whenNavigationStateIsInvalid_thenThrowDomainError() {
        // Given
        TellerSession session = new TellerSession("TS-NAV");
        // Violation: Navigation state must accurately reflect current operational context.
        StartSessionCmd cmd = new StartSessionCmd("T-01", "TM-05", true, false, false); // isNavValid = false

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("navigation"));
    }

    @Test
    void whenUnknownCommand_thenThrowUnknownCommandException() {
        // Given
        TellerSession session = new TellerSession("TS-UNK");
        Command unknownCmd = new Command() {}; // Anonymous invalid command

        // When & Then
        assertThrows(UnknownCommandException.class, () -> {
            session.execute(unknownCmd);
        });
    }
}
