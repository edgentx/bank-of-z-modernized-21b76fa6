package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Seed state: Logged in, Active, Valid Context
        aggregate.loadFromHistory(List.of(
            new TellerSessionAuthenticatedEvent(sessionId, "teller-001", Instant.now()),
            new TellerSessionStartedEvent(sessionId, "MAIN_MENU", "VIEW", Instant.now())
        ));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // State: Not authenticated (no history applied or explicitly expired)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        Instant past = Instant.now().minus(Duration.ofMinutes(31)); // Assuming 30 min timeout
        aggregate.loadFromHistory(List.of(
            new TellerSessionAuthenticatedEvent(sessionId, "teller-001", past)
        ));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "session-bad-state";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.loadFromHistory(List.of(
            new TellerSessionAuthenticatedEvent(sessionId, "teller-001", Instant.now()),
            // We set a context that makes the 'action' invalid (e.g. performing 'DEPOSIT' on 'LOGOUT_SCREEN')
            new TellerSessionStartedEvent(sessionId, "LOGOUT_SCREEN", "VIEW", Instant.now())
        ));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by the aggregate construction, but we ensure ID matches
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled via the command construction in the 'When' step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled via the command construction in the 'When' step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Default valid command fields, override if specific scenario context implies otherwise
            String menuId = (aggregate.getCurrentContext() != null && aggregate.getCurrentContext().equals("LOGOUT_SCREEN")) 
                            ? "INVALID_TARGET" // Trigger invariant violation 
                            : "ACCOUNT_SUMMARY";
            String action = (aggregate.getCurrentContext() != null && aggregate.getCurrentContext().equals("LOGOUT_SCREEN"))
                            ? "DEPOSIT" // Invalid action for this screen
                            : "VIEW";

            Command cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull("Expected events to be emitted", resultEvents);
        assertFalse("Expected at least one event", resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", caughtException);
        // Check it's one of our domain exceptions
        assertTrue("Expected IllegalStateException or IllegalArgumentException", 
                   caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException);
    }
}
