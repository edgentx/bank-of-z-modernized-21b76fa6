package com.example.steps;

import com.example.domain.shared.Command;
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

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Throwable caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // A new aggregate is valid by default for starting a session
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Tellers IDs are non-null strings
        // Handled in combination with terminalId below
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in combination with tellerId below
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Placeholder for Gherkin parsing
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Placeholder for Gherkin parsing
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Construct a valid command by default
        if (cmd == null) {
            cmd = new StartSessionCmd("session-1", "teller-123", "terminal-A");
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    // --- Invariants Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        // We create a command that represents a non-authenticated or invalid teller state
        // In this domain, we simulate this by passing null/invalid IDs which the aggregate rejects
        cmd = new StartSessionCmd("session-bad-auth", "", "terminal-A");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate a session that is already in a 'timed-out' or invalid state context
        // For a new aggregate, this would be modeled by command validation rejecting timeout status
        // Since we are initiating a new session, we model this check via command metadata or state simulation
        // Here we simulate a bad request context
        cmd = new StartSessionCmd("session-timeout", "teller-123", "terminal-A", false, true); // simulated timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Simulate a context mismatch
        cmd = new StartSessionCmd("session-nav-error", "teller-123", "terminal-A", false, false, true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception, but execution succeeded.");
        // Check that it's an explicit domain error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(IllegalArgumentException.class.isInstance(caughtException) || IllegalStateException.class.isInstance(caughtException));
    }

}
