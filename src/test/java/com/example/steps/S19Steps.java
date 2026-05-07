package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.Assert.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Hydrate aggregate to a valid state (Authenticated + Active)
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionAuthenticatedEvent("session-123", "teller-1", java.time.Instant.now()));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in the aggregate initialization
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in command execution
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command execution
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Deliberately not firing SessionAuthenticatedEvent, leaving isAuthenticated false
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionAuthenticatedEvent("session-408", "teller-1", java.time.Instant.now().minusSeconds(3600))); // 1 hour ago
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionTimedOutEvent("session-408", java.time.Instant.now().minusSeconds(1800)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-400");
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionAuthenticatedEvent("session-400", "teller-1", java.time.Instant.now()));
        // Simulate a state where the teller is trying to access a menu that requires a specific context they don't have
        // For simplicity in this domain model, we'll model this as the aggregate being locked or in an invalid state.
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionLockedEvent("session-400", " CONTEXT_ERROR", java.time.Instant.now()));
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "OPEN_ACCOUNT");
        // Adjust ID based on the setup scenario logic if needed, but reusing ID for simplicity in valid case
        if (aggregate.id().equals("session-401")) cmd = new NavigateMenuCmd("session-401", "MAIN_MENU", "OPEN_ACCOUNT");
        if (aggregate.id().equals("session-408")) cmd = new NavigateMenuCmd("session-408", "MAIN_MENU", "OPEN_ACCOUNT");
        if (aggregate.id().equals("session-400")) cmd = new NavigateMenuCmd("session-400", "FORBIDDEN_MENU", "OPEN_ACCOUNT");

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("teller.menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
