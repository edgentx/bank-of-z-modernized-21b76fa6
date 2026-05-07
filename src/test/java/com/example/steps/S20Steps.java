package com.example.steps;

import com.example.domain.shared.DomainError;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private String sessionId = "test-session-123";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already set in the constructor
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            aggregate.execute(new EndSessionCmd(sessionId));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have emitted events");
        assertTrue(events.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.invalidateNavigationState();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof DomainError, "Exception should be DomainError");
    }
}
