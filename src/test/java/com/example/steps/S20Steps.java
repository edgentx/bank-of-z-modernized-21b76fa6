package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "SESSION-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup: Make it authenticated and idle to satisfy basic invariants
        aggregate.markAuthenticated();
        aggregate.setCurrentScreen("IDLE");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The aggregate ID is the valid SessionId
        this.command = new EndSessionCmd(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "SESSION-INVALID-AUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // DO NOT mark authenticated. Defaults to false.
        this.command = new EndSessionCmd(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "SESSION-INVALID-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setCurrentScreen("IDLE");
        
        // Set last activity to 20 minutes ago (Timeout is 15)
        Instant past = Instant.now().minus(Duration.ofMinutes(20));
        aggregate.setLastActivityAt(past);
        
        this.command = new EndSessionCmd(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "SESSION-INVALID-NAV";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set screen to a non-idle state (e.g. mid-transaction)
        aggregate.setCurrentScreen("CASH_DEPOSIT_ENTRY");
        
        this.command = new EndSessionCmd(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Result events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.ended", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
