package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Step Definitions for S-18: StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the 'When' step construction for simplicity, or stored here
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command construction for the positive path
        if (command == null) {
            command = new StartSessionCmd("session-123", "TELLER-01", "TERM-01", true);
        }
        executeCommand();
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("TELLER-01", event.tellerId());
        assertEquals("TERM-01", event.terminalId());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // The violation is provided in the command (isAuthenticated=false)
        command = new StartSessionCmd("session-auth-fail", "TELLER-01", "TERM-01", false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify specific error messages based on the scenario context
        String msg = capturedException.getMessage();
        assertTrue(msg.contains("authenticated") || msg.contains("timeout") || msg.contains("context"), 
            "Error message should relate to the invariant violation. Got: " + msg);
        
        assertTrue(resultEvents == null || resultEvents.isEmpty(), "No events should be emitted on failure");
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate a state where the session is considered timed out
        aggregate.markAsTimedOut();
        command = new StartSessionCmd("session-timeout", "TELLER-01", "TERM-01", true);
    }

    // Scenario: StartSessionCmd rejected — Navigation state
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Simulate an incompatible state
        aggregate.markAsIncompatibleState();
        command = new StartSessionCmd("session-nav-fail", "TELLER-01", "TERM-01", true);
    }

    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
