package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Simulating an authenticated teller ID (e.g., via token)
        this.tellerId = "AUTH_teller_001"; 
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "TERM_42";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have produced one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(sessionId, startedEvent.aggregateId());
        assertEquals(tellerId, startedEvent.tellerId());
        assertEquals(terminalId, startedEvent.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-401";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set up a tellerId that represents a failed auth check (missing AUTH_ prefix)
        this.tellerId = "unauthenticated_teller";
        this.terminalId = "TERM_42";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // We use the sessionId to simulate a state flag in the aggregate for this test
        this.sessionId = "session_LOCKED_timed_out"; 
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "AUTH_teller_002";
        this.terminalId = "TERM_42";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "session-nav-error";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "AUTH_teller_003";
        // Simulate invalid context/terminal
        this.terminalId = "INVALID_CTX";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // We accept either IllegalStateException or IllegalArgumentException as a domain error
        assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException), but got: " + capturedException.getClass().getSimpleName()
        );
    }
}