package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String sessionId = "session-123";
    private String tellerId = "teller-01";
    private String terminalId = "terminal-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // In a real scenario, the session might be initialized to a state
        // representing the pre-authentication phase. Here we assume
        // a fresh aggregate is ready to accept a start command if configured correctly.
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // tellerId is hardcoded in setup for simplicity, derived from steps
        assertNotNull(tellerId);
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // terminalId is hardcoded in setup
        assertNotNull(terminalId);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, Instant.now().plus(Duration.ofHours(8)));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Assume the aggregate needs a flag or state set to allow starting.
        // If we don't set it, the command should fail.
        // Or, per the AC, if the command carries no auth token (or an invalid one), we fail.
        // We will use a constructor flag to simulate an "authenticated" state requirement
        // and instantiate it as NOT authenticated.
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        // We provide a timeout that is effectively in the past
        // However, the AC implies the COMMAND is the trigger.
        // Let's interpret this as: The command contains a timeout that is invalid (e.g., too long or in past).
        // We'll set the command up in the 'When' to use an invalid timeout,
        // but we can set state here if needed.
        // Actually, the Gherkin says "Given a TellerSession aggregate that violates...".
        // Perhaps the Aggregate has a state that prevents starting? 
        // Let's assume the command handles the timeout validation logic.
        // But to stick to the "Given Aggregate" pattern:
        aggregate.setValidTimeoutConfiguration(false);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setValidTimeoutConfiguration(true);
        // Set the navigation state to invalid
        aggregate.setNavigationState("INVALID_STATE");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for standard domain exception types or messages
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException,
                   "Exception should be a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
