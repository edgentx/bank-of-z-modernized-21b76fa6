package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {
    
    private Aggregate aggregate;
    private String sessionId;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "TS-123";
        // Simulate an authenticated, active, valid session via internal builder/helper
        this.aggregate = TellerSessionAggregate.createActive(sessionId, "teller-01");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is usually part of the aggregate identity or lookup, 
        // here we ensure the command matches the aggregate ID context.
        Assertions.assertNotNull(this.sessionId);
    }

    // --- Scenario 2: Authentication Violation ---
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "TS-UNAUTH";
        // Create an aggregate in a state that indicates no authenticated teller (e.g. null user)
        this.aggregate = TellerSessionAggregate.createUnauthenticated(sessionId);
    }

    // --- Scenario 3: Timeout Violation ---
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "TS-TIMEOUT";
        // Create an aggregate with a last activity time older than allowed timeout
        Duration timeout = Duration.ofMinutes(15);
        Instant expiredActivity = Instant.now().minus(timeout).minusSeconds(1);
        this.aggregate = TellerSessionAggregate.createStale(sessionId, "teller-02", expiredActivity);
    }

    // --- Scenario 4: Navigation State Violation ---
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.sessionId = "TS-NAV-ERR";
        // Create an aggregate where navigation is inconsistent (e.g. stuck in a transaction that doesn't match context)
        this.aggregate = TellerSessionAggregate.createWithNavigationConflict(sessionId, "teller-03");
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(this.sessionId);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        // Domain errors are typically modeled as IllegalStateException or IllegalArgumentException in this pattern
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Exception should be a domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}
