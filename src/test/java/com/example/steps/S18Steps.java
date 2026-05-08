package com.example.steps;

import com.example.domain.tellermaintenance.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        // Ensure state is valid for the successful scenario
        aggregate.markAuthenticated(); 
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-fail-auth");
        // Default state is not authenticated, violates invariant.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-fail-timeout");
        aggregate.markAuthenticated();
        aggregate.simulateSessionTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-fail-nav");
        aggregate.markAuthenticated();
        aggregate.markNavigationStateInvalid();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup, implicit in command execution
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup, implicit in command execution
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Using valid test data constants
            Command cmd = new StartSessionCmd("session-1", "teller-123", "term-456");
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have emitted events");
        assertTrue(events.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException, 
            "Exception should be a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
