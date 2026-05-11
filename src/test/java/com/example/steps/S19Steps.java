package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.mocks.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSession aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to hydrate the aggregate with a valid authenticated state
    // Note: In a real scenario, an InitiateSessionCmd would exist. Here we set state directly for test isolation of S-19.
    private void setupAuthenticatedSession(TellerSession agg) {
        // Using reflection or a package-private helper would be ideal, but for this snippet we assume
        // we can just work with the aggregate. However, state is private.
        // To fix this without changing the Aggregate code excessively for tests, we usually
        // load from a 'fake' event history or use a test-specific factory.
        // For this task, I will assume a test initialization method exists or I will use a Builder pattern if allowed.
        // Since I can't change the shared code much, I'll simulate the 'auth' by relying on the aggregate's behavior
        // OR by assuming the aggregate was loaded from a history.
        
        // WORKAROUND: The previous `execute` logic checks `authenticated`.
        // Since `TellerSession` state is private and immutable except via execute(),
        // and we don't have an `AuthenticateCmd`, we have a testing gap.
        // I will modify the aggregate slightly to allow setting state or assume the 'Given' handles it via a pre-existing event.
        // However, to keep it simple and robust: I will assume the TellerSession has a constructor or method that allows hydration.
        // The provided TellerSession code only has a basic constructor.
        // I will simulate an authenticated session by creating the aggregate and assuming it was hydrated.
        
        // Let's assume the aggregate was rehydrated from an event `SessionAuthenticatedEvent`.
        // I will use a test trick: In the 'Given' I will manually create a state where it is valid.
        // Actually, the cleanest way without adding public setters is to accept that the test needs to hydrate via events.
        // But since I can't generate the `SessionAuthenticatedEvent` in this file, I will assume the 'Valid TellerSession aggregate'
        // implies the internal state is set correctly. 
        
        // Due to the constraint of not modifying the aggregate wildly, I will assume a Test Double or a specific constructor usage.
        // Given the prompt constraints, I will use reflection or a custom logic in the 'Given' to set the private fields if possible,
        // or simply assume the Aggregate was 'executed' with an Authenticate command (which S-19 implies).
        // Since I cannot execute a command that doesn't exist, I will assume the 'Valid TellerSession' implies the state is already set.
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSession("session-123");
        // Hydrating via reflection for test purposes because public setters are bad DDD practice
        // and we don't have an AuthenticateCmd defined yet.
        try {
            var field = TellerSession.class.getDeclaredField("authenticated");
            field.setAccessible(true);
            field.setBoolean(aggregate, true);
            
            var fieldActive = TellerSession.class.getDeclaredField("active");
            fieldActive.setAccessible(true);
            fieldActive.setBoolean(aggregate, true);
            
            var fieldTime = TellerSession.class.getDeclaredField("lastActivityAt");
            fieldTime.setAccessible(true);
            fieldTime.set(aggregate, Instant.now());
            
            var fieldMenu = TellerSession.class.getDeclaredField("currentMenuId");
            fieldMenu.setAccessible(true);
            fieldMenu.set(aggregate, "MAIN_MENU");
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed: could not hydrate aggregate", e);
        }
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in aggregate construction
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in scenario setup or implicit
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Implicit in command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ACCOUNT_DETAILS", "VIEW");
        try {
            resultEvents = aggregate.execute(cmd);
            // Simulate repository save
            repo.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("ACCOUNT_DETAILS", event.currentMenuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("session-invalid-auth");
        // Default state is authenticated=false, so this is easy.
        // But we must ensure it doesn't timeout first.
        try {
            var fieldTime = TellerSession.class.getDeclaredField("lastActivityAt");
            fieldTime.setAccessible(true);
            fieldTime.set(aggregate, Instant.now());
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_is_timed_out() {
        aggregate = new TellerSession("session-timeout");
        try {
            var fieldAuth = TellerSession.class.getDeclaredField("authenticated");
            fieldAuth.setAccessible(true);
            fieldAuth.setBoolean(aggregate, true);
            
            var fieldTime = TellerSession.class.getDeclaredField("lastActivityAt");
            fieldTime.setAccessible(true);
            // Set time to 20 minutes ago
            fieldTime.set(aggregate, Instant.now().minusSeconds(20 * 60));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_with_stale_context() {
        aggregate = new TellerSession("session-stale");
        try {
            var fieldAuth = TellerSession.class.getDeclaredField("authenticated");
            fieldAuth.setAccessible(true);
            fieldAuth.setBoolean(aggregate, true);
            
            var fieldMenu = TellerSession.class.getDeclaredField("currentMenuId");
            fieldMenu.setAccessible(true);
            // Aggregate thinks we are at MAIN_MENU
            fieldMenu.set(aggregate, "MAIN_MENU");
            
            var fieldTime = TellerSession.class.getDeclaredField("lastActivityAt");
            fieldTime.setAccessible(true);
            fieldTime.set(aggregate, Instant.now());
        } catch (Exception e) { throw new RuntimeException(e); }
        // In the When step, we will send a command claiming we are at 'WRONG_MENU'
    }

    // Override the When for the specific violation case if needed, or reuse generic
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_NavigateMenuCmd_command_is_executed_with_invalid_context() {
        // Current is MAIN_MENU, but we tell the system we are at WRONG_MENU
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-stale", "WRONG_MENU", "ACCOUNT_DETAILS", "VIEW");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on the violation, it could be IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        assertNull(resultEvents);
    }
}
