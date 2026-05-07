package com.example.domain.teller;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TellerSessionAggregateTest {

    // Scenario: Successfully execute NavigateMenuCmd
    @Test
    void shouldExecuteNavigateMenuCommandSuccessfully() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01"); // Setup authenticated state
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
        assertNotNull(event.occurredAt());
    }

    // Scenario: NavigateMenuCmd rejected — A teller must be authenticated to initiate a session.
    @Test
    void shouldRejectNavigationWhenNotAuthenticated() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate("session-123");
        // aggregate.markAuthenticated(...) is NOT called
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");

        // When & Then
        IllegalStateException exception = assertThrows(IllegallegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertEquals("A teller must be authenticated to initiate a session.", exception.getMessage());
    }

    // Scenario: NavigateMenuCmd rejected — Sessions must timeout after a configured period of inactivity.
    @Test
    void shouldRejectNavigationWhenSessionTimedOut() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01");
        aggregate.expireSession(); // Simulate time passing
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");

        // When & Then
        IllegalStateException exception = assertThrows(IllegallegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertEquals("Sessions must timeout after a configured period of inactivity.", exception.getMessage());
    }

    // Scenario: NavigateMenuCmd rejected — Navigation state must accurately reflect the current operational context.
    @Test
    void shouldRejectNavigationWhenOperationalContextMismatch() {
        // Given
        TellerSessionAggregate aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01");
        aggregate.setOperationalContext("LOCKDOWN"); // Simulate a locked state
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER"); // ENTER is not allowed in LOCKDOWN

        // When & Then
        IllegalStateException exception = assertThrows(IllegallegalStateException.class, () -> {
            aggregate.execute(cmd);
        });
        assertEquals("Navigation state must accurately reflect the current operational context.", exception.getMessage());
    }
}
