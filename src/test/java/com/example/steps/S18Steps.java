package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.setAuthenticated(true);
        aggregate.setTimedOut(false);
        aggregate.setValidContext(true);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Value is hardcoded in the When step for simplicity, or we could store it in context
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Value is hardcoded in the When step for simplicity
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        aggregate.setAuthenticated(false); // Violation
        aggregate.setTimedOut(false);
        aggregate.setValidContext(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true);
        aggregate.setTimedOut(true); // Violation
        aggregate.setValidContext(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-bad-ctx");
        aggregate.setAuthenticated(true);
        aggregate.setTimedOut(false);
        aggregate.setValidContext(false); // Violation
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Assuming valid IDs for positive scenarios, IDs don't matter much for negative invariant checks
            Command cmd = new StartSessionCmd(aggregate.id(), "teller-123", "terminal-456");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        assertEquals("session.started", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
        // Optional: verify specific message content based on the scenario context
    }
}