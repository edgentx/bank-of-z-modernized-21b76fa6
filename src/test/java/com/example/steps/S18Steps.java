package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Simulate a fresh aggregate
        aggregate = new TellerSessionAggregate("session-123");
        // Pre-authenticate to satisfy invariants for success case
        // In a real repo, this state might be loaded from history
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context handled in the 'When' step via command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context handled in the 'When' step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // For the success scenario, we assume valid inputs
        // Assuming the aggregate tracks authenticated state via a method or constructor
        // For this exercise, we assume the default aggregate is valid/authenticated
        // unless modified by a 'Given that violates' step.
        executeCmd("teller-1", "terminal-1");
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("session.started", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-violate-auth");
        aggregate.markUnauthenticated(); // Helper to set state for testing
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-violate-timeout");
        aggregate.markTimedOut(); // Helper to set state for testing
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-violate-nav");
        aggregate.invalidateNavigationState(); // Helper to set state for testing
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain error exception");
        // In a real app, might check specific error type/code
    }

    // --- Helpers ---

    private void executeCmd(String tellerId, String terminalId) {
        try {
            Command cmd = new StartSessionCmd("session-id", tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }
}
