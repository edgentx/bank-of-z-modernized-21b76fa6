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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state for a fresh aggregate that can start
        aggregate.markAuthenticated();
        aggregate.markValidContext();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-401";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated(); // Violation
        aggregate.markValidContext();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markValidContext();
        aggregate.markTimedOut(); // Violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        String sessionId = "session-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.invalidateContext(); // Violation
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // This step is essentially a no-op in Java, but confirms we have the data ready for the command
        // We construct the command in the 'When' step using these implicit values
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // No-op
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Using hardcoded values for valid inputs per scenario simplicity
        command = new StartSessionCmd(aggregate.id(), "teller-101", "terminal-T1");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected a list of events, but got null");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-101", event.tellerId());
        assertEquals("terminal-T1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException for domain rule violation");
        
        // Verify the error messages match the specific invariants
        String message = thrownException.getMessage();
        assertTrue(
            message.contains("authenticated") || 
            message.contains("timeout") || 
            message.contains("Navigation state"),
            "Error message did not match expected invariant violations: " + message
        );
    }
}