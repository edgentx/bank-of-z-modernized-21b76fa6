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
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 * Tests domain logic in isolation using in-memory aggregates.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // We defer command creation to the 'When' to allow customization in other scenarios
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Defaults for happy path
            if (cmd == null) {
                cmd = new StartSessionCmd("session-123", "teller-01", "term-01");
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We use a specific convention in the aggregate to trigger this failure
        cmd = new StartSessionCmd("session-auth-fail", "BLOCKED", "term-01");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Using specific trigger ID for timeout simulation
        cmd = new StartSessionCmd("session-timeout-fail", "STALE_USER", "term-01");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Using specific trigger ID for nav state simulation
        cmd = new StartSessionCmd("session-nav-fail", "teller-01", "BROKEN_TERMINAL");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be a domain error (IllegalStateException)");
        assertFalse(capturedException.getMessage().isBlank(), "Exception should have a message");
    }

    @Given("a TellerSession aggregate")
    public void a_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-generic");
    }
}