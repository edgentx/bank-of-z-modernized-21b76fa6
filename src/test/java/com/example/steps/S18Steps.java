package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-1");
        // Valid state: authenticated, not timed out, valid context
        this.aggregate.setAuthenticated(true);
        this.aggregate.setTimeoutThreshold(Duration.ofMinutes(30));
        this.aggregate.setNavigationContext("HOME");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the 'When' step via Command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the 'When' step via Command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Using static valid IDs for simplicity in this scenario context
            StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-123", "term-456");
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertEquals("session.started", resultEvents.get(0).type());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-2");
        this.aggregate.setAuthenticated(false); // Violation: Not authenticated
        this.aggregate.setTimeoutThreshold(Duration.ofMinutes(30));
        this.aggregate.setNavigationContext("HOME");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Depending on implementation choice, this could be IllegalStateException, IllegalArgumentException, or a custom DomainError
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-3");
        this.aggregate.setAuthenticated(true);
        // Violation: Session has already timed out or is invalid
        // We simulate this by setting the last activity time extremely far back or flagging it timed out
        this.aggregate.markAsTimedOut(); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-4");
        this.aggregate.setAuthenticated(true);
        this.aggregate.setTimeoutThreshold(Duration.ofMinutes(30));
        // Violation: Context is invalid or unknown
        this.aggregate.setNavigationContext("UNKNOWN_CTX");
    }
}
