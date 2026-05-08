package com.example.steps;

import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Simulate a valid, authenticated, active session
        String sessionId = "session-123";
        String tellerId = "teller-01";
        Instant now = Instant.now();
        // Use reflection or a test-friendly constructor to set the state to VALID_ACTIVE
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markInitialized(tellerId, now, Duration.ofHours(1)); 
        aggregate.markNavigationState("IDLE"); 
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate ID in the previous step
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertTrue(aggregate.isEnded());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-no-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Keep it uninitialized/anonymous
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timedout";
        String tellerId = "teller-01";
        Instant longAgo = Instant.now().minus(Duration.ofHours(2));
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markInitialized(tellerId, longAgo, Duration.ofMinutes(30)); // Last active 2h ago, timeout 30m
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-bad-nav";
        String tellerId = "teller-01";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markInitialized(tellerId, Instant.now(), Duration.ofHours(1));
        aggregate.markNavigationState("IN_TRANSACTION_FLUX"); // An invalid state for ending session
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We expect an IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        assertNull(resultEvents);
    }
}