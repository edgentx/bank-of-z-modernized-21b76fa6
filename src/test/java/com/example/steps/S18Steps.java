package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Placeholder for data table or context setup if needed
        // We will construct the command fully in the 'When' step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Placeholder
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command construction for the happy path
        if (command == null) {
             command = new StartSessionCmd("session-123", "teller-001", "term-42", true);
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Result events should not be null");
        assertFalse(resultEvents.isEmpty(), "Result events should not be empty");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        assertEquals("session.started", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We set up the command to be unauthenticated
        command = new StartSessionCmd("session-auth-fail", "teller-001", "term-42", false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markTimedOut(); // Helper method to simulate invariant violation
        command = new StartSessionCmd("session-timeout", "teller-001", "term-42", true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.invalidateNavigationState(); // Helper method
        command = new StartSessionCmd("session-nav-fail", "teller-001", "term-42", true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
