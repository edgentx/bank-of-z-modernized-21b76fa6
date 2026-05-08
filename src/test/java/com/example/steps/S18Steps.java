package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * BDD Step Definitions for S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    // Test Context
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Constants
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_42";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Data prepared in the 'When' step via the command object
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Data prepared in the 'When' step via the command object
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command construction
        StartSessionCmd cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID, true);
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent sse = (SessionStartedEvent) event;
        Assertions.assertEquals("SESSION_123", sse.aggregateId());
        Assertions.assertEquals(VALID_TELLER_ID, sse.tellerId());
        Assertions.assertEquals(VALID_TERMINAL_ID, sse.terminalId());
        Assertions.assertEquals("session.started", sse.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION_AUTH_FAIL");
    }

    // Override When for this scenario to inject invalid auth state
    @When("the StartSessionCmd command is executed with unauthenticated user")
    public void the_start_session_cmd_command_is_executed_unauthenticated() {
        StartSessionCmd cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID, false);
        executeCommand(cmd);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_TIMEOUT");
        // Force the internal state to look like it happened 20 minutes ago
        Instant past = Instant.now().minus(Duration.ofMinutes(20));
        aggregate.forceLastActivityTo(past);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION_NAV_ERR");
        aggregate.markNavigationStateInvalid();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Specific invariant checks
        if (capturedException instanceof IllegalStateException ise) {
            // Validate message content if necessary, e.g. "Authentication required"
            Assertions.assertTrue(ise.getMessage() != null && !ise.getMessage().isEmpty());
        } else {
            Assertions.fail("Expected IllegalStateException, but got: " + capturedException.getClass().getSimpleName());
        }
    }

    // Helper to execute and capture errors
    private void executeCommand(StartSessionCmd cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
