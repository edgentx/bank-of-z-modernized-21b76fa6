package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> events;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Create a valid session with authenticated state and valid navigation
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate initialization by manually setting state (bypassing command for test setup)
        // or use a StartSessionCmd if it existed. We assume valid state for the happy path.
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivity(java.time.Instant.now().minusSeconds(60)); // Active recently
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd("session-123");
        try {
            events = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) events.get(0);
        Assertions.assertEquals("session-123", event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Not authenticated
        aggregate.setNavigationState("IDLE");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.setNavigationState("IDLE");
        // Set last activity to 31 minutes ago (Timeout is 30 mins)
        aggregate.setLastActivity(java.time.Instant.now().minusSeconds(1860));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.markAuthenticated();
        aggregate.setLastActivity(java.time.Instant.now());
        // Simulating an invalid transition or state that prevents logout
        // e.g., "LOCKED" state might require supervisor intervention
        aggregate.setNavigationState("LOCKED_TRANSACTION_IN_PROGRESS");
    }

    // Nested class for convenience if needed, or separate file
    public static class DomainException extends RuntimeException {
        public DomainException(String msg) { super(msg); }
    }
}