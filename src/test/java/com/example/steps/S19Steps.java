package com.example.steps;

import com.example.domain.shared.*;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Simulate an active, authenticated session state
        aggregate.applyHistory(new TellerSessionAuthenticatedEvent("session-123", "teller-1", Instant.now()));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by the aggregate initialization
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled implicitly by the command initialization in @When
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled implicitly by the command initialization in @When
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-unauth");
        // Do NOT emit an AuthenticatedEvent. The aggregate defaults to unauthenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        Instant past = Instant.now().minus(Duration.ofMinutes(31));
        aggregate.applyHistory(new TellerSessionAuthenticatedEvent("session-timeout", "teller-1", past));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.applyHistory(new TellerSessionAuthenticatedEvent("session-bad-state", "teller-1", Instant.now()));
        // Flag the aggregate as having an invalid navigation context (e.g. blocked screen)
        aggregate.markNavigationStateInvalid();
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MENU_MAIN", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
