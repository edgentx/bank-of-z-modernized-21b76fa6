package com.example.steps;

import com.example.domain.ui.model.StartSessionCmd;
import com.example.domain.ui.model.SessionStartedEvent;
import com.example.domain.ui.model.TellerSessionAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> result;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Pre-mark as active (simulating a state where auth is bypassed or already active but invalid)
        aggregate.markActive(); // If it's already active, it can't be started again
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate a session that is already timed out
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Simulate a session with invalid navigation state
        aggregate.markInvalidNavigation();
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "user-123";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "term-456";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(result, "Result list should not be null");
        assertEquals(1, result.size(), "Should emit exactly one event");
        assertTrue(result.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) result.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException, 
                   "Exception should be a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
