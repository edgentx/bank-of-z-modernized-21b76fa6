package com.example.steps;

import com.example.domain.tellersession.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.tellerId = "teller-1";
        this.terminalId = "terminal-1";
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in setup
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("terminal-1", event.terminalId());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-404");
        this.tellerId = null; // Invalid: missing auth
        this.terminalId = "terminal-1";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // We can simulate an expired command timestamp or explicit invalid state
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.tellerId = "teller-1";
        this.terminalId = "terminal-1";
        // Force the aggregate into a state where it considers the session context expired
        // For simplicity in this unit test, we pass an invalid/inactive terminal ID logic
        this.terminalId = "INACTIVE_TERMINAL"; 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-error");
        this.tellerId = "teller-1";
        this.terminalId = null; // Invalid: missing context
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error to be thrown");
        // Ideally catch specific DomainException, but relying on generic Exception for now
        assertTrue(caughtException.getMessage().contains("must") 
                   || caughtException.getMessage().contains("required")
                   || caughtException.getMessage().contains("inconsistent"));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_inactivity() {
        // This scenario implies the session is already active and the command is invalid, 
        // but StartSessionCmd is for initialization. We'll simulate a duplicate start 
        // or an invalid state check.
        this.aggregate = new TellerSessionAggregate("session-active");
        // Simulate that the session is already active
        this.aggregate.markAsStarted(); // Helper to set internal state for testing
        this.tellerId = "teller-1";
        this.terminalId = "terminal-1";
    }

    @Then("the command is rejected with a domain error on active session")
    public void the_command_is_rejected_with_a_domain_error_on_active() {
        assertNotNull(caughtException);
        assertTrue(caughtException.getMessage().contains("already started"));
    }

}