package com.example.domain.teller;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test Suite for S-19: NavigateMenuCmd.
 * Tests are written to fail initially against an empty/stubbed implementation.
 */
class TellerSessionAggregateTest {

    /*
     * Scenario: Successfully execute NavigateMenuCmd
     * Given a valid TellerSession aggregate
     * And a valid sessionId is provided
     * And a valid menuId is provided
     * And a valid action is provided
     * When the NavigateMenuCmd command is executed
     * Then a menu.navigated event is emitted
     */
    @Test
    void testSuccessfulNavigation() {
        // Setup a valid, authenticated session
        TellerSessionAggregate session = new TellerSessionAggregate("session-123");
        session.markAuthenticated(); // Pre-condition

        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");

        // Execute
        List<com.example.domain.shared.DomainEvent> events = session.execute(cmd);

        // Assertions
        assertFalse(events.isEmpty(), "An event should be emitted");
        assertEquals(1, events.size(), "Exactly one event should be emitted");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("MAIN_MENU", event.targetMenuId());
        assertNotNull(event.occurredAt());
    }

    /*
     * Scenario: NavigateMenuCmd rejected — A teller must be authenticated to initiate a session.
     * Given a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.
     * When the NavigateMenuCmd command is executed
     * Then the command is rejected with a domain error
     */
    @Test
    void testRejectIfNotAuthenticated() {
        // Setup an unauthenticated session
        TellerSessionAggregate session = new TellerSessionAggregate("session-999");
        session.markUnauthenticated(); // Violating condition

        NavigateMenuCmd cmd = new NavigateMenuCmd("session-999", "ADMIN_MENU", "F3");

        // Execute & Assert Exception
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("authenticated"));
    }

    /*
     * Scenario: NavigateMenuCmd rejected — Sessions must timeout after a configured period of inactivity.
     * Given a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.
     * When the NavigateMenuCmd command is executed
     * Then the command is rejected with a domain error
     */
    @Test
    void testRejectIfTimedOut() {
        // Setup an authenticated but timed out session
        TellerSessionAggregate session = new TellerSessionAggregate("session-timeout");
        session.markAuthenticated();
        
        // Force the last activity time to be well beyond the threshold (e.g., 2 hours ago)
        Instant oldTime = Instant.now().minus(Duration.ofHours(2));
        session.setLastActivity(oldTime);

        NavigateMenuCmd cmd = new NavigateMenuCmd("session-timeout", "ANY_MENU", "ENTER");

        // Execute & Assert Exception
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("timeout") || ex.getMessage().contains("inactivity"));
    }

    /*
     * Scenario: NavigateMenuCmd rejected — Navigation state must accurately reflect the current operational context.
     * Given a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.
     * When the NavigateMenuCmd command is executed
     * Then the command is rejected with a domain error
     */
    @Test
    void testRejectInvalidNavigationState() {
        // Setup valid session, but provide invalid command context
        TellerSessionAggregate session = new TellerSessionAggregate("session-context");
        session.markAuthenticated();

        // Blank menuId is invalid context
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-context", "", "ENTER");

        // Execute & Assert Exception
        Exception ex = assertThrows(Exception.class, () -> {
            session.execute(cmd);
        });

        // Expecting IllegalArgumentException or IllegalStateException based on implementation
        assertTrue(ex.getMessage().contains("context") || ex.getMessage().contains("required"));
    }

    @Test
    void testUnknownCommandThrowsException() {
        TellerSessionAggregate session = new TellerSessionAggregate("session-unknown");
        session.markAuthenticated();

        Command unknownCmd = new Command() {}; // Anonymous invalid command

        assertThrows(UnknownCommandException.class, () -> {
            session.execute(unknownCmd);
        });
    }
}