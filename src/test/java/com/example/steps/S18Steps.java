package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // State variables for context building
    private boolean isValidContext = true;
    private boolean isAuthenticated = true;
    private boolean isTimedOut = false;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Reset defaults
        isValidContext = true;
        isAuthenticated = true;
        isTimedOut = false;
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Implicitly handled in the command construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Implicitly handled in the command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-401");
        isAuthenticated = false; // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        isTimedOut = true; // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        isValidContext = false; // Trigger null/blank state
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        String navState = isValidContext ? "CASH_DRAWER_OPEN" : "";
        
        Command cmd = new StartSessionCmd(
            aggregate.id(), 
            "teller-01", 
            "term-01", 
            isAuthenticated, 
            isTimedOut, 
            navState
        );

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertTrue(event instanceof SessionStartedEvent);
        
        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals("session-123", started.aggregateId());
        assertEquals("teller-01", started.tellerId());
        assertEquals("term-01", started.terminalId());
        
        // Verify Aggregate State
        assertTrue(aggregate.isActive());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // In this domain model, invariants are enforced by throwing RuntimeExceptions
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
