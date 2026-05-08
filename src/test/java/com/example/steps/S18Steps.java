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
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in When step via command construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in When step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-123", "terminal-456");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(thrownException, "Should not throw exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // authenticated is false by default
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav");
        aggregate.markAuthenticated();
        aggregate.markInvalidContext();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException");
        assertNull(resultEvents, "No events should be emitted when command is rejected");
    }
}