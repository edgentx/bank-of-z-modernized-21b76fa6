package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a previous StartSession to make it valid
        aggregate.apply(new SessionStartedEvent(sessionId, "teller-1", "Main Menu", java.time.Instant.now()));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId already set in previous step
        Assertions.assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Do not start session - implies unauthenticated state context for this command check
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.apply(new SessionStartedEvent(sessionId, "teller-1", "Main Menu", java.time.Instant.now().minusSeconds(3600)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "session-nav-error";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.apply(new SessionStartedEvent(sessionId, "teller-1", "INVALID_STATE", java.time.Instant.now()));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Support method for test harness
    public static class SessionStartedEvent implements DomainEvent {
        private final String sessionId;
        private final String tellerId;
        private final String navigationState;
        private final java.time.Instant timestamp;

        public SessionStartedEvent(String id, String tellerId, String state, java.time.Instant ts) {
            this.sessionId = id;
            this.tellerId = tellerId;
            this.navigationState = state;
            this.timestamp = ts;
        }
        @Override public String type() { return "session.started"; }
        @Override public String aggregateId() { return sessionId; }
        @Override public java.time.Instant occurredAt() { return timestamp; }
        public String getTellerId() { return tellerId; }
        public String getNavigationState() { return navigationState; }
    }
}
