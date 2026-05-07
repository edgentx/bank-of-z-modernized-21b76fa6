package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    // --- Scenario: Successfully execute EndSessionCmd ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initialize to a valid state for the happy path
        aggregate.init("teller-1", true, "HOME", Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already set in the previous step
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        var cmd = new EndSessionCmd(sessionId, "teller-1");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertFalse(aggregate.isActive(), "Session should be terminated");
    }

    // --- Scenario: EndSessionCmd rejected — Auth ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Active but NOT authenticated
        aggregate.init("teller-2", false, "HOME", Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalStateException);
    }

    // --- Scenario: EndSessionCmd rejected — Timeout ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.makeTimedOut();
    }

    // --- Scenario: EndSessionCmd rejected — Navigation State ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "session-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        // Authenticated, but in a state that doesn't allow termination (e.g. mid-transaction)
        aggregate.init("teller-3", true, "TXN_IN_PROGRESS", Instant.now());
    }

}
