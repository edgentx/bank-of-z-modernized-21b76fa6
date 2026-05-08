package com.example.domain.tellersession;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.InitiateTellerSessionCmd;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite for S-19: TellerSession Navigation.
 * Covers: Authentication, Timeout, and Navigation State invariants.
 */
class TellerSessionAggregateTest {

    @Test
    void shouldSuccessfullyNavigateWhenValid() {
        // Given
        var aggregate = new TellerSessionAggregate("session-1");
        // Authenticate and activate
        aggregate.execute(new InitiateTellerSessionCmd("session-1"));
        aggregate.clearEvents(); // Clear initiation events

        var cmd = new NavigateMenuCmd("session-1", "WITHDRAWAL_MENU", "ENTER");

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof MenuNavigatedEvent);
        var evt = (MenuNavigatedEvent) events.get(0);
        assertEquals("session-1", evt.aggregateId());
        assertEquals("WITHDRAWAL_MENU", evt.targetMenuId());
        assertEquals("ENTER", evt.action());
        assertEquals("WITHDRAWAL_MENU", aggregate.getCurrentMenuId());
    }

    @Test
    void rejectNavigationIfNotAuthenticated() {
        // Given: A session that was created but never authenticated (Initiate not called)
        var aggregate = new TellerSessionAggregate("session-unauth");
        var cmd = new NavigateMenuCmd("session-unauth", "MAIN_MENU", "F3");

        // When & Then
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("authenticated"));
        assertTrue(aggregate.uncommittedEvents().isEmpty());
    }

    @Test
    void rejectNavigationIfSessionTimedOut() {
        // Given: An authenticated session that has timed out
        var aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.execute(new InitiateTellerSessionCmd("session-timeout"));
        aggregate.clearEvents();
        
        // Force timeout using test seam
        aggregate.forceTimeout();

        var cmd = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "ENTER");

        // When & Then
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("timed out"));
    }

    @Test
    void rejectNavigationIfStateIsInvalid() {
        // Given: An authenticated session, but navigating to an invalid target
        var aggregate = new TellerSessionAggregate("session-state");
        aggregate.execute(new InitiateTellerSessionCmd("session-state"));
        aggregate.clearEvents();

        var cmd = new NavigateMenuCmd("session-state", "", "ENTER"); // Blank Menu ID

        // When & Then
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Target menu ID"));
    }
}