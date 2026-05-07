package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermgmt.model.EndSessionCmd;
import com.example.domain.tellermgmt.model.SessionEndedEvent;
import com.example.domain.tellermgmt.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        // Initialize aggregate with valid state: authenticated, active, lastActivity recent, valid nav state
        this.aggregate = new TellerSessionAggregate(sessionId);
        // We simulate a previous session start event to satisfy invariants
        aggregate.applySessionStart("teller-456", Instant.now()); 
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate initialization in previous step
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Aggregate defaults to unauthenticated state (false)
        // Force a state where it seems like it might be active but isn't authenticated
        aggregate.applySessionStart(null, Instant.now()); // Null tellerId violates auth
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.applySessionStart("teller-456", Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "session-bad-nav";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.applySessionStart("teller-456", Instant.now());
        aggregate.applyNavigationState("INVALID_STATE_TO_CASH_WITHDRAWAL"); // Invalid state transition context
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.caughtException = e;
        } catch (UnknownCommandException e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        assertEquals(sessionId, resultEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error (exception)");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}