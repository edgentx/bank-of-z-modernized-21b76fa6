package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermode.model.EndSessionCmd;
import com.example.domain.tellermode.model.SessionEndedEvent;
import com.example.domain.tellermode.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        // Simulate a prior start event to set up valid state (authenticated, active)
        aggregate.applySessionStarted("session-1", "teller-123", "terminal-A", Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // ID is implicitly provided via the aggregate instance in this pattern
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Do not start the session, leaving it unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        // Start with a timestamp far in the past
        aggregate.applySessionStarted("session-3", "teller-123", "terminal-A", Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-4");
        // Start valid session
        aggregate.applySessionStarted("session-4", "teller-123", "terminal-A", Instant.now());
        // Simulate entering a menu where EndSession is not allowed
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS");
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd("session-1", Instant.now());
            // Adjust cmd ID based on the aggregate context if necessary (simple hack for the demo)
            if (aggregate.id().equals("session-2")) cmd = new EndSessionCmd("session-2", Instant.now());
            else if (aggregate.id().equals("session-3")) cmd = new EndSessionCmd("session-3", Instant.now());
            else if (aggregate.id().equals("session-4")) cmd = new EndSessionCmd("session-4", Instant.now());
            
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // We expect IllegalStateException or IllegalArgumentException based on implementation
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
