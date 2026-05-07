package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate previous initialization to make it valid (Authenticated)
        aggregate.replayInitialization("session-123", "teller-456", "Teller 1", true, Instant.now().minusSeconds(60), "Idle");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The ID is implicitly provided by the aggregate instance
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Create a session that is NOT authenticated (or was never properly initialized)
        aggregate.replayInitialization("session-unauth", null, null, false, Instant.now().minusSeconds(60), "Idle");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate a session with a last activity time far in the past (e.g., 4 hours ago)
        aggregate.replayInitialization("session-timeout", "teller-456", "Teller 1", true, Instant.now().minus(Duration.ofHours(4)), "Idle");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        // Valid authenticated session, but stuck in a critical processing state
        aggregate.replayInitialization("session-bad-nav", "teller-456", "Teller 1", true, Instant.now().minusSeconds(10), "CRITICAL_PROCESSING_IN_PROGRESS");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // We accept IllegalStateException (Invariant violation) or IllegalArgumentException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context for logout lock.")
    public void a_TellerSession_aggregate_that_is_locked() {
        aggregate = new TellerSessionAggregate("session-locked");
        aggregate.replayInitialization("session-locked", "teller-456", "Teller 1", true, Instant.now().minusSeconds(10), "LOCKED");
    }
}