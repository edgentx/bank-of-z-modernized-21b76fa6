package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private Exception caughtException;
    private java.util.List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("sess-123");
        aggregate.markActive(); // Helper to set valid base state for successful path
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Assuming session ID matches the aggregate ID
        this.command = new EndSessionCmd("sess-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("sess-401");
        aggregate.markActive();
        aggregate.markUnauthenticated(); // Violation
        this.command = new EndSessionCmd("sess-401");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("sess-408");
        aggregate.markActive();
        aggregate.markTimedOut(); // Violation
        this.command = new EndSessionCmd("sess-408");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("sess-nav-err");
        aggregate.markActive();
        aggregate.markInvalidNavigationState(); // Violation
        this.command = new EndSessionCmd("sess-nav-err");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        // We verify it's a logic exception (Domain Logic enforcement)
        assertTrue(caughtException instanceof IllegalStateException, "Should be an IllegalStateException domain violation");
    }
}
