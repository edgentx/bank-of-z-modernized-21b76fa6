package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Standard State
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup: Ensure teller is authenticated (Invariant pre-condition)
        this.aggregate.setAuthenticatedTeller("TELLER_01");
        // Setup: Ensure navigation is at Home (Invariant pre-condition)
        this.aggregate.setNavigationContext("HOME");
        // Setup: Ensure session is active
        this.aggregate.setActive(true);
        this.aggregate.setLastActivity(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the constructor in the previous step
        // This step documents the scenario context
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd("session-123");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        assertEquals("session.ended", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.aggregate = new TellerSessionAggregate("session-invalid-auth");
        // Violation: Teller ID is null/empty
        this.aggregate.setAuthenticatedTeller(null); 
        this.aggregate.setNavigationContext("HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.aggregate.setAuthenticatedTeller("TELLER_02");
        this.aggregate.setNavigationContext("HOME");
        // Violation: Set activity to 20 minutes ago (Timeout is 15)
        this.aggregate.setLastActivity(Instant.now().minus(java.time.Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavContext() {
        this.aggregate = new TellerSessionAggregate("session-bad-nav");
        this.aggregate.setAuthenticatedTeller("TELLER_03");
        this.aggregate.setLastActivity(Instant.now());
        // Violation: Deep in a workflow, not at Home
        this.aggregate.setNavigationContext("CASH_WITHDRAWAL_CONFIRMATION");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
        
        // Optional: Verify specific error messages based on scenario
        String message = thrownException.getMessage();
        assertNotNull(message);
    }

}
