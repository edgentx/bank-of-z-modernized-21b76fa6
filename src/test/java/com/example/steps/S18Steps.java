package com.example.steps;

import com.example.domain.shared.Command;
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
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in When clause construction, or stored in context
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in When clause construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Defaults for success scenario
        command = new StartSessionCmd(
            "session-123",
            "teller-42",
            "terminal-07",
            true, // authenticated
            "HOME"
        );
        executeCommand();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-123");
        // The violation is in the command for this rule, but we prep the aggregate
    }

    @When("the StartSessionCmd command is executed with authentication failure")
    public void the_command_is_executed_with_auth_failure() {
        command = new StartSessionCmd(
            "session-123",
            "teller-42",
            "terminal-07",
            false, // NOT authenticated
            "HOME"
        );
        executeCommand();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        // Force the aggregate into a state where it appears timed out
        aggregate.markAsTimedOut();
    }

    @When("the StartSessionCmd command is executed on timed out session")
    public void the_command_is_executed_on_timed_out_session() {
        command = new StartSessionCmd(
            "session-123",
            "teller-42",
            "terminal-07",
            true,
            "HOME"
        );
        executeCommand();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_command_is_executed_with_invalid_navigation_state() {
        command = new StartSessionCmd(
            "session-123",
            "teller-42",
            "terminal-07",
            true,
            "" // Invalid navigation state
        );
        executeCommand();
    }

    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(command);
            if (resultEvents != null && !resultEvents.isEmpty()) {
                aggregate.clearEvents(); // clear uncommitted after fetch
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-42", event.tellerId());
        assertEquals("terminal-07", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    @Then("the error message contains {string}")
    public void the_error_message_contains(String messageFragment) {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains(messageFragment), 
            "Error message should contain '" + messageFragment + "'. Got: " + capturedException.getMessage());
    }
}