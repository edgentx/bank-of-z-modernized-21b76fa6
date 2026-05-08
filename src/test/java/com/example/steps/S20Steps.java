package com.example.steps;

import com.example.domain.shared.DomainEvent;
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
    private EndSessionCmd command;
    private Exception caughtException;
    private String providedSessionId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        providedSessionId = "TS-12345";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Setup a valid state: authenticated, not timed out, valid context
        aggregate.markAuthenticated();
        aggregate.markValidContext();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is set in the previous step, this just confirms intent
        assertNotNull(providedSessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        command = new EndSessionCmd(providedSessionId);
        try {
            aggregate.execute(command);
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
        SessionEndedEvent event = (SessionEndedEvent) events.get(0);
        assertEquals("session.ended", event.type());
    }

    // ---------- Rejection Scenarios ----------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        providedSessionId = "TS-FAIL-AUTH";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Intentionally do NOT call markAuthenticated().
        // But we set a valid context so we don't fail on other checks first
        aggregate.markValidContext();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        providedSessionId = "TS-FAIL-TIMEOUT";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated(); // Must be authenticated to check timeout logic (order of checks in implementation)
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        providedSessionId = "TS-FAIL-CONTEXT";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.markAuthenticated();
        aggregate.markInsecureContext(); // Sets screen to SIGNON
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be a domain error (IllegalStateException)");
        assertFalse(caughtException.getMessage().isBlank(), "Error message should be present");
    }
}