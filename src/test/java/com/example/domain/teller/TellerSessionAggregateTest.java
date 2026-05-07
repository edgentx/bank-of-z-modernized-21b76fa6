package com.example.domain.teller;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for TellerSession Aggregate (Story S-18).
 * Covers StartSessionCmd scenarios.
 */
class TellerSessionAggregateTest {

    private static final String SESSION_ID = "session-123";
    private static final String TELLER_ID = "teller-01";
    private static final String TERMINAL_ID = "term-42";

    @Test
    void shouldStartSessionWhenCommandIsValid() {
        // Given
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, true);

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals("session.started", event.type());
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(TELLER_ID, event.tellerId());
        assertEquals(TERMINAL_ID, event.terminalId());
        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldRejectIfTellerNotAuthenticated() {
        // Given
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, false); // Not Authenticated

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertEquals("Teller must be authenticated to initiate a session.", ex.getMessage());
    }

    @Test
    void shouldRejectIfNavigationStateInvalid() {
        // Given
        // We simulate a state where navigation is not IDLE via a custom test setup or
        // by assuming the aggregate constructor defaults to IDLE, so this specific invariant
        // check acts as a future-proofing guard.
        // Since the constructor forces IDLE, we test the logic by checking that
        // if we were in a different state (theoretically), it would fail.
        // For now, we verify the happy path accepts IDLE.
        
        // However, to strictly test the rejection, we would need to modify state 
        // via reflection or a test-specific method if the constructor didn't default to IDLE.
        // Assuming the aggregate is correctly implemented, if we somehow set state to "BASK_MENU":
        // This test documents the invariant.
        
        // A practical test for this invariant:
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        // If the aggregate has logic that prevents state changes that make nav invalid, 
        // this test ensures that logic exists.
        // Since we can't easily force a bad state in the TDD Red Phase without the code existing,
        // we test the command validation logic.
        
        // Let's rely on the Invariant enforced in the execute method.
        // Since the current impl is 'IDLE', we test the boundary: Null/Blank command params 
        // are handled by IllegalArgumentException, but Navigation State is specific.
        
        // To test the rejection: We cannot easily set the private `currentNavigationState` without reflection.
        // We will use reflection to simulate a 'dirty' state to prove the guard works.
        try {
            var field = TellerSessionAggregate.class.getDeclaredField("currentNavigationState");
            field.setAccessible(true);
            field.set(aggregate, "SOME_OTHER_STATE");

            var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, true);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
                aggregate.execute(cmd);
            });

            assertTrue(ex.getMessage().contains("Navigation state invalid"));
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }

    @Test
    void shouldRejectIfTimedOut() {
        // Given
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        
        // Simulate an old active session
        try {
            var isActiveField = TellerSessionAggregate.class.getDeclaredField("isActive");
            isActiveField.setAccessible(true);
            isActiveField.set(aggregate, true);

            var lastActivityField = TellerSessionAggregate.class.getDeclaredField("lastActivityAt");
            lastActivityField.setAccessible(true);
            // Set time to 20 minutes ago (Timeout is 15)
            lastActivityField.set(aggregate, Instant.now().minusSeconds(20 * 60));

            var cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, true);

            // When & Then
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
                aggregate.execute(cmd);
            });

            assertTrue(ex.getMessage().contains("Session has timed out"));
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }

    @Test
    void shouldThrowUnknownCommandForUnsupportedCmds() {
        // Given
        var aggregate = new TellerSessionAggregate(SESSION_ID);
        Object fakeCmd = new Object();

        // When & Then
        assertThrows(UnknownCommandException.class, () -> aggregate.execute((com.example.domain.shared.Command) fakeCmd));
    }
}