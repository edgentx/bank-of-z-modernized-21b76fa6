package com.example.domain.teller;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.SessionAuthenticatedEvent;
import com.example.domain.teller.model.SessionInitiatedEvent;
import com.example.domain.teller.model.TellerSession;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * RED PHASE TESTS
 * These tests describe the expected behavior of the TellerSession aggregate.
 * They will fail because the implementation is either missing or incomplete.
 */
public class TellerSessionTest {

    // Helper to create a valid, active session
    private TellerSession createActiveSession() {
        String sessionId = "session-123";
        String tellerId = "teller-01";
        TellerSession session = new TellerSession(sessionId);

        // Simulate past events to bring session to life (Event Sourcing hydration simulation)
        // In a real repo, these would be loaded from the event store.
        // Here we manually invoke the behavior logic or directly set state for testing if no rehydration method exists.
        // However, to test the 'execute' command properly, we need the session in a valid state.
        // Assuming the aggregate starts empty and we hydrate it, or we start a session first.

        // Let's assume a "Initiate" then "Authenticate" flow is required for an active session.
        // But since we are testing Navigate, we will construct a valid internal state manually
        // to isolate the Navigate logic, or use a setup command if available.
        // For TDD Red phase, we often just call the command and expect the implementation to handle state.

        // We will simulate a pre-authenticated session by calling internal state setters if accessible,
        // or by verifying that execute throws "Not Authenticated" if we don't.
        // To test SUCCESS, we MUST have an authenticated session.

        // Creating a "mock" authenticated state via reflection or package-private helpers is risky.
        // We will assume the TellerSession has a constructor or method to establish a baseline state.
        // Since the prompt says "Implement... on TellerSession", and we don't have other commands defined
        // in the prompt, we have to make assumptions or use a backdoor.
        // BETTER: We assume TellerSession has a mechanism to be loaded.
        // For this test file, we will instantiate and try to set state.
        // *Actually*, standard TDD: We write the test that expects the state to be valid.
        // If the aggregate can't be constructed in a valid state, the test fails.

        // To make the tests compile and run, we will assume the existence of a method
        // `hydrate(List<DomainEvent>)` or similar, OR we will rely on the implementation
        // providing a constructor for testing. 
        // WAIT: The prompt says "Implement NavigateMenuCmd". It implies other logic might exist.
        // Let's look at the acceptance criteria: "A teller must be authenticated".
        
        // Implementation Trick: We will use a default constructor or factory to create a 'ready' session
        // for the success test, and verify the 'unauthenticated' test with a fresh/empty one.
        return session;
    }

    private TellerSession createAuthenticatedSession() {
        String sessionId = "session-123";
        TellerSession session = new TellerSession(sessionId);
        
        // To make the test pass, the implementation needs to know who the teller is.
        // We will inject an event that represents authentication.
        SessionAuthenticatedEvent authEvent = new SessionAuthenticatedEvent(
            sessionId, 
            "teller-01", 
            "TERMINAL_01", 
            Instant.now()
        );
        
        // We need a way to apply this event to the aggregate.
        // Typically `apply(event)`. We will assume this method exists or use reflection to set `authenticated = true`.
        // For the purpose of generating the code, we will assume the implementation will support 
        // a way to be in this state. 
        // Since we are writing the test first (Red), we can define the API we wish existed.
        // But we can't change the Aggregate interface provided.
        // We will assume the TellerSession class has a package-private or public method to apply history.
        // Actually, usually aggregates have a `load(history)` method or constructor.
        // Let's try to call `execute` with a fake command? No, that generates events.
        
        // We will assume the TellerSession class allows setting internal state for testing 
        // or we will test the negative paths (unauthenticated) first.
        
        // Let's assume the class `TellerSession` will expose a static factory or constructor for testing.
        session.markAsAuthenticated(); // We will assume this method exists in the implementation to make the test pass.
        session.markAsActive();         // Assume this sets lastActivity to now.
        return session;
    }

    @Test
    void should_emit_MenuNavigatedEvent_when_command_is_valid() {
        // Arrange
        TellerSession session = createAuthenticatedSession();
        String targetMenuId = "ACCOUNT_SUMMARY";
        String action = "DISPLAY";
        NavigateMenuCmd cmd = new NavigateMenuCmd(session.id(), targetMenuId, action);

        // Act
        var events = session.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(targetMenuId, event.menuId());
        assertEquals(action, event.action());
        assertEquals(session.id(), event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Test
    void should_reject_command_when_teller_not_authenticated() {
        // Arrange - A fresh session is not authenticated
        TellerSession session = new TellerSession("session-unknown");
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-unknown", "SOME_MENU", "VIEW");

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("authenticated"));
    }

    @Test
    void should_reject_command_when_session_is_timed_out() {
        // Arrange
        TellerSession session = new TellerSession("session-timeout");
        session.markAsAuthenticated();
        // Simulate timeout: Set last activity to 31 minutes ago (assuming 30 min timeout)
        session.forceLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
        
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "ENTER");

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            session.execute(cmd);
        });
        
        assertTrue(exception.getMessage().contains("timeout") || exception.getMessage().contains("inactive"));
    }
    
    @Test
    void should_reject_command_when_navigation_context_is_invalid() {
        // Arrange
        TellerSession session = createAuthenticatedSession();
        // Context error: e.g., trying to navigate to a screen that requires a Customer ID selected, 
        // but none is selected. Or navigating from a state that doesn't allow the action.
        // The scenario says "Navigation state must accurately reflect...".
        // We will model this by passing an action that conflicts with current state.
        // e.g., action "POST_TRANSACTION" but no transaction context.
        
        // For the test to fail properly, the implementation needs to check specific invariants.
        // Let's assume the command requires a 'context' (missing in command record, so maybe implicit in aggregate)
        // Or maybe the MenuId is invalid for the current User Role.
        // Let's test: Invalid Menu ID.
        
        NavigateMenuCmd cmd = new NavigateMenuCmd(session.id(), "RESTRICTED_ADMIN_MENU", "ENTER");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            session.execute(cmd);
        });
        
        assertTrue(exception.getMessage().contains("context") || exception.getMessage().contains("allowed"));
    }

    @Test
    void should_throw_UnknownCommandException_for_unsupported_commands() {
        TellerSession session = new TellerSession("session-unknown");
        Object badCmd = new Object(); // Not a valid command
        
        // Note: The signature is execute(Command cmd). We can't pass Object.
        // We need a dummy command.
        Command dummyCmd = () -> "DummyCommand"; // Functional interface
        
        assertThrows(UnknownCommandException.class, () -> session.execute(dummyCmd));
    }
}
