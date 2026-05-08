package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario: Successfully execute StartSessionCmd

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Ensure clean state (default constructor sets status NONE)
        // No additional setup needed for validity other than constructor.
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Data setup happens in the 'When' step via the command object.
        // This step is essentially documentation that the command uses a valid ID.
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Data setup happens in the 'When' step.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        executeCommand(true, "teller-1", "term-1"); // Default valid inputs
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated...

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // The violation is in the Command, not the aggregate state for this specific rule.
    }

    @When("the StartSessionCmd command is executed for unauthenticated user")
    public void the_start_session_cmd_command_is_executed_unauthenticated() {
        // isAuthenticated = false triggers the violation
        executeCommand(false, "teller-1", "term-1");
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout...

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate old activity (e.g., 31 minutes ago)
        aggregate.setLastActivityAt(Instant.now().minus(31, ChronoUnit.MINUTES));
    }

    // Scenario: StartSessionCmd rejected — Navigation state must accurately reflect...

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.setOperationalContext("INVALID_CTX");
    }

    // Common Verification Steps

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In DDD, command rejection is often an IllegalStateException or specific DomainException
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // Helper to execute command and capture results/errors
    private void executeCommand(boolean isAuthenticated, String tellerId, String terminalId) {
        capturedException = null;
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                tellerId,
                terminalId,
                isAuthenticated
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
