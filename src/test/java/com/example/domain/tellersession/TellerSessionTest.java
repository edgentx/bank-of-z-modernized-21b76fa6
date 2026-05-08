package com.example.domain.tellersession;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellsession.model.StartSessionCmd;
import com.example.domain.tellsession.model.TellerSession;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test Suite for S-18: StartSessionCmd.
 * Red Phase: Verifying behavior and invariants.
 */
class TellerSessionTest {

    static final String SESSION_ID = "sess-123";
    static final String TELLER_ID = "teller-alice";
    static final String TERMINAL_ID = "term-01";
    static final String CHANNEL_ID = "WEB";
    static final String CONTEXT = "MAIN_MENU";

    @Test
    void givenValidCommand_whenExecute_thenEmitsSessionStartedEvent() {
        // Given
        var aggregate = new TellerSession(SESSION_ID);
        var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, CHANNEL_ID, CONTEXT, Duration.ofMinutes(30));

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        var event = events.get(0);
        assertEquals("SessionStartedEvent", event.type());
        assertEquals(SESSION_ID, event.aggregateId());
        assertTrue(aggregate.isStarted());
        assertEquals(CONTEXT, aggregate.getCurrentContext());
    }

    @Test
    void givenMissingTellerId_whenExecute_thenThrowsIllegalStateException() {
        // Given: Violates "A teller must be authenticated"
        var aggregate = new TellerSession(SESSION_ID);
        var cmd = new StartSessionCmd(SESSION_ID, null, TERMINAL_ID, CHANNEL_ID, CONTEXT, Duration.ofMinutes(30));

        // When & Then
        assertThrows(IllegalStateException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void givenInvalidTimeout_whenExecute_thenThrowsIllegalArgumentException() {
        // Given: Violates "Sessions must timeout after a configured period"
        var aggregate = new TellerSession(SESSION_ID);
        var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, CHANNEL_ID, CONTEXT, Duration.ZERO); // Invalid timeout

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void givenMissingContext_whenExecute_thenThrowsIllegalArgumentException() {
        // Given: Violates "Navigation state must accurately reflect current operational context"
        var aggregate = new TellerSession(SESSION_ID);
        var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, CHANNEL_ID, "", Duration.ofMinutes(30)); // Blank context

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }

    @Test
    void givenUnknownCommand_whenExecute_thenThrowsUnknownCommandException() {
        // Given
        var aggregate = new TellerSession(SESSION_ID);
        var unknownCmd = new com.example.domain.shared.Command() {}; // Anonymous mock command

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}
