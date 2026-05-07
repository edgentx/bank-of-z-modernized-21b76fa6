package com.example.domain.teller;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for TellerSessionAggregate (Story S-19).
 * Written in TDD Red Phase: Implementation is assumed missing or stubbed.
 */
class TellerSessionAggregateTest {

    // --- Scenario: Successfully execute NavigateMenuCmd ---
    @Test
    void testNavigateMenu_Success() {
        // Given a valid TellerSession aggregate
        String sessionId = "session-123";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Setup valid state
        aggregate.setLastActivityAt(Instant.now());
        
        String menuId = "MAIN_MENU";
        String action = "ENTER";

        // When the NavigateMenuCmd command is executed
        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
        var events = aggregate.execute(cmd);

        // Then a menu.navigated event is emitted
        assertNotNull(events);
        assertFalse(events.isEmpty());
        
        assertTrue(events.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
        assertNotNull(event.occurredAt());
    }

    // --- Scenario: NavigateMenuCmd rejected — A teller must be authenticated ---
    @Test
    void testNavigateMenu_Rejected_NotAuthenticated() {
        // Given a TellerSession aggregate that violates: A teller must be authenticated
        String sessionId = "session-unauth";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark authenticated

        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MENU_1", "ACTION");

        // When the NavigateMenuCmd command is executed
        // Then the command is rejected with a domain error
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("authenticated"));
    }

    // --- Scenario: NavigateMenuCmd rejected — Sessions must timeout ---
    @Test
    void testNavigateMenu_Rejected_Timeout() {
        // Given a TellerSession aggregate that violates: Sessions must timeout
        String sessionId = "session-timeout";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        
        // Set last activity to 31 minutes ago (default timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));

        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MENU_1", "ACTION");

        // When the NavigateMenuCmd command is executed
        // Then the command is rejected with a domain error
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("timeout"));
    }

    // --- Scenario: NavigateMenuCmd rejected — Navigation state must accurately reflect current operational context ---
    @Test
    void testNavigateMenu_Rejected_InvalidState() {
        // Given a TellerSession aggregate that violates: Navigation state accuracy (Invalid Inputs)
        String sessionId = "session-bad-input";
        TellerSessionAggregate aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());

        // Test 1: Null MenuId
        NavigateMenuCmd cmdNullMenu = new NavigateMenuCmd(sessionId, null, "ACTION");
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmdNullMenu));

        // Test 2: Blank Action
        NavigateMenuCmd cmdBlankAction = new NavigateMenuCmd(sessionId, "MENU_1", " ");
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmdBlankAction));
    }

    @Test
    void testExecute_UnknownCommand_ThrowsException() {
        TellerSessionAggregate aggregate = new TellerSessionAggregate("id");
        
        // Testing the catch-all default behavior defined in AggregateRoot/Shared pattern
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(new Object() {}); // Anonymous command class
        });
    }
}
