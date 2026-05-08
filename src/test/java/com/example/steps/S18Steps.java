package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
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
    private Exception capturedException;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context handled in When step for simplicity in this stateless setup
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is provided() {
        // Context handled in When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        var cmd = new StartSessionCmd("session-123", "teller-01", "term-42", true, "CICS");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        var event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-42", event.terminalId());
        assertEquals("session.started", event.type());
    }

    // Scenario: StartSessionCmd rejected — Auth required
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_start_session_cmd_command_is_executed_with_invalid_auth() {
        // isAuthenticated = false
        var cmd = new StartSessionCmd("session-auth-fail", "teller-01", "term-42", false, "CICS");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error regarding auth")
    public void the_command_is_rejected_with_a_domain_error_regarding_auth() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // Scenario: StartSessionCmd rejected — Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate a state where the session is technically active but hasn't been touched in 9 hours
        aggregate.markStale(); 
    }

    @When("the StartSessionCmd command is executed on stale session")
    public void the_start_session_cmd_command_is_executed_on_stale_session() {
        // Trying to "start" or effectively resume a session that has timed out
        var cmd = new StartSessionCmd("session-timeout", "teller-01", "term-42", true, "CICS");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error regarding timeout")
    public void the_command_is_rejected_with_a_domain_error_regarding_timeout() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("timeout"));
    }

    // Scenario: StartSessionCmd rejected — Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void the_start_session_cmd_command_is_executed_with_invalid_nav_state() {
        // Passing invalid context
        var cmd = new StartSessionCmd("session-nav-fail", "teller-01", "term-42", true, "INVALID");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error regarding navigation")
    public void the_command_is_rejected_with_a_domain_error_regarding_navigation() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("Navigation state"));
    }
}
