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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup handled in 'When' step via command construction
    }

    @Given("a valid terminalId is provided")
    public void a valid_terminalId_is_provided() {
        // Context setup handled in 'When' step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd("teller-01", "terminal-01");
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should produce exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We simulate violation by sending a null/blank tellerId in the command
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.forceTimeout(); // Manipulate time to simulate timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.corruptNavigationState(); // Set state to invalid
    }

    @When("the StartSessionCmd command is executed on invalid context")
    public void the_StartSessionCmd_command_is_executed_on_invalid_context() {
        // Use the specific invalid data setup for the scenario, or the same valid command
        // to test the aggregate internal state rejection.
        // Based on the Gherkin, the Aggregate state is violated, not the command.
        StartSessionCmd cmd = new StartSessionCmd("teller-01", "terminal-01");
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it's a domain logic error (IllegalStateException)
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session..")
    public void a_teller_session_aggregate_that_violates_auth() {
        // Duplicate for regex matching safety if needed, or alias
        a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated();
    }
}