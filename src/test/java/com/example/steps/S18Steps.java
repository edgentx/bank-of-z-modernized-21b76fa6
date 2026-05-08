package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup managed in the 'When' step for simplicity or stored in context
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup managed in the 'When' step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command
        command = new StartSessionCmd("session-123", "teller-1", "terminal-1", true, false, true);
        executeCommand();
    }

    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    // --- Scenario 2 ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_start_session_cmd_command_is_executed_with_invalid_auth() {
        command = new StartSessionCmd("session-auth-fail", "teller-1", "terminal-1", false, false, true);
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("authenticated"));
    }

    // --- Scenario 3 ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
    }

    @When("the StartSessionCmd command is executed with timeout")
    public void the_start_session_cmd_command_is_executed_with_timeout() {
        command = new StartSessionCmd("session-timeout", "teller-1", "terminal-1", true, true, true);
        executeCommand();
    }

    // --- Scenario 4 ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed with invalid navigation")
    public void the_start_session_cmd_command_is_executed_with_invalid_navigation() {
        command = new StartSessionCmd("session-nav-fail", "teller-1", "terminal-1", true, false, false);
        executeCommand();
    }
}