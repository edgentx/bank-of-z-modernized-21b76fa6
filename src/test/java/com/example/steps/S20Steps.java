package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private DomainEvent resultingEvent;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Initialize a valid active session
        aggregate = new TellerSessionAggregate("session-123");
        // Seed state as if it was started and authenticated successfully
        aggregate.markAuthenticated();
        aggregate.markActive();
        aggregate.updateLastActivity(); 
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is implicit in the aggregate ID
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Do NOT authenticate. Session remains in unauthenticated state.
        aggregate.markActive(); 
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markAuthenticated();
        aggregate.markActive();
        // Simulate timeout by explicitly expiring the session
        aggregate.expire();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated();
        aggregate.markActive();
        // Simulate a state where the terminal is in a menu not allowing logout (e.g. in-transaction)
        aggregate.enterRestrictedState();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultingEvent, "Expected an event to be emitted");
        assertTrue(resultingEvent instanceof SessionEndedEvent, "Expected SessionEndedEvent");
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain exception to be thrown");
        // Check for common domain exception types or message content
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException,
                   "Expected an IllegalStateException or IllegalArgumentException");
    }
}
